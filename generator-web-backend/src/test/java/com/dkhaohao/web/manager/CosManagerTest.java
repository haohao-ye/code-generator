package com.dkhaohao.web.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * Cos 操作测试
 *
 * @author dkhaohao
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class CosManagerTest {

    @Resource
    private CosManager cosManager;

    @Test
    void putObject() {
        cosManager.putObject("test/test.json", "C:\\Users\\86135\\Desktop\\Project-Practice\\code-generator\\generator-web-backend\\sql\\tables_xxl_job.sql");
    }


    @Test
    void deleteObject() {
        cosManager.deleteObject("/test/test.json");
    }

    @Test
    void deleteObjects() {
        cosManager.deleteObjects(Arrays.asList("test/11个设计小技巧2.png", "test/img.png"));
    }

    @Test
    void deleteDir() {
        cosManager.deleteDir("test/");
    }
}