package ${basePackage};

import ${basePackage}.cli.CommandExecutor;

/**
 * @author ${author}
 * @Title:
 * @Package
 * @Description:
 * @date ${createTime}
 */
public class Main {
    public static void main(String[] args) {
        System.out.println(args.toString());

        CommandExecutor commandExecutor=new CommandExecutor();
        commandExecutor.doExecute(args);

    }
}