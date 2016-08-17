package com.application.gritstone.utils;

/**
 * Created by guorenjie on 2016/5/23.
 */
import java.io.File;
import java.io.FileFilter;

public class MyFileFilter implements FileFilter {

    //	@Override
    public boolean accept(File pathname) {
        if (!pathname.getName().startsWith(".")) {
            return true;
        } else {
            return false;
        }
    }

}