package com.ysj.weixinzhuanexecl.executor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PriceReader {
    public static Map<String, Double> readPriceFromExcel(String excelFilePath) {
        Map<String, Double> priceMap = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell itemCell = row.getCell(0);
                Cell priceCell = row.getCell(1);
                if (itemCell != null && priceCell != null) {
                    String item = itemCell.getStringCellValue();
                    double price = priceCell.getNumericCellValue();
                    priceMap.put(item, price);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return priceMap;
    }
}