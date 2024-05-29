package com.dkhaohao.web.utils;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.json.JSONUtil;
import com.dkhaohao.web.model.dto.generator.GeneratorQueryRequest;

/**
 * @Author dkhaohao
 * @Date 2024/5/28/16:54
 * @Description : 缓存key生成器(业务名:事务名:参数)
 */
public class CacheKey {
    public static String getPageCacheKey(GeneratorQueryRequest generatorQueryRequest){
        String jsonStr = JSONUtil.toJsonStr(generatorQueryRequest);
        String base64 = Base64Encoder.encode(jsonStr);
        String key = "generator:page:" + base64;
        return key;
    }
}
