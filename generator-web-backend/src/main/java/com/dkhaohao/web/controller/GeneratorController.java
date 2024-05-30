package com.dkhaohao.web.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.*;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dkhaohao.maker.generator.main.ZipGenerator;
import com.dkhaohao.maker.meta.Meta;
import com.dkhaohao.maker.meta.MetaValidator;
import com.dkhaohao.web.annotation.AuthCheck;
import com.dkhaohao.web.common.BaseResponse;
import com.dkhaohao.web.common.DeleteRequest;
import com.dkhaohao.web.common.ErrorCode;
import com.dkhaohao.web.common.ResultUtils;
import com.dkhaohao.web.constant.UserConstant;
import com.dkhaohao.web.exception.BusinessException;
import com.dkhaohao.web.exception.ThrowUtils;
import com.dkhaohao.web.manager.CacheManager;
import com.dkhaohao.web.manager.CosManager;
import com.dkhaohao.web.model.dto.generator.*;
import com.dkhaohao.web.model.entity.Generator;
import com.dkhaohao.web.model.entity.User;
import com.dkhaohao.web.model.vo.GeneratorVO;
import com.dkhaohao.web.service.GeneratorService;
import com.dkhaohao.web.service.UserService;
import com.dkhaohao.web.utils.CacheKey;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 帖子接口
 *
 * @author dkhaohao
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheManager cacheManager;

    @PostMapping("/make")
    public void makeGenerator(
            @RequestBody GeneratorMakeRequest generatorMakeRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // 输入参数
        String zipFilePath = generatorMakeRequest.getZipFilePath();
        Meta meta = generatorMakeRequest.getMeta();

        // 获取登录用户
        User loginUser = userService.getLoginUser(request);

        // 创建独立工作空间
        if (StrUtil.isBlank(zipFilePath)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "压缩包不存在");
        }
        String projectPath = System.getProperty("user.dir");
        String id = IdUtil.getSnowflakeNextId() + RandomUtil.randomString(6);
        String tempDirPath = String.format("%s/.temp/make/%s", projectPath, id);
        String localZipFilePath = tempDirPath + "dist.zip";

        //新建文件
        if (!FileUtil.exist(localZipFilePath)) {
            FileUtil.touch(localZipFilePath);
        }

        try {
            cosManager.download(zipFilePath, localZipFilePath);
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载压缩包失败");
        }


        // 解压压缩包,得到模版文件
        File unzipDistDir = ZipUtil.unzip(localZipFilePath);

        // 构造meta对象和输出路径
        String sourceRootPath = unzipDistDir.getAbsolutePath();
        meta.getFileConfig().setSourceRootPath(sourceRootPath);
        MetaValidator.doValidAndFill(meta);
        String outputPath = String.format("%s/generated/%s", tempDirPath, meta.getName());

        // 调用maker方法制作生成器
        ZipGenerator generateTemplate = new ZipGenerator();
        try {
            generateTemplate.doGenerate(meta, outputPath);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器制作失败");
        }

        // 下载压缩包
        String suffix = "-dist.zip";
        String zipFileName = meta.getName() + suffix;
        String distZipFilePath = outputPath + suffix;

        //下载文件
        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + zipFileName);
        //写入响应
        Files.copy(Paths.get(distZipFilePath), response.getOutputStream());

        // 删除临时文件
        CompletableFuture.runAsync(() -> {
            FileUtil.del(tempDirPath);
        });


    }


    /**
     * 使用代码生成器
     *
     * @param generatorUseRequest
     * @param request
     * @param response
     */
    @PostMapping("/use")
    public void useGenerator(
            @RequestBody GeneratorUseRequest generatorUseRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // 获取用户输入的请求参数
        Long id = generatorUseRequest.getId();
        Map<String, Object> dataModel = generatorUseRequest.getDataModel();

        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        log.info("用户 {} 使用代码生成器 id:{}, dataModel:{}", loginUser, id, dataModel);

        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 生成器的存储路径
        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        //从对象存储下载生成器的压缩包
        //定义独立的工作空间
        String projectPath = System.getProperty("user.dir");
        // 必须要用 userId 区分，否则可能会导致输入参数文件冲突
        String tempDirPath = String.format("%s/.temp/use/%s%s", projectPath, id, loginUser.getId());
        String zipFilePath = tempDirPath + "/dist.zip";

        if (!FileUtil.exist(zipFilePath)) {
            FileUtil.touch(zipFilePath);
        }

        try {
            cosManager.download(distPath, zipFilePath);
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载生成器失败");
        }
        // 解压生成器压缩包
        File unzipDistDir = ZipUtil.unzip(zipFilePath);

        // 将用户输入的参数写入到json文件中
        String dataModelFilePath = tempDirPath + "/dataModel.json";
        String jsonStr = JSONUtil.toJsonStr(dataModel);
        FileUtil.writeUtf8String(jsonStr, dataModelFilePath);

        // 执行脚本
        //windows使用"generator.bat" linux使用"generator"
        File scriptFile = FileUtil.loopFiles(unzipDistDir, 2, null)
                .stream()
                .filter(file -> file.isFile() && "generator".equals(file.getName()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "生成器脚本不存在"));

        // 添加可执行权限

        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(scriptFile.toPath(), permissions);
        } catch (Exception e) {

        }

        File scriptDir = scriptFile.getParentFile();
        // windows
