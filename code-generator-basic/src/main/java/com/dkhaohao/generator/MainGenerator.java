package com.dkhaohao.generator;

import com.dkhaohao.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author dkhaohao
 * @Title: 代码生成器
 * @Package com.dkhaohao.generator
 * @Description: 生成器主方法
 * @date 2024/3/721:33
 */
public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        //创建数据模型
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("dkhaohao");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("结果");

        doGenerator(mainTemplateConfig);
    }
    public static void doGenerator(Object model) throws TemplateException, IOException {
        String projectPath=System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        //输入路径
        String sourcePath=new File(parentFile,"code-generator-demo-projects" + File.separator + "acm-template").getAbsolutePath();
        //生成静态文件
        StaticGenerator.copyFilesByHutool(sourcePath,projectPath);

        //生成动态文件（模版动态生成）
        String dynamicSourcePath=projectPath+File.separator+"src/main/resources/template/MainTemplate.java.ftl";
        String dynamicDestPath=projectPath+File.separator+"acm-template/src/com/yupi/acm/MainTemplate.java";
        DynamicGenerator.doGenerator(dynamicSourcePath,dynamicDestPath,model);



    }

}
