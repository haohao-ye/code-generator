package com.dkhaohao.maker.template.model;

import lombok.Data;

/**
 * @Author dkhaohao
 * @Date 2024/5/24/10:43
 * @Description :
 */
@Data
public class TemplateMakerOutputConfig {
    //从未分组文件中移除组内的同名文件
    private boolean removeGroupFilesFromRoot = true;
}
