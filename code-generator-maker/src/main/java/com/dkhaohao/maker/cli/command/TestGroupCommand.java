package com.dkhaohao.maker.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.dkhaohao.maker.generator.file.FileGenerator;
import com.dkhaohao.maker.model.DataModel;
import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
 * @Author dkhaohao
 * @Date 2024/5/20/15:32
 * @Description :
 */
@Data
@Command(name = "generate",description = "generateCode",mixinStandardHelpOptions = true)
public class TestGroupCommand implements Callable<Integer> {

    @Option(names = {"-g","--needGit"},arity = "0..1",description = "是否生成git相关文件",interactive = true ,echo = true)
    private boolean needGit=true;
    @Option(names = {"-l","--loop"},arity = "0..1",description = "是否生成循环",interactive = true ,echo = true)
    private boolean loop=false;

    static DataModel.MainTemplate mainTemplate = new DataModel.MainTemplate();

    @Override
    public Integer call() throws Exception {
     /*   DataModel dataModel =new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        System.out.println("配置信息："+ dataModel);
        FileGenerator.doGenerator(dataModel);*/
        System.out.println(needGit);

        System.out.println(loop);
        if (true) {
            System.out.println("输入核心模版配置");
            CommandLine commandLine = new CommandLine(MainTemplateCommand.class);
            commandLine.execute("-a","-o");
            System.out.println(mainTemplate);
        }
        return 0;
    }
    @Command(name = "maintemplate",description = "生成主类模板",mixinStandardHelpOptions = true)
    public static class MainTemplateCommand implements Callable<Integer> {
        @Option(names = {"-a","--author"},arity = "0..1",description = "作者注释",interactive = true ,echo = true)
        private String author="dkhaohao";
        @Option(names = {"-o","--outputText"},arity = "0..1",description = "输出信息",interactive = true ,echo = true)
        private String outputText="sum = ";

        @Override
        public Integer call() throws Exception {
            mainTemplate.author = author;
            mainTemplate.outputText = outputText;
            System.out.println("配置信息："+ mainTemplate);
            return null;
        }
    }
    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(TestGroupCommand.class);
//        commandLine.execute("--help");
        commandLine.execute("-g","-l");
    }
}
