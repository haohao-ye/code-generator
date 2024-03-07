package com.dkhaohao;

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
        //获取当前项目的跟目录
        String projectPath=System.getProperty("user.dir");
        System.out.println(projectPath);
        File parentFile=new File(projectPath).getParentFile();
        //输入路径：ACM 示例代码模版目录
        String sourcePath =
                new File(parentFile, "code-generator-demo-projects"+File.separator+"acm-first-template.ftl").getAbsolutePath();
        //输出路径：直接输出到项目的根目录
        String destPath=projectPath;
        StaticGenerator.copyFilesByHutool(sourcePath,destPath);


    }
}