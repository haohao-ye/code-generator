package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author ${author}
 * @Title: 代码生成器
 * @Package
 * @Description: 生成器主方法
 * @date 2024/3/721:33
 */
public class FileGenerator {
    /**
    * 生成目标代码文件的方法
    * @param model 模版模型数据
    * @throws TemplateException 模版异常
    * @throws IOException IO异常
    */
    public static void doGenerator(Object model) throws TemplateException, IOException {
        //代码的原路径和目标根路径
        String sourceRootPath="${fileConfig.inputRootPath}";
        String destRootPath="${fileConfig.outputRootPath}";

        String sourcePath;
        String destPath;

<#list fileConfig.files as fileInfo>

        sourcePath=new File(sourceRootPath,"${fileInfo.inputPath}").getAbsolutePath();
        destPath=new File(destRootPath,"${fileInfo.outputPath}").getAbsolutePath();

    <#if fileInfo.generateType=="static">
        StaticFileGenerator.copyFilesByHutool(sourcePath,destPath);
     <#else >
        DynamicFileGenerator.doGenerator(sourcePath,destPath,model);
    </#if>

</#list>
    }

}
