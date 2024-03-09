package com.dkhaohao.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.dkhaohao.generator.MainGenerator;
import com.dkhaohao.model.MainTemplateConfig;
import lombok.Data;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description: 接受命令行信息生成代码
 * @date 2024/3/916:26
 */
@Command(name = "generate",description = "generateCode",mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        MainTemplateConfig mainTemplateConfig=new MainTemplateConfig();
        BeanUtil.copyProperties(this,mainTemplateConfig);
        System.out.println("配置信息："+mainTemplateConfig);
        MainGenerator.doGenerator(mainTemplateConfig);
        return 0;
    }
    @Option(names = {"-l","--loop"},arity = "0..1",description = "是否循环",interactive = true)
    private boolean loop;
    @Option(names = {"-o","--outputText"},arity = "0..1",description = "输出文本",interactive = true)
    private String outputText;
    @Option(names = {"-a","--author"},arity = "0..1",description = "作者",interactive = true)
    private String author;

}
