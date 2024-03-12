package ${basePackage}.generator;

import cn.hutool.core.io.FileUtil;


/**
 * @author dkhaohao
 * @Description: 静态代码生成
 * @date 2024/3/715:29
 */
public class StaticFileGenerator {

    /***
     *拷贝文件或目录
     * @param sourcePath 源文件或目录路径
     * @param destPath 目标文件或目录路径
     */
    public static void copyFilesByHutool(String sourcePath, String destPath) {
        FileUtil.copy(sourcePath, destPath, false);

    }
}
