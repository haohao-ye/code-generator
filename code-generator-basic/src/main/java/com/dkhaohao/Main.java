package com.dkhaohao;

import com.dkhaohao.cli.CommandExecutor;

/**
 * @author dkhaohao
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date ${DATE}${TIME}
 */
public class Main {
    public static void main(String[] args) {
        CommandExecutor commandExecutor=new CommandExecutor();
        commandExecutor.doExecute(args);

    }
}