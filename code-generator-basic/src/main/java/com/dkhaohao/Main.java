package com.dkhaohao;

import com.dkhaohao.cli.command.CommandExecutor;
import com.dkhaohao.generator.StaticGenerator;

import java.io.File;

/**
 * @author dkhaohao
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date ${DATE}${TIME}
 */
public class Main {
    public static void main(String[] args) {
        args=new String[]{"list"};
        CommandExecutor commandExecutor=new CommandExecutor();
        commandExecutor.doExecute(args);

    }
}