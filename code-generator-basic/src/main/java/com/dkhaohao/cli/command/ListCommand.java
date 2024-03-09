package com.dkhaohao.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/916:41
 */
@Command(name = "list",description = "查看生成文件列表",mixinStandardHelpOptions = true)
public class ListCommand implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        //子模块根目录（工作路径）
        String projectPath = System.getProperty("user.dir");
        //项目根目录
        File parentFile = new File(projectPath).getParentFile();
        //输入路径
        String sourcePath = new File(parentFile, "code-generator-demo-projects" + File.separator + "acm-template").getAbsolutePath();
        List<File> files = FileUtil.loopFiles(sourcePath);
        files.stream().forEach(System.out::println);
        return null;
    }
}
