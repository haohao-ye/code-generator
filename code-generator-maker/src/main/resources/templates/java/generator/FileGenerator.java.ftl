package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

<#macro generateFile indent fileInfo>
${indent}sourcePath=new File(sourceRootPath,"${fileInfo.inputPath}").getAbsolutePath();
${indent}destPath=new File(destRootPath,"${fileInfo.outputPath}").getAbsolutePath();
    <#if fileInfo.generateType=="static">
${indent}StaticFileGenerator.copyFilesByHutool(sourcePath,destPath);
    <#else >
${indent}DynamicFileGenerator.doGenerator(sourcePath,destPath,model);
    </#if>
</#macro>

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
    public static void doGenerator(DataModel model) throws TemplateException, IOException {
        //代码的原路径和目标根路径
        String sourceRootPath="${fileConfig.inputRootPath}";
        String destRootPath="${fileConfig.outputRootPath}";

        String sourcePath;
        String destPath;
    <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#list modelInfo.models as subModelInfo>
        ${subModelInfo.type} ${subModelInfo.fieldName} = model.${modelInfo.groupKey}.${subModelInfo.fieldName};
        </#list>
        <#else>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
        </#if>
    </#list>

<#list fileConfig.files as fileInfo>
    <#if fileInfo.groupKey??>
        <#if fileInfo.condition??>
        if(${fileInfo.condition}){
        <#list fileInfo.files as fileInfo>
            <@generateFile indent="            " fileInfo=fileInfo/>
        </#list>
        }
        <#else >
        <#list fileInfo.files as fileInfo>
            <@generateFile indent="        " fileInfo=fileInfo/>
        </#list>
        </#if>
    <#else >
        <#if fileInfo.condition??>
        if(${fileInfo.condition}){
            <@generateFile indent="            " fileInfo=fileInfo/>
        }
        <#else >
            <@generateFile indent="        " fileInfo=fileInfo/>
        </#if>
    </#if>
</#list>
    }

}
