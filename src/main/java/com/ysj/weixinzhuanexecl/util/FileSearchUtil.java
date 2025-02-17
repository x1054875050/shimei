package com.ysj.weixinzhuanexecl.util;

import com.ysj.weixinzhuanexecl.handler.FileInfo;

import java.io.File;

public class FileSearchUtil {
    public static FileInfo searchRecentFile(File root, String data) {
        FileInfo recentFile = null;
        if (root.isDirectory()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    FileInfo subRecentFile = searchRecentFile(file, data);
                    if (subRecentFile != null) {
                        if (recentFile == null || subRecentFile.lastModified > recentFile.lastModified) {
                            recentFile = subRecentFile;
                        }
                    }
                }
            }
        } else {
            String fileName = root.getName();
            if (fileName.contains(data)) {
                long lastModified = root.lastModified();
                if (recentFile == null || lastModified > recentFile.lastModified) {
                    recentFile = new FileInfo(root.getAbsolutePath(), lastModified);
                }
            }
        }
        return recentFile;
    }


}