package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import ${basePackage}.generator.FileGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @Author dkhaohao
 * @Date 2024/5/26/14:30
 * @Description :
 */
@CommandLine.Command(name = "json-generate", description = "Generate json file from csv file",mixinStandardHelpOptions = true)
@Data
public class JsonGenerateCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-f", "--file"}, arity = "0..1", description = "The path of csv file", interactive = true, echo = true )
    private String filePath;


    @Override
    public Integer call() throws Exception {
        String jsonStr = FileUtil.readUtf8String(filePath);
        DataModel dataModel = JSONUtil.toBean(jsonStr, DataModel.class);
        FileGenerator.doGenerator(dataModel);
        return 0;
    }
}
