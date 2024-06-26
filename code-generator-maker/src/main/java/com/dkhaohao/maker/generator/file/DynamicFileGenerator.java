package com.dkhaohao.maker.generator.file;


import cn.hutool.core.io.FileUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author dkhaohao
 * @Description: 动态文件生成
 * @date 2024/3/716:35
 */
public class DynamicFileGenerator {
    /**
     *
     * @param relativeInputPath 模版文件相对路径
     * @param destPath 生成文件路径
     * @param model
     * @throws IOException IO异常
     * @throws TemplateException 创建模版异常
     */
    public static void doGenerator(String relativeInputPath, String destPath, Object model) throws IOException, TemplateException {
        //创建Configuration对象，参数为FreeMarker版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        configuration.setDefaultEncoding("utf-8");
        configuration.setNumberFormat("0.######");


        //类路径获取模版文件路径
        int lastSplitIndex = relativeInputPath.lastIndexOf("/");
        String basePackagePath= relativeInputPath.substring(0, lastSplitIndex);
        String templateName = relativeInputPath.substring(lastSplitIndex + 1);

        ClassTemplateLoader classTemplateLoader = new ClassTemplateLoader(DynamicFileGenerator.class, basePackagePath);
        configuration.setTemplateLoader(classTemplateLoader);


        //创建模版对象
        Template template = configuration.getTemplate(templateName);

        //如果文件不存在则创建文件
        if(!FileUtil.exist(destPath)){
            //新建文件
            FileUtil.touch(destPath);
        }


        //生成
        Writer out = new FileWriter(destPath);
        template.process(model, out);

//        关闭资源
        out.close();

    }

    /**
     *
     * @param sourcePath 模版文件路径
     * @param destPath 生成文件路径
     * @param model
     * @throws IOException IO异常
     * @throws TemplateException 创建模版异常
     */
    public static void doGeneratorByPath(String sourcePath, String destPath, Object model) throws IOException, TemplateException {
        //创建Configuration对象，参数为FreeMarker版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        configuration.setDefaultEncoding("utf-8");
        configuration.setNumberFormat("0.######");


        //指定模版文件所在路径
        File templateDir = new File(sourcePath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);


        //创建模版对象
        Template template = configuration.getTemplate(new File(sourcePath).getName());

        //如果文件不存在则创建文件
        if(!FileUtil.exist(destPath)){
            //新建文件
            FileUtil.touch(destPath);
        }


        //生成
        Writer out = new FileWriter(destPath);
        template.process(model, out);

//        关闭资源
        out.close();

    }


}
