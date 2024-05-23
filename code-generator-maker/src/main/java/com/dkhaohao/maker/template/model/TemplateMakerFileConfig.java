package com.dkhaohao.maker.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author dkhaohao
 * @Date 2024/5/22/16:51
 * @Description :
 */
@Data
public class TemplateMakerFileConfig {
    private List<FileInfoConfig> files;

    /**
     * 文件分组配置
     */
    private FileGroupConfig fileGroupConfig;

    @Data
    @NoArgsConstructor
    public static class FileInfoConfig {
        private String path;
        private List<FileFilterConfig> fileFilterConfigs;
    }

    @Data
    public static class FileGroupConfig {
        private String condition;
        private String groupKey;
        private String groupName;
    }
}
