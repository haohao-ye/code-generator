package com.dkhaohao.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.dkhaohao.maker.generator.file.DynamicFileGenerator;
import com.dkhaohao.maker.generator.main.GenerateTemplate;
import com.dkhaohao.maker.meta.Meta;
import com.dkhaohao.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/1112:11
 */
public class MainGenerator extends GenerateTemplate {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        //获取Meta类
/*        Meta meta = MetaManager.getMeta();

        //输出路径
        String projectPath = System.getProperty("user.dir");
        String destPath = projectPath + File.separator + "generated";

        if (!FileUtil.exist(destPath)) {
            FileUtil.mkdir(destPath);
        }

        //从原始模版文件路径复制到生成的代码包中
        String sourceRootPath=meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath=destPath+File.separator+".source";
        FileUtil.copy(sourceRootPath,sourceCopyDestPath,false);

        //读取Resource目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String resourceAbsolutePath = classPathResource.getAbsolutePath();

        //java 包基础路径
        String basePackage = meta.getBasePackage();
        //将com.dkhaohao换成com/dkhaohao
        String destBasePackagePath = StrUtil.join("/", StrUtil.split(basePackage, "."));
        String destJavaPath = destPath + File.separator + "src/main/java" + File.separator + destBasePackagePath;


        String sourceModelPath;
        String destModelPath;
        //model.DataModel
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/java/model/DataModel.java.ftl";
        destModelPath = destJavaPath + File.separator + "model/DataModel.java";

        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);

        //model.command.ConfigCommand
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/java/cli/command/ConfigCommand.java.ftl";
        destModelPath = destJavaPath + File.separator + "cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);

        //model.command.GenerateCommand
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/java/cli/command/GenerateCommand.java.ftl";
        destModelPath = destJavaPath + File.separator + "cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);

        //model.command.ListCommand
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/java/cli/command/ListCommand.java.ftl";
        destModelPath = destJavaPath + File.separator + "cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);

        //model.command.CommandExecutor
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/java/cli/CommandExecutor.java.ftl";
        destModelPath = destJavaPath + File.separator + "cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);

        //command.Main
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/java/Main.java.ftl";
        destModelPath = destJavaPath + File.separator + "Main.java";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);

        //generator.DynamicGenerator
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/java/generator/DynamicFileGenerator.java.ftl";
        destModelPath = destJavaPath + File.separator + "generator/DynamicFileGenerator.java";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);
        //generator.StaticGenerator
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/java/generator/StaticFileGenerator.java.ftl";
        destModelPath = destJavaPath + File.separator + "generator/StaticFileGenerator.java";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);
        //generator.fileGenerator
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/java/generator/FileGenerator.java.ftl";
        destModelPath = destJavaPath + File.separator + "generator/FileGenerator.java";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);

        //pom.xml
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/pom.xml.ftl";
        destModelPath = destPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);

        //README.md
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/README.md.ftl";
        destModelPath = destPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);

        //构建jar包
        JarGenerator.doGenerate(destPath);

        //封装脚本
        String shellDestPath=destPath+File.separator+"generator";
        String jarName=String.format("%s-%s-jar-with-dependencies.jar",meta.getName(),meta.getVersion());
        String jarPath="target/"+jarName;
        ScriptGenerator.doGenerator(shellDestPath,jarPath);

        //精简版
        String distDestPath=destPath+"-dist";
        //拷贝jar包
        String targetAbsolutePath=distDestPath+File.separator+"target";
        FileUtil.mkdir(targetAbsolutePath);
        String jarAbsolutePath = destPath + File.separator + jarPath;
        FileUtil.copy(jarAbsolutePath,targetAbsolutePath,true);
        ///拷贝脚本文件
        FileUtil.copy(shellDestPath,distDestPath,true);
        FileUtil.copy(shellDestPath+".bat",distDestPath,true);
        //拷贝源文件
        FileUtil.copy(sourceCopyDestPath,distDestPath,true);*/



    }
}
