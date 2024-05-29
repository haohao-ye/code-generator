package com.dkhaohao.web.job;

import cn.hutool.core.util.StrUtil;
import com.dkhaohao.web.manager.CosManager;
import com.dkhaohao.web.mapper.GeneratorMapper;
import com.dkhaohao.web.model.entity.Generator;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author dkhaohao
 * @Date 2024/5/29/14:34
 * @Description :
 */
@Component
@Slf4j
public class ClearCosJobHandler {
    @Resource
    private CosManager cosManager;
    @Resource
    private GeneratorMapper generatorMapper;

    @XxlJob("clearCosJobHandler")
    public void clearCosJobHandler() throws Exception {
        log.info("ClearCosJobHandler start");
        //业务逻辑
        //删除用户上传的模版制作文件
        cosManager.deleteDir("/generator_make_template/");
        //已删除的代码生成器对应的产物包文件
        List<Generator> generators = generatorMapper.listDeleteGenerator();
        List<String> keyList = generators.stream().map(Generator::getDistPath)
                .filter(StrUtil::isNotBlank)
                //移除'/'前缀
                .map(distPath -> distPath.substring(1))
                .collect(Collectors.toList());
        cosManager.deleteObjects(keyList);
        log.info("ClearCosJobHandler end");

    }
}
