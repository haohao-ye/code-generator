package com.dkhaohao.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import javax.annotation.Generated;

/**
 * @Author dkhaohao
 * @Date 2024/5/22/16:54
 * @Description : 文件过滤范围枚举
 */
@Getter
public enum FileFilterRangeEnum {
    FILE_NAME("文件名", "fileName"),

    FILE_CONTENT("文件内容", "fileContent");


    private final String text;
    private final String value;

    FileFilterRangeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static FileFilterRangeEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (FileFilterRangeEnum e : FileFilterRangeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
