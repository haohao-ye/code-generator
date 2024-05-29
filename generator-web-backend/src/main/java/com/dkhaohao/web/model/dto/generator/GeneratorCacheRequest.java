package com.dkhaohao.web.model.dto.generator;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author dkhaohao
 * @Date 2024/5/28/10:44
 * @Description :
 */
@Data
public class GeneratorCacheRequest implements Serializable {
    private Long id;

    public static final long serialVersionUID = 1L;
}
