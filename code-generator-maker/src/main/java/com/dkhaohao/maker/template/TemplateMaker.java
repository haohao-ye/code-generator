package com.dkhaohao.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dkhaohao.maker.meta.Meta;
import com.dkhaohao.maker.meta.enums.FileGenerateTypeEnum;
import com.dkhaohao.maker.meta.enums.FileTypeEnum;
import com.dkhaohao.maker.template.model.TemplateMakerConfig;
import com.dkhaohao.maker.template.model.TemplateMakerFileConfig;
import com.dkhaohao.maker.template.model.TemplateMakerModelConfig;
import com.dkhaohao.maker.template.model.TemplateMakerOutputConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author dkhaohao
 * @Date 2024/5/22/9:26
 * @Description : 模版制作器
 */
public class TemplateMaker {

    /**
     * 制作模版
     *
     * @param templateMakerConfig 模版配置
     * @return model id
     */
    public static long makeTemplate(TemplateMakerConfig templateMakerConfig) {
        Meta meta = templateMakerConfig.getMeta();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig templateMakerFileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig templateMakerModelConfig = templateMakerConfig.getModelConfig();
        TemplateMakerOutputConfig templateMakerOutputConfig = templateMakerConfig.getOutputConfig();
        Long id = templateMakerConfig.getId();
        return makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig,templateMakerOutputConfig, id);
    }

    /**
     * 生成模版
     *
     * @param newMeta                  元信息参数
     * @param originalProjectPath      原始项目路径
     * @param templateMakerFileConfig  输入文件配置
     * @param templateMakerModelConfig 模版信息配置
     * @param id                       模版id
     * @return 模版id
     */
    public static long makeTemplate(Meta newMeta,
                                    String originalProjectPath,
                                    TemplateMakerFileConfig templateMakerFileConfig,
                                    TemplateMakerModelConfig templateMakerModelConfig,
                                    TemplateMakerOutputConfig templateMakerOutputConfig,
                                    Long id) {
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
        String sourceRootPath =  FileUtil.loopFiles(new File(templatePath), 1, null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();
//        templatePath + File.separator
//                + FileUtil.getLastPathEle(Paths.get(originalProjectPath)).toString();
        // windows路径转义
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigs = templateMakerFileConfig.getFiles();


        // 生成文件模版,并获取文件信息
        List<Meta.FileConfig.FileInfo> newFileInfos = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath, fileInfoConfigs);
        // 获取模型信息
        List<Meta.ModelConfig.ModelInfo> newModelInfos = getModelInfos(templateMakerModelConfig);
        //生成元信息文件
        String metaOutputpath = templatePath + File.separator + "meta.json";
        //如果meta文件已经存在,则追加修改
        if (FileUtil.exist(metaOutputpath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputpath), Meta.class);
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;
            // 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfos = newMeta.getFileConfig().getFiles();
            fileInfos.addAll(newFileInfos);
            List<Meta.ModelConfig.ModelInfo> modelInfos = newMeta.getModelConfig().getModels();
            modelInfos.addAll(newModelInfos);
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
            modelInfos.addAll(newModelInfos);
        }
        // 去除分组内外同名文件配置
        if(templateMakerOutputConfig!= null){
            if(templateMakerOutputConfig.isRemoveGroupFilesFromRoot()){
                List<Meta.FileConfig.FileInfo> fileInfos = newMeta.getFileConfig().getFiles();
                newMeta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFilesFromRoot(fileInfos));
            }
        }

        // 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputpath);

        return id;
    }

    /**
     * 获取模型信息
     *
     * @param templateMakerModelConfig
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> getModelInfos(TemplateMakerModelConfig templateMakerModelConfig) {
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        // 本次新增的模型配置列表
        List<Meta.ModelConfig.ModelInfo> newModelInfos = new ArrayList<>();
        if (templateMakerModelConfig == null) {
            return newModelInfos;
        }
        if (CollUtil.isEmpty(models)) {
            return newModelInfos;
        }
        // 构造模型配置
        List<Meta.ModelConfig.ModelInfo> inputModelInfos = models
                .stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
                }).collect(Collectors.toList());
        // 如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {
            // 新增分组配置
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelGroupConfig, groupModelInfo);
            // 模型全放到分组文件夹下
            groupModelInfo.setModels(inputModelInfos);
            newModelInfos.add(groupModelInfo);
        } else {
            // 不分组,直接添加
            newModelInfos.addAll(inputModelInfos);
        }
        return newModelInfos;
    }

    /**
     * 生成多个文件模版
     *
     * @param templateMakerFileConfig
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @param fileInfoConfigs
     * @return
     */

    private static List<Meta.FileConfig.FileInfo> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath, List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigs) {
        List<Meta.FileConfig.FileInfo> newFileInfos = new ArrayList<>();
        if (templateMakerFileConfig == null) {
            return newFileInfos;
        }
        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigs = templateMakerFileConfig.getFiles();
        if (CollUtil.isEmpty(fileConfigs)) {
            return newFileInfos;
        }
        // 二 使用字符串替换,生成模版文件
        // 遍历输入文件
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigs) {
            String inputFilePath = fileInfoConfig.getPath();
            // 输入路径为相对路径,则拼接绝对路径
            if (!inputFilePath.startsWith(sourceRootPath)) {
                inputFilePath = sourceRootPath + File.separator + inputFilePath;
            }
            // 获取过滤后的文件列表(不会存在目录)
            List<File> files = FileFilter.doFilter(inputFilePath, fileInfoConfig.getFileFilterConfigs());
            // 不处理.ftl文件
            files = files.stream().filter(file -> !file.getAbsolutePath().endsWith(".ftl")).collect(Collectors.toList());
            for (File file : files) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(templateMakerModelConfig, sourceRootPath, file, fileInfoConfig);
                newFileInfos.add(fileInfo);
            }

        }

        // 三 生成配置文件
        // 如果是文件组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (fileGroupConfig != null) {
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();
            String condition = fileGroupConfig.getCondition();

            // 新增分组配置
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());

            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setCondition(condition);
            //文件全放到分组文件夹下
            groupFileInfo.setFiles(newFileInfos);
            newFileInfos = new ArrayList<>();
            newFileInfos.add(groupFileInfo);
        }
        return newFileInfos;
    }


    /**
     * 生成文件模版
     *
     * @param templateMakerModelConfig 模版信息
     * @param sourceRootPath           原始项目路径
     * @param inputFile                输入文件
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(
            TemplateMakerModelConfig templateMakerModelConfig,
            String sourceRootPath,
            File inputFile,
            TemplateMakerFileConfig.FileInfoConfig fileInfoConfig) {
        // 要挖坑的文件绝对路径 (用于制作模版)
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 文件输入输出路径(用于生成配置)
        String fileInputpath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputpath + ".ftl";

        //使用字符串替换,生成模版文件
        String fileContent;
        // 如果模版文件已经存在,在基础模版上追加
        if (FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        //对同一个文件内容,遍历模型多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            //不是分组,则直接替换
            if (modelGroupConfig == null) {
                replacement = String.format("${%s}", modelInfoConfig.getFieldName());
            } else {
                replacement = String.format("${%s.%s}", modelGroupConfig.getGroupKey(), modelInfoConfig.getFieldName());
            }
            //多次替换
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        // meta文件路径输入输出路径反转
        fileInfo.setInputPath(fileOutputPath);
        fileInfo.setOutputPath(fileInputpath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
        fileInfo.setCondition(fileInfoConfig.getCondition());


        //如果已有模版文件,则在模版基础上再挖
        boolean existTemplateFile = FileUtil.exist(fileOutputAbsolutePath);
        // 是否更改了文件内容
        boolean contentEquals = newFileContent.equals(fileContent);
        //不存在模版文件,并且文件内容没有变化,则为静态文件
        if (!existTemplateFile) {
            if (contentEquals) {
                fileInfo.setInputPath(fileInputpath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            } else {
                FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);

            }
        } else if (!contentEquals) {
            //存在模版文件,并且文件内容有变化,则覆盖
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
        //同分组合并,不同分组保留
        // 先分组
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfosMap = fileInfos
                .stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));
        // 再合并
        // 去掉同分组重复文件配置
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfosMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileInfos = entry.getValue();
            ArrayList<Meta.FileConfig.FileInfo> newFileInfos = new ArrayList<>(tempFileInfos
                    .stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())//轧平文件列表 {groudKey:xxx,groupName:xxx,condition:xxx,files:[{file1},{file2}]} -> {file1,file2}
                    .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r))
                    .values());

            // 使用新的group配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfos);
            newFileInfo.setFiles(newFileInfos);
            groupKeyMergedFileInfoMap.put(entry.getKey(), newFileInfo);
        }
        // 将文件分组添加到结果列表
        ArrayList<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        // 将未分组的文件添到结果列表
        List<Meta.FileConfig.FileInfo> noGroupFileInfos = fileInfos
                .stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey())).collect(Collectors.toList());

        resultList.addAll(new ArrayList<>(noGroupFileInfos.stream()
                .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)).values()
        ));


        return resultList;

    }


    /**
     * 去重模型配置信息
     *
     * @param modelInfos
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModelInfos(List<Meta.ModelConfig.ModelInfo> modelInfos) {
        // 同分组合并,不同分组保留

        // 有分组
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfosMap = modelInfos
                .stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));

        // 同分组内模型合并
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfosMap.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tempModelInfos = entry.getValue();
            List<Meta.ModelConfig.ModelInfo> newModelInfos = new ArrayList<>(tempModelInfos
                    .stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r))
                    .values()
            );
            // 使用新的group配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfos);
            newModelInfo.setModels(newModelInfos);
            groupKeyMergedModelInfoMap.put(entry.getKey(), newModelInfo);

        }
        // 将模型分组添加到结果列表
        ArrayList<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());

        // 将未分组的模型添到结果列表
        List<Meta.ModelConfig.ModelInfo> noGroupModelInfos = modelInfos
                .stream()
                .filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList());

        resultList.addAll(new ArrayList<>(noGroupModelInfos.stream()
                .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values()
        ));
        return resultList;

    }
}
