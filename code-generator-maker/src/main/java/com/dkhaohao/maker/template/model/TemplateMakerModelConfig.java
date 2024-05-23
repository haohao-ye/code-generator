package com.dkhaohao.maker.template.model;

import com.dkhaohao.maker.meta.Meta;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author dkhaohao
 * @Date 2024/5/23/15:24
 * @Description :
 */
@Data
public class TemplateMakerModelConfig {
    private List<ModelInfoConfig> models;
    private  ModelGroupConfig modelGroupConfig;
    @Data
    @NoArgsConstructor
    public static class ModelInfoConfig {
        private String fieldName;
        private String type;
        private String description;
        private Object defaultValue;
        private String abbr;

        /**
         * 需要被替换的文本
         */
        private String replaceText;
    }
    @Data
    public static class ModelGroupConfig {
        private String condition;
        private String groupKey;
        private String groupName;
    }
}
