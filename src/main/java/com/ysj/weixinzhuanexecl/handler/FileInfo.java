package com.ysj.weixinzhuanexecl.handler;

public class FileInfo {
   public String filePath;
   public long lastModified;

    public FileInfo(String filePath, long lastModified) {
        this.filePath = filePath;
        this.lastModified = lastModified;
    }

}
