package ${basePackage}.model;

import lombok.Data;

/**
 * @author ${author}
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/717:23
 */
@Data
public class DataModel {
<#list modelConfig.models as modelInfo>

    <#if modelInfo.description??>
        /**
        * ${modelInfo.description}
        */
    </#if>

    private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c} </#if>;

</#list>


}
