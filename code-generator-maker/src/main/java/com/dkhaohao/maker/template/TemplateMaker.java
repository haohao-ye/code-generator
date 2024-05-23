package com.dkhaohao.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dkhaohao.maker.meta.Meta;
import com.dkhaohao.maker.meta.enums.FileGenerateTypeEnum;
import com.dkhaohao.maker.meta.enums.FileTypeEnum;
import com.dkhaohao.maker.template.enums.FileFilterRangeEnum;
import com.dkhaohao.maker.template.enums.FileFilterRuleEnum;
import com.dkhaohao.maker.template.model.FileFilterConfig;
import com.dkhaohao.maker.template.model.TemplateMakerFileConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author dkhaohao
 * @Date 2024/5/22/9:26
 * @Description : 模版制作器
 */
public class TemplateMaker {
    public static void main(String[] args) {
        Meta newMeta = new Meta();
        newMeta.setName("acm-template-generator");
        newMeta.setDescription("ACM模版生成器");

        String projectPath = System.getProperty("user.dir");
        System.out.println("当前项目路径：" + projectPath);
        String originalProjectPath = new File(projectPath).getParent()
                + File.separator + "code-generator-demo-projects/springboot-init";
        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";
        String inputFilePath2 = "src/main/java/com/yupi/springbootinit/controller";
        List<String> inputFilePaths = new ArrayList<>();
        inputFilePaths.add(inputFilePath1);
        inputFilePaths.add(inputFilePath2);

        // 模版信息
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("sum = ");
        // 模版信息
        Meta.ModelConfig.ModelInfo modelInfo2 = new Meta.ModelConfig.ModelInfo();
        modelInfo2.setFieldName("className");
        modelInfo2.setType("String");

        //替换变量
        String searchStr = "Sum: ";
        String searchStr2 = "BaseResponse";
        //构建文件过滤配置
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig0 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig0.setPath(inputFilePath1);
        List<FileFilterConfig> fileFilterConfigs = new ArrayList<>();
        // 过滤文件名包含Base的文件(只对该文件夹下的文件进行过滤)
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        ;fileFilterConfigs.add(fileFilterConfig);
        fileInfoConfig0.setFileFilterConfigs(fileFilterConfigs);
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        //不过滤
        fileInfoConfig1.setPath(inputFilePath2);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig0, fileInfoConfig1));

        long id = makeTemplate(newMeta, originalProjectPath, templateMakerFileConfig, modelInfo2, searchStr2, null);
        System.out.println("模版生成成功,id为：" + id);

    }

    /**
     * 生成模版
     *
     * @param newMeta                 元信息参数
     * @param originalProjectPath     原始项目路径
     * @param templateMakerFileConfig 输入文件配置
     * @param modelInfo               模版信息
     * @param searchStr               待替换字符串
     * @param id                      模版id
     * @return
     */
    public static long makeTemplate(Meta newMeta, String originalProjectPath, TemplateMakerFileConfig templateMakerFileConfig, Meta.ModelConfig.ModelInfo modelInfo, String searchStr, Long id) {
        //没有id则生成

        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }
        //复制目录
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;
        if (!FileUtil.exist(tempDirPath)) {
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originalProjectPath, templatePath, true);
        }

        // 是否为首次生成
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originalProjectPath, templatePath, true);
        }

        // 一 输入信息
        //输入文件信息
        String sourceRootPath = templatePath + File.separator
                + FileUtil.getLastPathEle(Paths.get(originalProjectPath)).toString();
        // windows路径转义
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigs = templateMakerFileConfig.getFiles();


        // 二 使用字符串替换,生成模版文件
        // 遍历输入文件
        List<Meta.FileConfig.FileInfo> newFileInfos = new ArrayList<>();
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigs) {
            String inputFilePath = fileInfoConfig.getPath();
            // 输入路径为相对路径,则拼接绝对路径
            if (!inputFilePath.startsWith(sourceRootPath)) {
                inputFilePath = sourceRootPath + File.separator + inputFilePath;
            }
            // 获取过滤后的文件列表(不会存在目录)
            List<File> files = FileFilter.doFilter(inputFilePath, fileInfoConfig.getFileFilterConfigs());
            for (File file : files) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath, file);
                newFileInfos.add(fileInfo);
            }
//            String inputFileAbsolutePath = sourceRootPath + File.separator + inputFilePath;
/*            // 输入的是目录
            if (FileUtil.isDirectory(inputFileAbsolutePath)) {
                List<File> files = FileUtil.loopFiles(inputFileAbsolutePath);
                for (File file : files) {
                    Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath, file);
                    newFileInfos.add(fileInfo);
                }
            } else {
                // 输入的是文件
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath, new File(inputFileAbsolutePath));
                newFileInfos.add(fileInfo);
            }*/
        }

        // 三 生成配置文件
        String metaOutputpath = sourceRootPath + File.separator + "meta.json";
        //如果meta文件已经存在,则追加修改
        if (FileUtil.exist(metaOutputpath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputpath), Meta.class);
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;
            // 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfos = newMeta.getFileConfig().getFiles();
            fileInfos.addAll(newFileInfos);
            List<Meta.ModelConfig.ModelInfo> modelInfos = newMeta.getModelConfig().getModels();
            modelInfos.add(modelInfo);
            // 去重
            newMeta.getFileConfig().setFiles(distinctFileInfos(fileInfos));
            newMeta.getModelConfig().setModels(distinctModelInfos(modelInfos));

        } else {
            // 构造配置参数
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfos = new ArrayList<>();
            fileConfig.setFiles(fileInfos);
            fileInfos.addAll(newFileInfos);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfos = new ArrayList<>();
            modelConfig.setModels(modelInfos);
            modelInfos.add(modelInfo);
        }

        // 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputpath);

        return id;
    }

    /**
     * 生成文件模版
     *
     * @param modelInfo      模版信息
     * @param searchStr      待替换字符串
     * @param sourceRootPath 原始项目路径
     * @param inputFile      输入文件
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(
            Meta.ModelConfig.ModelInfo modelInfo,
            String searchStr,
            String sourceRootPath,
            File inputFile) {
        // 要挖坑的文件绝对路径 (用于制作模版)
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 文件输入输出路径(用于生成配置)
        String fileInputpath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputpath + ".ftl";

        //使用字符串替换,生成模版文件
        String fileContent = null;
        if (FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputpath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        // 和源文件一致,则为静态文件
        if (newFileContent.equals(fileContent)) {
            fileInfo.setOutputPath(fileInputpath);
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        } else {
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }

        return fileInfo;

    }


    /**
     * 去重文件配置信息
     *
     * @param fileInfos
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> distinctFileInfos(List<Meta.FileConfig.FileInfo> fileInfos) {

        return new ArrayList<>(fileInfos.stream()
                .collect(
                        Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)
                ).values()
        );

    }

    /**
     * 去重模型配置信息
     *
     * @param modelInfos
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModelInfos(List<Meta.ModelConfig.ModelInfo> modelInfos) {

        return new ArrayList<>(modelInfos.stream()
                .collect(
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
                ).values()
        );

    }
}
