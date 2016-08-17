package com.application.gritstone.utils;

import java.io.File;
import java.util.Comparator;

/**
 * Created by guorenjie on 2016/5/21.
 */
public class FileComparator implements Comparator<File> {
    @Override
    public int compare(File file1, File file2) {
        //1.比较文件夹和文件夹，且以A-Z顺序排列
        if(file1.isDirectory()&&file2.isDirectory()){
            return file1.getName().compareToIgnoreCase(file2.getName());
        }else{
            //2.比较文件夹和文件
            if(file1.isDirectory()&&!file2.isDirectory()){
                return -1;
            }else{
                //3.比较文件和文件夹
                if(!file1.isDirectory()&&file2.isDirectory()){
                    return 1;
                }else{
                    //4.比较文件和文件
                    return file1.getName().compareToIgnoreCase(file2.getName());
                }
            }
        }
    }

}
