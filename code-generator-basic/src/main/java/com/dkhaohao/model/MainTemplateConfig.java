package com.dkhaohao.model;

import lombok.Data;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/717:23
 */
@Data
public class MainTemplateConfig {
    /**
     * 是否生成循环
     */
    private boolean loop=true;
    /**
     * 作者名
     */
    private String author="dkhaohao";
    /**
     * 输出信息
     */
    private String outputText="sum = ";

}
