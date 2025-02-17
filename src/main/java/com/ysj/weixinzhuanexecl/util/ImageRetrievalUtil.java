package com.ysj.weixinzhuanexecl.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class ImageRetrievalUtil {
    // 获取图片全路径的方法
    public static String[] getFullImagePaths(String rootPath) {
        File rootFolder = new File(rootPath);
        List<String> fullPaths = new ArrayList<>();
        traverseFolderForFullPaths(rootFolder, fullPaths);
        return fullPaths.toArray(new String[0]);
    }

    // 内部递归方法用于获取全路径
    private static void traverseFolderForFullPaths(File folder, List<String> fullPaths) {
        File[] files = folder.listFiles(new ImageFileFilter());
        if (files!= null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    traverseFolderForFullPaths(file, fullPaths);
                } else {
                    fullPaths.add(file.getAbsolutePath());
                }
            }
        }
    }

    // 获取图片名称的方法（去除后缀名）
    public static String[] getImageNames(String rootPath) {
        File rootFolder = new File(rootPath);
        List<String> imageNames = new ArrayList<>();
        traverseFolderForNames(rootFolder, imageNames);
        String[] names = new String[imageNames.size()];
        for (int i = 0; i < imageNames.size(); i++) {
            String name = imageNames.get(i);
            names[i] = name.substring(0, name.lastIndexOf('.'));
        }
        return names;
    }

    // 内部递归方法用于获取名称
    private static void traverseFolderForNames(File folder, List<String> imageNames) {
        File[] files = folder.listFiles(new ImageFileFilter());
        if (files!= null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    traverseFolderForNames(file, imageNames);
                } else {
                    imageNames.add(file.getName());
                }
            }
        }
    }

    static class ImageFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            String fileName = file.getName().toLowerCase();
            return fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif");
        }
    }

    public static void main(String[] args) {
        String rootPath = "C:\\Users\\sideyu\\Desktop\\tmp\\20240620";
        String[] fullPaths = getFullImagePaths(rootPath);
        String[] names = getImageNames(rootPath);

        // 打印全路径
        for (String path : fullPaths) {
            System.out.println(path);
        }

        // 打印名称
        for (String name : names) {
            System.out.println(name);
        }
    }
}
