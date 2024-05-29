package com.dkhaohao.web.manager;

import cn.hutool.core.collection.CollUtil;
import com.dkhaohao.web.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import com.qcloud.cos.transfer.Download;
import com.qcloud.cos.transfer.TransferManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Cos 对象存储操作
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    private TransferManager transferManager;

    /**
     * 上传对象
     *
     * @param key           唯一键
     * @param localFilePath 本地文件路径
     * @return
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                new File(localFilePath));
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }

    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    //bean加载完成后执行
    @PostConstruct
    public void init() {
        System.out.println("Bean init");
        //多线程并发上传下载
        ExecutorService threadPool = Executors.newFixedThreadPool(32);
        transferManager = new TransferManager(cosClient, threadPool);
    }


    public Download download(String key, String localFilePath) throws InterruptedException {
        File dowloadFile = new File(localFilePath);
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        Download download = transferManager.download(getObjectRequest, dowloadFile);

        //等待下载完成
        download.waitForCompletion();
        return download;
    }

    /**
     * 删除对象
     *
     * @param key
     */
    public void deleteObject(String key) {
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }

    /**
     * 批量删除对象
     *
     * @param keyList
     * @return
     */
    public DeleteObjectsResult deleteObjects(List<String> keyList) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(cosClientConfig.getBucket());
        //设置要删除的对象
        ArrayList<DeleteObjectsRequest.KeyVersion> keyVersionList = new ArrayList<>();
        //传入要删除的文件名
        //文件名不允许以正斜杠（/）或反斜杠（\）开头
        //例如: 存储桶有test/test/test.txt ,如果要删除,只能是keyList.add("test/test/test.txt"),
        for (String key : keyList) {
            keyVersionList.add(new DeleteObjectsRequest.KeyVersion(key));
        }
        deleteObjectsRequest.setKeys(keyVersionList);
        DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);

        return deleteObjectsResult;
    }

    /**
     * 删除目录(后缀需要加个/,否则会删除所有前缀匹配的文件)
     * @param delPrefix
     */
    public void deleteDir(String delPrefix) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        // 设置 bucket 名称
        listObjectsRequest.setBucketName(cosClientConfig.getBucket());
        // prefix 表示列出的对象名以 prefix 为前缀
        // 这里填要列出的目录的相对 bucket 的路径
        listObjectsRequest.setPrefix(delPrefix);
        // 设置最大遍历出多少个对象, 一次 listobject 最大支持1000
        listObjectsRequest.setMaxKeys(1000);
        // 保存每次列出的结果
        ObjectListing objectListing = null;

        do {
            objectListing = cosClient.listObjects(listObjectsRequest);

            // 这里保存列出的对象列表
            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            if(CollUtil.isEmpty(cosObjectSummaries)){
                break;
            }
            ArrayList<DeleteObjectsRequest.KeyVersion> delObjects = new ArrayList<>();
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                delObjects.add(new DeleteObjectsRequest.KeyVersion(cosObjectSummary.getKey()));
            }


            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(cosClientConfig.getBucket());
            deleteObjectsRequest.setKeys(delObjects);
            cosClient.deleteObjects(deleteObjectsRequest);



            // 标记下一次开始的位置
            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);
        } while (objectListing.isTruncated());

    }
}
