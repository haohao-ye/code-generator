package com.dkhaohao.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.dkhaohao.maker.generator.JarGenerator;
import com.dkhaohao.maker.generator.ScriptGenerator;
import com.dkhaohao.maker.generator.file.DynamicFileGenerator;
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
 * @date 2024/3/1218:02
 */
public abstract class GenerateTemplate {

    public void doGenerate() throws TemplateException, IOException, InterruptedException {
        //获取Meta类
        Meta meta = MetaManager.getMeta();

        //输出根路径
        String projectPath = System.getProperty("user.dir");
        String destPath = projectPath + File.separator + meta.getName();
        if (!FileUtil.exist(destPath)) {
            FileUtil.mkdir(destPath);
        }

        //从原始模版文件路径复制到生成的代码包中
        String sourceCopyDestPath = copySource(meta, destPath);

        generateCode(meta, destPath);

        //构建jar包
        String jarPath = buildJar(meta, destPath);
        //封装脚本
        String shellDestPath = buildScript(destPath, jarPath);

        //精简版
        buildDist(destPath, sourceCopyDestPath, jarPath, shellDestPath);

    }
    public void doGenerate(Meta meta, String outputPath) throws TemplateException, IOException, InterruptedException {
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 1. 复制原始文件
        String sourceCopyDestPath = copySource(meta, outputPath);


        // 2. 生成代码
        generateCode(meta, outputPath);

        // 3. 构建jar包
        String jarPath = buildJar(meta, outputPath);
        // 4. 封装脚本
        String shellDestPath = buildScript(outputPath, jarPath);

        // 5. 精简版
        buildDist(outputPath, sourceCopyDestPath, jarPath, shellDestPath);

    }

    /**
     * 生成精简版
     * @param destPath
     * @param sourceCopyDestPath
     * @param jarPath
     * @param shellDestPath
     */

    protected String buildDist(String destPath, String sourceCopyDestPath, String jarPath, String shellDestPath) {
        String distDestPath = destPath + "-dist";
        //拷贝jar包
        String targetAbsolutePath = distDestPath + File.separator + "target";
        FileUtil.mkdir(targetAbsolutePath);
        String jarAbsolutePath = destPath + File.separator + jarPath;
        FileUtil.copy(jarAbsolutePath, targetAbsolutePath, true);
        ///拷贝脚本文件
        FileUtil.copy(shellDestPath, distDestPath, true);
        FileUtil.copy(shellDestPath + ".bat", distDestPath, true);
        //拷贝源文件
        FileUtil.copy(sourceCopyDestPath, distDestPath, true);
        return distDestPath;
    }

    /**
     * 构建脚本文件
     * @param destPath
     * @param jarPath
     * @return
     */
    protected String buildScript(String destPath, String jarPath) {
        String shellDestPath = destPath + File.separator + "generator";
        ScriptGenerator.doGenerator(shellDestPath, jarPath);
        return shellDestPath;
    }

    /**
     * 构建jar包
     * @param meta
     * @param destPath
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    protected String buildJar(Meta meta, String destPath) throws IOException, InterruptedException {
        JarGenerator.doGenerate(destPath);

        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "target/" + jarName;
        return jarPath;
    }


    /**
     * 构建压缩包
     * @param outputPath
     * @return
     */
    protected String buildZip(String outputPath) {
        String zipPath = outputPath + ".zip";
        ZipUtil.zip(outputPath, zipPath);
        return zipPath;
    }

    /**
     * 生成代码
     *
     * @param meta     元文件
     * @param destPath 目标路径
     * @throws IOException
     * @throws TemplateException
     */

    protected void generateCode(Meta meta, String destPath) throws IOException, TemplateException {
        //读取Resource目录
        ClassPathResource classPathResource = new ClassPathResource("");
//        String resourceAbsolutePath = classPathResource.getAbsolutePath();
        String resourceAbsolutePath = "";

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

        //cli.command.JsonGenerateCommand
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/java/cli/command/JsonGenerateCommand.java.ftl";
        destModelPath = destJavaPath + File.separator + "cli/command/JsonGenerateCommand.java";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);

        //pom.xml
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/pom.xml.ftl";
        destModelPath = destPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);

        //README.md
        sourceModelPath = resourceAbsolutePath + File.separator + "templates/README.md.ftl";
        destModelPath = destPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerator(sourceModelPath, destModelPath, meta);
    }

    /**
     * 复制原始文件
     *
     * @param meta     元文件
     * @param destPath 目标路径
     * @return sourceCopyDestPath 源文件路径
     */
    protected String copySource(Meta meta, String destPath) {
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = destPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, false);
        return sourceCopyDestPath;
    }
}
