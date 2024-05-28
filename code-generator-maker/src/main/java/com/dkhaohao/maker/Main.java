package com.dkhaohao.maker;

import com.dkhaohao.maker.generator.MainGenerator;
import com.dkhaohao.maker.generator.main.ZipGenerator;
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
//        MainGenerator generatorTemplate = new MainGenerator();
        ZipGenerator generatorTemplate = new ZipGenerator();
        generatorTemplate.doGenerate();
//        CommandExecutor commandExecutor=new CommandExecutor();
//        commandExecutor.doExecute(args);

    }
}