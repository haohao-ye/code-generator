package com.dkhaohao.maker.generator.file;

import com.dkhaohao.maker.model.DataModel;
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
public class FileGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        //创建数据模型
        DataModel dataModel = new DataModel();
        dataModel.setAuthor("dkhaohao");
        dataModel.setLoop(false);
        dataModel.setOutputText("结果");

        doGenerator(dataModel);
    }

    /**
     * 生成目标代码文件的方法
     * @param model 模版模型数据
     * @throws TemplateException 模版异常
     * @throws IOException IO异常
     */
    public static void doGenerator(Object model) throws TemplateException, IOException {
        String sourceRootPath="C:\\Users\\86135\\Desktop\\Project-Practice\\code-generator\\code-generator-demo-projects\\acm-template";
        String destRootPath="C:\\Users\\86135\\Desktop\\Project-Practice\\code-generator";

        String sourcePath;
        String destPath;

        sourcePath=new File(sourceRootPath,"src/main/resources/template/MainTemplate.java.ftl").getAbsolutePath();
        destPath=new File(destRootPath,"acm-template/src/com/yupi/acm/MainTemplate.java").getAbsolutePath();
        DynamicFileGenerator.doGenerator(sourcePath,destPath,model);

        sourcePath=new File(sourceRootPath,".gitignore").getAbsolutePath();
        destPath=new File(destRootPath,"acm-template/.gitignore").getAbsolutePath();
        StaticFileGenerator.copyFilesByHutool(sourcePath,destPath);

        sourcePath=new File(sourceRootPath,"README.md").getAbsolutePath();
        destPath=new File(destRootPath,"acm-template/README.md").getAbsolutePath();
        StaticFileGenerator.copyFilesByHutool(sourcePath,destPath);







    }

}
