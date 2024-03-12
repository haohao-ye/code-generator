package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.FileGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

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
        @Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}",</#if>"--${modelInfo.fieldName}"},arity = "0..1",<#if modelInfo.description??>description = "${modelInfo.description}",</#if>interactive = true ,echo = true)
        private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??>=${modelInfo.defaultValue?c}</#if>;
    </#list>
    @Override
    public Integer call() throws Exception {
        DataModel dataModel =new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        System.out.println("配置信息："+ dataModel);
        FileGenerator.doGenerator(dataModel);
        return 0;
    }
}