package com.dkhaohao.cli;


import com.dkhaohao.cli.command.ConfigCommand;
import com.dkhaohao.cli.command.GenerateCommand;
import com.dkhaohao.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/916:13
 */
@Command(name = "dk",mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable{
    @Override
    public void run() {
        System.out.println("请输入命令（输入 --help 查看命令提示）");
    }

    private final CommandLine commandLine;

    /**
     * generate 子命令：生成文件
     * list 子命令：查看要生成的原始文件列表
     * config 子命令：查看运行用户传入的动态参数信息
     */
    {
        commandLine=new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }

    public Integer doExecute(String[] args){
        return commandLine.execute(args);
    }

}
