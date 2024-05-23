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

    @Data
    @NoArgsConstructor
    public static class FileInfoConfig {
        private String path;
        private List<FileFilterConfig> fileFilterConfigs;
    }
}
