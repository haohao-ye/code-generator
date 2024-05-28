package com.dkhaohao.maker.generator.main;

/**
 * @Author dkhaohao
 * @Date 2024/5/26/13:05
 * @Description :
 */
public class ZipGenerator extends GenerateTemplate {
    @Override
    protected String buildDist(String outputPath,String sourceCopyDestPath,String jarPath, String shellOutPutFilePath){

        String distPath =  super.buildDist(outputPath,sourceCopyDestPath,jarPath,shellOutPutFilePath);
        return super.buildZip(distPath);
    }
}
