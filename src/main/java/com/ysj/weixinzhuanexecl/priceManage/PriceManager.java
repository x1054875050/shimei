package com.ysj.weixinzhuanexecl.priceManage;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class PriceManager {
    public static Map<String, Double> readPriceFromExcel(String excelFilePath) throws IOException {
        Map<String, Double> priceMap = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表

            // 从第二行开始读取数据（跳过表头）
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue; // 跳过空行
                }

                // 读取货号列（索引 0）
                Cell itemCell = row.getCell(0);
                // 读取单价列（索引 1）
                Cell priceCell = row.getCell(1);

                if (itemCell != null && priceCell != null) {
                    String item = itemCell.getStringCellValue(); // 读取货号
                    double price = 0.0;

                    // 根据单元格类型读取价格
                    if (priceCell.getCellType() == CellType.NUMERIC) {
                        price = priceCell.getNumericCellValue(); // 数值类型
                    } else if (priceCell.getCellType() == CellType.STRING) {
                        try {
                            // 尝试将字符串转换为数值
                            price = Double.parseDouble(priceCell.getStringCellValue().trim());
                        } catch (NumberFormatException e) {
                            System.err.println("价格格式错误，无法转换为数值: " + priceCell.getStringCellValue());
                            continue; // 跳过无效数据
                        }
                    }

                    priceMap.put(item, price); // 将货号和价格存入 Map
                }
            }
        }
        return priceMap;
    }
}
