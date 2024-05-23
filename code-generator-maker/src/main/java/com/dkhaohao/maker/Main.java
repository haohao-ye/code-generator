package com.dkhaohao.maker;

import com.dkhaohao.maker.generator.MainGenerator;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * @author dkhaohao
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date ${DATE}${TIME}
 */
public class Main {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        MainGenerator mainGenerator = new MainGenerator();
        mainGenerator.doGenerate();
//        CommandExecutor commandExecutor=new CommandExecutor();
//        commandExecutor.doExecute(args);

    }
}