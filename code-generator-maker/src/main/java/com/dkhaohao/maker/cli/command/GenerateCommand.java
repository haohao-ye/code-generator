package com.dkhaohao.maker.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.dkhaohao.maker.generator.file.FileGenerator;
import com.dkhaohao.maker.model.DataModel;
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
        DataModel dataModel =new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        System.out.println("配置信息："+ dataModel);
        FileGenerator.doGenerator(dataModel);
        return 0;
    }
    @Option(names = {"-l","--loop"},arity = "0..1",description = "是否循环",interactive = true ,echo = true)
    private boolean loop;
    @Option(names = {"-o","--outputText"},arity = "0..1",description = "输出文本",interactive = true ,echo = true)
    private String outputText;
    @Option(names = {"-a","--author"},arity = "0..1",description = "作者",interactive = true,echo = true)
    private String author;

}
