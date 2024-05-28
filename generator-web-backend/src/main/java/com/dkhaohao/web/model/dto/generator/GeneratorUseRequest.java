package com.dkhaohao.web.model.dto.generator;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author dkhaohao
 * @Date 2024/5/27/12:00
 * @Description :
 */
@Data
public class GeneratorUseRequest implements Serializable {
    /**
     * 生成器的id
     */

    private Long id;

    /**
     * 数据模型
     */
    Map<String, Object> dataModel;

    public static final long serialVersionUID = 1L;
}
