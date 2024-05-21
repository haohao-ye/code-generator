package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.FileGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine;
import java.util.concurrent.Callable;
<#--生成选项-->
<#macro generateOptions indent modelInfo>
${indent}@Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}",</#if>"--${modelInfo.fieldName}"},arity = "0..1",<#if modelInfo.description??>description = "${modelInfo.description}",</#if>interactive = true ,echo = true)
${indent}private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??>=${modelInfo.defaultValue?c}</#if>;
</#macro>
<#--    生成命令调用-->
<#macro generateCommand indent modelInfo>
${indent}System.out.println("输入${modelInfo.groupName}配置: ");
${indent}CommandLine commandline = new CommandLine(${modelInfo.type}Command.class);
${indent}commandline.execute(${modelInfo.allArgsStr});
</#macro>
/**
 * @author ${author}
 * @Title:
 * @Package
 * @Description: 接受命令行信息生成代码
 * @date 2024/3/916:26
 */
@Command(name = "generate",description = "generateCode",mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable<Integer> {


    <#list modelConfig.models as modelInfo>
<#--    有分组-->
        <#if modelInfo.groupKey??>
        /**
         * ${modelInfo.groupName}
         */
         static DataModel.${modelInfo.type} ${modelInfo.groupKey}= new DataModel.${modelInfo.type}();
        <#--根据分组生成命令-->
        @Command(name = "${modelInfo.groupKey}")
        @Data
        public static class ${modelInfo.type}Command implements Callable<Integer> {
            <#list modelInfo.models as subModelInfo>
                <@generateOptions indent="                " modelInfo=subModelInfo/>
            </#list>
            @Override
            public Integer call() throws Exception {
                <#list modelInfo.models as subModelInfo>
                    ${modelInfo.groupKey}.${subModelInfo.fieldName}="${subModelInfo.fieldName}";
                </#list>
                return 0;
            }
        }
        <#else>
        <@generateOptions indent="    " modelInfo=modelInfo/>
        </#if>
    </#list>
    @Override
    public Integer call() throws Exception {
    <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#if modelInfo.condiction??>
        if(${modelInfo.condiction}){
            <@generateCommand indent="            " modelInfo=modelInfo/>
        }
        <#else>
            <@generateCommand indent="        " modelInfo=modelInfo/>
        </#if>
        </#if>
    </#list>
        <#--填充数据对象-->
        DataModel dataModel =new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        dataModel.${modelInfo.groupKey}=${modelInfo.groupKey};
        </#if>
        </#list>
        FileGenerator.doGenerator(dataModel);
        return 0;
    }
}