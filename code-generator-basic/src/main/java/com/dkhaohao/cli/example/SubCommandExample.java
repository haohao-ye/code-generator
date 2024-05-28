package com.dkhaohao.cli.example;

import picocli.CommandLine;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/915:22
 */
@CommandLine.Command(name = "main",mixinStandardHelpOptions = true)
public class SubCommandExample implements Runnable{
    @Override
    public void run() {
        System.out.println("main");

    }
    @CommandLine.Command(name = "add",description = "增加",mixinStandardHelpOptions = true)
    static class AddCommand implements Runnable{
        @Override
        public void run() {
            System.out.println("add...");
        }
    }
    @CommandLine.Command(name = "delete",description = "删除",mixinStandardHelpOptions = true)
    static class DeleteCommand implements Runnable{
        @Override
        public void run() {
            System.out.println("delete...");
        }
    }
    @CommandLine.Command(name = "query",description = "查询",mixinStandardHelpOptions = true)
    static class QueryCommand implements Runnable{
        @Override
        public void run() {
            System.out.println("query...");
        }
    }


    public static void main(String[] args) {
        String [] myArgs=new String[]{"main add"};

        int execute = new CommandLine(new SubCommandExample())
                .addSubcommand(new AddCommand())
                .addSubcommand(new DeleteCommand())
                .addSubcommand(new QueryCommand()).execute(myArgs);
        System.exit(execute);
    }
}
