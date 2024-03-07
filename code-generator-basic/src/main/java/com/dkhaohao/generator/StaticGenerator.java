package com.dkhaohao.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;


/**
 * @author dkhaohao
 * @Title:
 * @Package com.dkhaohao.generator
 * @Description: 静态代码生成
 * @date 2024/3/715:29
 */
public class StaticGenerator {
    public static void main(String[] args) {
        //获取当前项目的跟目录
        String projectPath = System.getProperty("user.dir");

        //输入路径：ACM 示例代码模版目录
        String sourcePath =
                new File(new File(projectPath).getParent(), "code-generator-demo-projects" + File.separator + "acm-template").getAbsolutePath();
        //输出路径：直接输出到项目的根目录
        String destPath = projectPath;
        StaticGenerator.copyFilesByHutool(sourcePath, destPath);

    }

    /***
     *拷贝文件或目录
     * @param sourcePath 源文件或目录路径
     * @param destPath 目标文件或目录路径
     */
    public static void copyFilesByHutool(String sourcePath, String destPath) {
        FileUtil.copy(sourcePath, destPath, false);

    }
}
