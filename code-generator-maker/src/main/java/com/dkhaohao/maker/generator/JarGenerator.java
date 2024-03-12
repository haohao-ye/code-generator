package com.dkhaohao.maker.generator;

import java.io.*;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/1214:06
 */
public class JarGenerator {

    public static void main(String[] args) throws IOException, InterruptedException {
        doGenerate("C:\\Users\\86135\\Desktop\\Project-Practice\\code-generator\\code-generator-basic");
    }
    
    public static void doGenerate(String projectDir) throws IOException, InterruptedException {
        //调用 Process类执行 Maven打包命令
        String winMavenCommand = "mvn.cmd clean package -DskipTests=true";
        String otherMavenCommand = "mvn clean package -DskipTests=true";
        String mavenCommand=winMavenCommand;
        ProcessBuilder processBuilder = new ProcessBuilder(mavenCommand.split(" "));
        processBuilder.directory(new File(projectDir));
        Process process = processBuilder.start();

        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = bufferedReader.readLine())!=null){
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("命令执行结束,退出码:"+exitCode);
    }
}
