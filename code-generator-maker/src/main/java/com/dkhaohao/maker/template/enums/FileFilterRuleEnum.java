package com.dkhaohao.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * @Author dkhaohao
 * @Date 2024/5/22/17:01
 * @Description : 文件过滤规则枚举
 */
@Getter
public enum FileFilterRuleEnum {
    CONTAINS("包含","contains"),

    STARTS_WITH("前缀匹配","startsWith"),

    ENDS_WITH("后缀匹配","endsWith"),

    EQUALS("相等","equals"),

    REGEX("正则","regex");

    private String name;
    private String value;

    FileFilterRuleEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */

    public static FileFilterRuleEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (FileFilterRuleEnum e : FileFilterRuleEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
