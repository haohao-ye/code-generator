package com.dkhaohao.maker.model;

import lombok.Data;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/717:23
 */
@Data
public class DataModel {
    /**
     * 是否生成循环
     */
    public boolean loop = true;

    public boolean needGit = true;
    public MainTemplate mainTemplate = new MainTemplate();
    @Data
    public static class MainTemplate {
        /**
         * 作者名
         */
        public String author = "dkhaohao";
        /**
         * 输出信息
         */
        public String outputText = "sum = ";
    }

}
