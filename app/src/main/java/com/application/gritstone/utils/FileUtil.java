package com.application.gritstone.utils;



import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by guorenjie on 2016/5/21.
 */
public class FileUtil {
    public static File[] sort(File[] listfiles){
        if(listfiles!=null) {
            List<File> list = Arrays.asList(listfiles);
            Collections.sort(list, new FileComparator());
            File[] array = list.toArray(new File[list.size()]);
            return array;
        }else
            return null;
    }
}
