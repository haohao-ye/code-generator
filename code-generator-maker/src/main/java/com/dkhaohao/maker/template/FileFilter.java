package com.dkhaohao.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.dkhaohao.maker.template.enums.FileFilterRangeEnum;
import com.dkhaohao.maker.template.enums.FileFilterRuleEnum;
import com.dkhaohao.maker.template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author dkhaohao
 * @Date 2024/5/22/17:10
 * @Description :
 */
public class FileFilter {
    /**
     * 对某个文件或目录进行过滤,返回文件列表
     *
     * @param filePath 要过滤的文件或目录路径
     * @param fileFilterConfigList 过滤规则
     * @return 过滤后的文件列表
     */

    public static List<File> doFilter(String filePath, List<FileFilterConfig> fileFilterConfigList) {
        List<File> files = FileUtil.loopFiles(filePath);
        return files.stream().filter(file -> doSingleFileFilter(fileFilterConfigList, file)).collect(Collectors.toList());
    }

    /**
     * 单个文件过滤
     *
     * @param fileFilterConfigList 过滤规则
     * @param file                 单个文件
     * @return 是否过滤
     */

    public static boolean doSingleFileFilter(List<FileFilterConfig> fileFilterConfigList, File file) {
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);

        //所有过滤器校验的结果
        boolean result = true;
        if (CollUtil.isEmpty(fileFilterConfigList)) {
            return true;
        }

        for (FileFilterConfig fileFilterConfig : fileFilterConfigList) {
            String range = fileFilterConfig.getRange();
            String value = fileFilterConfig.getValue();
            String rule = fileFilterConfig.getRule();

            FileFilterRangeEnum fileFilterRangeEnum = FileFilterRangeEnum.getEnumByValue(range);
            if (fileFilterRangeEnum == null) {
                continue;
            }
            String content = fileName;
            switch (fileFilterRangeEnum) {
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
                default:
            }
            FileFilterRuleEnum filterRuleEnum = FileFilterRuleEnum.getEnumByValue(rule);
            if (filterRuleEnum == null) {
                continue;
            }
            switch (filterRuleEnum) {
                case CONTAINS:
                    result = content.contains(value);
                    break;
                case EQUALS:
                    result = content.equals(value);
                    break;
                case STARTS_WITH:
                    result = content.startsWith(value);
                    break;
                case ENDS_WITH:
                    result = content.endsWith(value);
                    break;
                default:
            }
            if (!result) {
                return false;
            }
        }
        return true;
    }

    /**
     * 对某个文件进行过滤,返回文件列表
     *
     * @param filePath
     * @param fileFilterConfigList
     * @return
     */
    public static List<File> filter(String filePath, List<FileFilterConfig> fileFilterConfigList) {
        List<File> fileList = FileUtil.loopFiles(filePath);
        if (CollUtil.isEmpty(fileList)) {
            return fileList;
        }
        return fileList.stream().filter(file -> doSingleFileFilter(fileFilterConfigList, file)).collect(Collectors.toList());

    }
}



