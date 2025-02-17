package com.ysj.weixinzhuanexecl.executor;

import com.ysj.weixinzhuanexecl.handler.FileInfo;
import com.ysj.weixinzhuanexecl.util.FileSearchUtil;
import com.ysj.weixinzhuanexecl.util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainProcessExecutor {
    public static void executeMainProcess(ArrayList<String> dataList, String imageSearchFolder, String priceDatabaseFile, JTextArea feedbackArea) throws IOException {
        Map<String, Double> priceMap = PriceReader.readPriceFromExcel(priceDatabaseFile);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String targetFolderName = sdf.format(new Date()) + "施美毛巾";
        File targetFolder = new File(targetFolderName);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }
        feedbackArea.append("输出目录为: " + targetFolder.getAbsolutePath() + "\n");

        File root = new File(imageSearchFolder);
        Map<String, String> itemToNewFileNameMap = new LinkedHashMap<>();
        for (String data : dataList) {
            boolean found = false;
            FileInfo recentFile = FileSearchUtil.searchRecentFile(root, data);
            if (recentFile != null) {
                found = true;
                Double price = null;
                // 遍历 Map 的 key 进行模糊匹配
                for (Map.Entry<String, Double> entry : priceMap.entrySet()) {
                    if (entry.getKey().contains(data)) {
                        price = entry.getValue();
                        break; // 如果只需要找到第一个匹配项，找到后就跳出循环
                    }
                }
                String newFileName;
                if (price != null) {
                    newFileName = data + "_" + price + "." + FileUtils.getFileExtension(new File(recentFile.filePath).getName());
                } else {
                    newFileName = data + "." + FileUtils.getFileExtension(new File(recentFile.filePath).getName());
                }
                File targetFile = new File(targetFolder.getAbsolutePath() + File.separator + newFileName);
                Files.copy(new File(recentFile.filePath).toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                itemToNewFileNameMap.put(data, newFileName);
                feedbackArea.append("货号: " + data + ", 价格: " + (price != null ? price : "未找到") + ", 来源: " + recentFile.filePath + ", 状态: 已检索到\n");
            }
            if (!found) {
                feedbackArea.append("货号: " + data + ", 价格: 未找到, 来源: 未找到, 状态: 未检索到\n");
            }
        }

        ExcelGenerator.generateExcel(itemToNewFileNameMap, targetFolder);

        // 尝试打开生成的 Excel 文件
        File excelFile = new File("wechat_data.xlsx");
        if (excelFile.exists()) {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(excelFile);
                } else {
                    feedbackArea.append("不支持使用桌面打开文件。\n");
                }
            } catch (IOException e) {
                feedbackArea.append("打开 Excel 文件时出错: " + e.getMessage() + "\n");
            }
        } else {
            feedbackArea.append("未找到生成的 Excel 文件。\n");
        }
    }
}