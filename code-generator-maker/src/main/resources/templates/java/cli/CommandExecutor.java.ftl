package ${basePackage}.cli;


import ${basePackage}.cli.command.ConfigCommand;
import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.command.ListCommand;
import ${basePackage}.cli.command.JsonGenerateCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * @author ${author}
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/916:13
 */
@Command(name = "${name}",mixinStandardHelpOptions = true)
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
                .addSubcommand(new ListCommand())
                .addSubcommand(new JsonGenerateCommand());
    }

    public Integer doExecute(String[] args){
        return commandLine.execute(args);
    }

}
