package com.dkhaohao.maker.template;

import cn.hutool.core.util.StrUtil;
import com.dkhaohao.maker.meta.Meta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author dkhaohao
 * @Date 2024/5/24/10:50
 * @Description : 模板生成工具类
 */
public class TemplateMakerUtils {

    /**
     * 从未分组文件中移除同名文件
     * @param fileInfos 文件列表
     * @return 移除分组文件后的文件列表
     */
    public static List<Meta.FileConfig.FileInfo> removeGroupFilesFromRoot(List<Meta.FileConfig.FileInfo> fileInfos){
        //获取所有分组
        List<Meta.FileConfig.FileInfo> groupFileInfos = fileInfos
                .stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        //获取分组内的文件列表
        List<Meta.FileConfig.FileInfo> groupInnerFileInfos = groupFileInfos
                .stream()
                .flatMap(fileInfo -> fileInfo.getFiles().stream())
                .collect(Collectors.toList());
        //获取分组内的文件输入路径集合
        Set<String> fileInputPathSet = groupInnerFileInfos
                .stream()
                .map(Meta.FileConfig.FileInfo::getInputPath)
                .collect(Collectors.toSet());
        //移除所有名称在set中的外层文件
        return fileInfos
                .stream()
                .filter(fileInfo -> !fileInputPathSet.contains(fileInfo.getInputPath()))
                .collect(Collectors.toList());
    }
}
