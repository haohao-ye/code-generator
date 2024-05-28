package com.dkhaohao.cli.example;


import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/914:58
 */
@CommandLine.Command(name = "Login", version = "Lofin 1.0", mixinStandardHelpOptions = true)
public class Login implements Callable<Integer> {
    @CommandLine.Option(names = {"-u", "--user"}, description = "username")
    String username;
    //arity 参数个数可选 0到1个参数
    @CommandLine.Option(names = {"-p", "--password"},arity = "0..1", description = "password",interactive = true)
    String password;
    @CommandLine.Option(names = {"-cp", "--checkpassword"}, description = "checkPassword",interactive = true)
    String checkPassword;

    @CommandLine.Parameters(paramLabel = "<word>", defaultValue = "Hello, picocli",
            description = "Words to be translated into ASCII art.")
    private String[] words = {"Hello,", "picocli"};

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Login()).execute("-u","username","-p","-cp");
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println(password);
        System.out.println(checkPassword);
        return 0;
    }
}
