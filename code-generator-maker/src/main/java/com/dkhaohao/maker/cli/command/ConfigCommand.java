package com.dkhaohao.maker.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.dkhaohao.maker.model.DataModel;
import picocli.CommandLine.Command;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/916:57
 */
@Command(name = "config",description = "查看参数信息",mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable{

    @Override
    public void run() {
        //
        System.out.println("查看参数信息");

        Field[] fields = ReflectUtil.getFields(DataModel.class);

        Arrays.stream(fields).forEach(field -> {
            System.out.println("字段类型："+field.getType());
            System.out.println("字段名： "+field.getName());
            System.out.println("================");
        });

    }
}
