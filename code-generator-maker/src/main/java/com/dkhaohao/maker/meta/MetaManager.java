package com.dkhaohao.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/1013:13
 */
public class MetaManager {
    //volatile用于确保多线程环境下内存的可见性
    private static volatile Meta meta;

    //双检锁单例模式
    public static Meta getMeta() {
        if (meta == null) {//防止线程强锁，影响性能
            synchronized (MetaManager.class){
                if (meta == null) {
                    meta=initMeta();
                }
            }
        }
        return meta;
    }
    private static Meta initMeta(){

        String metaJson = ResourceUtil.readUtf8Str("meta.json");
//        String metaJson = ResourceUtil.readUtf8Str("springboot-init-meta.json");
        Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
        // todo 校验配置文件，处理默认值
        MetaValidator.doValidAndFill(newMeta);
        return newMeta;
    }

}