//        String scriptAbsolutPath = scriptFile.getAbsolutePath().replace("\\", "/");
//        String[] commands = {scriptAbsolutPath, "json-generate", "--file=" + dataModelFilePath};

        // linux
        String scriptAbsolutPath = scriptFile.getAbsolutePath();
        String[] commands = {"./generator", "json-generate", "--file=" + dataModelFilePath};

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(scriptDir);
        try {
            Process process = processBuilder.start();

            // 读取命令行输出
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.info(line);
            }
            // 等待命令执行完成
            int exitCode = process.waitFor();
            log.info("命令执行完成，exitCode: {}", exitCode);

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器执行失败");
        }

        // 压缩得到的生成结果,返回给前端
        String generatedPath = scriptDir.getAbsolutePath() + "/generated";
        String resultPath = tempDirPath + "/result.zip";
        File resultFile = ZipUtil.zip(generatedPath, resultPath);

        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + resultFile.getName());
        Files.copy(resultFile.toPath(), response.getOutputStream());

        // 删除临时文件
        CompletableFuture.runAsync(() -> {
            FileUtil.del(tempDirPath);
        });

    }

    /**
     * 根据id下载
     *
     * @param id
     * @return
     */
    @GetMapping("/download}")
    public void downloadGeneratorById(long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);

        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String filePath = generator.getDistPath();
        if (StrUtil.isBlank(filePath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        //追踪时间
        log.info("用户 {} 下载文件 {}", loginUser, filePath);
        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + filePath);
        // 优先从缓存读取
        String cacheFilePath = getCacheFilePath(id, filePath);
        if (FileUtil.exist(cacheFilePath)) {
            Files.copy(Paths.get(cacheFilePath), response.getOutputStream());
            return;
        }

        COSObjectInputStream cosObjectInput = null;

        try {

/*            //流式传输
            try (OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[1024 * 8];
                int len = 0;
                while ((len = cosObjectInput.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
            }catch (Exception e){
                log.error("file download error, filePath:{}, e:{}", filePath, e);
            }*/
            COSObject cosObject = cosManager.getObject(filePath);
            cosObjectInput = cosObject.getObjectContent();

            // 输出文件流(从对象存储下载)
            byte[] buffer = IOUtils.toByteArray(cosObjectInput);
            // 写入响应
            response.getOutputStream().write(buffer);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filePath:{}, e:{}", filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }


    /**
     * 创建
     *
     * @param generatorAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest generatorAddRequest, HttpServletRequest request) {
        if (generatorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorAddRequest, generator);
        List<String> tags = generatorAddRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorAddRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorAddRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));

        generatorService.validGenerator(generator, true);
        User loginUser = userService.getLoginUser(request);
        generator.setUserId(loginUser.getId());
        generator.setStatus(0);
        boolean result = generatorService.save(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newGeneratorId = generator.getId();
        return ResultUtils.success(newGeneratorId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = generatorService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param generatorUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateGenerator(@RequestBody GeneratorUpdateRequest generatorUpdateRequest) {
        if (generatorUpdateRequest == null || generatorUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorUpdateRequest, generator);
        List<String> tags = generatorUpdateRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorUpdateRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorUpdateRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        // 参数校验
        generatorService.validGenerator(generator, false);
        long id = generatorUpdateRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<GeneratorVO> getGeneratorVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(generatorService.getGeneratorVO(generator, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param generatorQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Generator>> listGeneratorByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        Page<Generator> postPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(postPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                 HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> postPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(postPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listMyGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                   HttpServletRequest request) {
        if (generatorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        generatorQueryRequest.setUserId(loginUser.getId());
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> postPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(postPage, request));
    }


    /**
     * 编辑（用户）
     *
     * @param generatorEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editGenerator(@RequestBody GeneratorEditRequest generatorEditRequest, HttpServletRequest request) {
        if (generatorEditRequest == null || generatorEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorEditRequest, generator);
        List<String> tags = generatorEditRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorEditRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorEditRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        // 参数校验
        generatorService.validGenerator(generator, false);
        User loginUser = userService.getLoginUser(request);
        long id = generatorEditRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldGenerator.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 根据id下载
     *
     * @param generatorCacheRequest
     * @return
     */
    @GetMapping("/cache}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void cacheGenerator(@RequestBody GeneratorCacheRequest generatorCacheRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (ObjectUtil.isEmpty(generatorCacheRequest) || generatorCacheRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        Long id = generatorCacheRequest.getId();
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String filePath = generator.getDistPath();
        if (StrUtil.isBlank(filePath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        String zipFilePath = getCacheFilePath(id, filePath);

        //新建文件
        if (!FileUtil.exist(zipFilePath)) {
            FileUtil.touch(zipFilePath);
        }

        try {
            cosManager.download(filePath, zipFilePath);
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载生成器失败");
        }


    }

    /**
     * 快速获取分页列表
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo/fast")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPageFast(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                     HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 优先从缓存读取
        // 多级缓存
        String pageCacheKey = CacheKey.getPageCacheKey(generatorQueryRequest);
        String cacheValue = cacheManager.get(pageCacheKey);
        if (cacheValue != null) {
            Page<GeneratorVO> page = JSONUtil.toBean(cacheValue, new TypeReference<Page<GeneratorVO>>() {
            },false);
            return ResultUtils.success(page);
        }
//        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
//        String cacheValue = valueOperations.get(pageCacheKey);
//        if (StrUtil.isNotBlank(cacheValue)) {
//            Page<GeneratorVO> page = JSONUtil.toBean(cacheValue, new TypeReference<Page<GeneratorVO>>() {
//            },false);
//            return ResultUtils.success(page);
//        }

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Generator> queryWrapper = generatorService.getQueryWrapper(generatorQueryRequest);
        // 仅返回必要字段,减少查询压力和传输数据量提高响应速度
        queryWrapper.select("id", "name", "description", "tags", "status","picture", "createTime", "updateTime","userId");
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size), queryWrapper);
        Page<GeneratorVO> generatorVOPage = generatorService.getGeneratorVOPage(generatorPage, request);

        // 缓存
        cacheManager.put(pageCacheKey, JSONUtil.toJsonStr(generatorVOPage));
        return ResultUtils.success(generatorVOPage);

    }

    /**
     * 获取缓存路径
     *
     * @param id
     * @param filePath
     * @return
     */
    private String getCacheFilePath(Long id, String filePath) {
        String projectPath = System.getProperty("user.dir");
        String tempPath = String.format("%s.temp/cache/%s", projectPath, id);
        String zipFilePath = String.format("%s/%s", tempPath, filePath);
        return zipFilePath;

    }


}
