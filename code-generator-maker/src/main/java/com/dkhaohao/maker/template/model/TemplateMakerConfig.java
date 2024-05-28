package com.dkhaohao.maker.template.model;

import com.dkhaohao.maker.meta.Meta;
import lombok.Data;

/**
 * @Author dkhaohao
 * @Date 2024/5/23/18:09
 * @Description : 模板生成器配置类
 */
@Data
public class TemplateMakerConfig {
    private Long id;
    private Meta meta;
    private String originProjectPath;
    TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();
    TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();
    TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();
}
