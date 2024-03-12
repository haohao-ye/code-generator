package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author ${author}
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/916:41
 */
@Command(name = "list",description = "查看生成文件列表",mixinStandardHelpOptions = true)
public class ListCommand implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        //输入路径
        String sourcePath = "${fileConfig.inputRootPath}";
        List<File> files = FileUtil.loopFiles(sourcePath);
        files.stream().forEach(System.out::println);
        return null;
    }
}
