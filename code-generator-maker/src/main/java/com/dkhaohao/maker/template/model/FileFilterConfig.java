package com.dkhaohao.maker.template.model;

import lombok.Builder;
import lombok.Data;

/**
 * @Author dkhaohao
 * @Date 2024/5/22/16:12
 * @Description :
 */
@Data
@Builder
public class FileFilterConfig {
    /**
     * 过滤范围
     */
    private String range;
    /**
     * 过滤规则
     */
    private String rule;
    /**
     * 过滤值
     */
    private String value;
}
