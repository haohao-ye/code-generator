package com.dkhaohao.generator;


import com.dkhaohao.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/716:35
 */
public class DynamicGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        String projectPath = System.getProperty("user.dir");
        String soucePath=projectPath+File.separator+"src/main/resources/template/MainTemplate.java.ftl";
        String destPath=projectPath+File.separator+"MainTemplate.java";
        //创建数据模型
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("dkhaohao");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("结果");
        doGenerator(soucePath,destPath,mainTemplateConfig);


    }

    public static void doGenerator(String sourcePath, String destPath, Object model) throws IOException, TemplateException {
        //创建Configuration对象，参数为FreeMarker版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        configuration.setDefaultEncoding("utf-8");
        configuration.setNumberFormat("0.######");


        //指定模版文件所在路径
        File templateDir = new File(sourcePath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);


        //创建模版对象
        Template template = configuration.getTemplate(new File(sourcePath).getName());




        //生成
        Writer out = new FileWriter(destPath);
        template.process(model, out);

//        关闭资源
        out.close();

    }


}
