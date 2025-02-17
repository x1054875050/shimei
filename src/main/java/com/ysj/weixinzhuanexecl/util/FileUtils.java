package com.ysj.weixinzhuanexecl.util;

public class FileUtils {
    public static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            return fileName.substring(index + 1);
        }
        return "";
    }
}