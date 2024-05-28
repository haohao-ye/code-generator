package com.dkhaohao.web.model.dto.generator;

import com.dkhaohao.maker.meta.Meta;
import lombok.Data;

/**
 * @Author dkhaohao
 * @Date 2024/5/27/14:39
 * @Description :
 */
@Data
public class GeneratorMakeRequest implements java.io.Serializable {
    /**
     * 压缩文件路径
     */
    private String zipFilePath;
    /**
     * 元数据
     */
    private Meta meta;

    public static final long serialVersionUID = 1L;
}
