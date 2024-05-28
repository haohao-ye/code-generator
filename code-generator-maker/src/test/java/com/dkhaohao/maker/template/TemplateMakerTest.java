package com.dkhaohao.maker.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.dkhaohao.maker.meta.Meta;
import com.dkhaohao.maker.template.enums.FileFilterRangeEnum;
import com.dkhaohao.maker.template.enums.FileFilterRuleEnum;
import com.dkhaohao.maker.template.model.FileFilterConfig;
import com.dkhaohao.maker.template.model.TemplateMakerConfig;
import com.dkhaohao.maker.template.model.TemplateMakerFileConfig;
import com.dkhaohao.maker.template.model.TemplateMakerModelConfig;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @Author dkhaohao
 * @Date 2024/5/23/17:41
 * @Description :
 */
public class TemplateMakerTest {

    @Test
    public void main() {
    }
    @Test
    public void testMakeTemplate() {
        Meta newMeta = new Meta();
        newMeta.setName("acm-template-generator");
        newMeta.setDescription("ACM模版生成器");

        String projectPath = System.getProperty("user.dir");
        System.out.println("当前项目路径：" + projectPath);
        String originalProjectPath = new File(projectPath).getParent()
                + File.separator + "code-generator-demo-projects/springboot-init";
        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";
        String inputFilePath2 = "src/main/resources/application.yml";

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        // -模型组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);


        // -模型信息配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("host");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig3 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig3.setFieldName("className");
        modelInfoConfig3.setType("String");
        modelInfoConfig3.setReplaceText("BaseResponse");


        templateMakerModelConfig.setModels(Arrays.asList(modelInfoConfig1, modelInfoConfig2, modelInfoConfig3));


        //替换变量(第一次)
//        String searchStr = "Sum: ";
        //替换变量(第二次)
        String searchStr = "BaseResponse";
        //构建文件过滤配置
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        List<FileFilterConfig> fileFilterConfigs = new ArrayList<>();
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();

        fileFilterConfigs.add(fileFilterConfig);
        fileInfoConfig1.setFileFilterConfigs(fileFilterConfigs);

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputFilePath2);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1, fileInfoConfig2));

        // 分组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setGroupKey("outputText");
        fileGroupConfig.setGroupName("test");
        fileGroupConfig.setCondition("测试分组");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

        // 模版生成
//        long id = TemplateMaker.makeTemplate(newMeta, originalProjectPath, templateMakerFileConfig, templateMakerModelConfig, 1793571199779880960l);
//
//        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1));
//        System.out.println("模版生成成功,id为：" + id);

    }
    @Test
    public void testMakeTemplate2() {
        String rootPath = "example/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println("模版生成成功,id为：" + id);
    }

    @Test
    public void makeSpringbootTemplate() {
        String rootPath = "example/springboot-init/";
        String configStr ;
        TemplateMakerConfig templateMakerConfig ;
        long id ;
         configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker.json");
         templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
         id = TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker1.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        id = TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker2.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        id = TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker3.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        id = TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker4.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        id = TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker5.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        id = TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker6.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        id = TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker7.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        id = TemplateMaker.makeTemplate(templateMakerConfig);


        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker8.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        id = TemplateMaker.makeTemplate(templateMakerConfig);

        System.out.println("模版生成成功,id为：" + id);
    }
}