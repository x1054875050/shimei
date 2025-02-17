package com.ysj.weixinzhuanexecl.priceManage;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class readPriceFromExcel {
    public static void main(String[] args) throws IOException {
        // 源文件夹路径
        String sourceFolder = "C:\\Users\\sideyu\\Desktop\\tmp\\";
        // 输出文件夹路径
        String outputFolderPath = "C:\\Users\\sideyu\\Desktop\\数据表\\";

        // 获取当前日期并格式化
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
        String formattedDate = currentDate.format(formatter);

        // 生成带日期的输出文件路径
        String outputFileName = formattedDate + "汇总价格.xlsx";
        String outputFilePath = outputFolderPath + File.separator + outputFileName;

        // 提取货号和单价
        Map<String, Double> priceMap = extractPricesFromFolder(sourceFolder);

        // 生成新的 Excel 文件
        createSummaryExcel(priceMap, outputFilePath);

        System.out.println("提取完成，结果已保存到: " + outputFilePath);
    }

    /**
     * 从文件夹中提取货号和单价
     */
    private static Map<String, Double> extractPricesFromFolder(String folderPath) throws IOException {
        Map<String, Double> priceMap = new HashMap<>();
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("提供的路径不是文件夹: " + folderPath);
        }
        // 递归遍历文件夹
        traverseFolder(folder, priceMap);
        return priceMap;
    }

    /**
     * 递归遍历文件夹
     */
    private static void traverseFolder(File folder, Map<String, Double> priceMap) throws IOException {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归遍历子文件夹
                traverseFolder(file, priceMap);
            } else if (file.isFile() && (file.getName().endsWith(".xlsx") || file.getName().endsWith(".xls"))) {
                // 处理 Excel 文件
                extractPricesFromExcel(file, priceMap);
            }
        }
    }

    /**
     * 从 Excel 文件中提取货号和单价
     */
    private static void extractPricesFromExcel(File excelFile, Map<String, Double> priceMap) throws IOException {
        Workbook workbook;
        try (FileInputStream fileInputStream = new FileInputStream(excelFile)) {
            // 根据文件扩展名选择 Workbook 实现
            if (excelFile.getName().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fileInputStream); // 处理 .xlsx 文件
            } else if (excelFile.getName().endsWith(".xls")) {
                workbook = new HSSFWorkbook(fileInputStream); // 处理 .xls 文件
            } else {
                System.err.println("不支持的文件格式: " + excelFile.getName());
                return;
            }
        }

        Sheet sheet = workbook.getSheetAt(0); // 读取第一个工作表

        // 1. 查找表头行
        int headerRowIndex = findHeaderRow(sheet);
        if (headerRowIndex == -1) {
            System.err.println("未找到表头行（文件: " + excelFile.getName() + "）");
            workbook.close();
            return;
        }

        // 2. 查找货号和单价列的索引
        Row headerRow = sheet.getRow(headerRowIndex);
        int skuColumn = -1; // 货号列索引
        int priceColumn = -1; // 单价列索引
        for (Cell cell : headerRow) {
            if (skuColumn != -1 && priceColumn != -1) {
                break;
            }
            String header = getCellValueAsString(cell); // 获取单元格值
            if (header.equals("货号") || header.equals("商品编号")||header.contains("名称")) {
                skuColumn = cell.getColumnIndex();
            } else if (header.contains("单价") || header.contains("价格")) {
                priceColumn = cell.getColumnIndex();
            }
        }

        if (skuColumn == -1 || priceColumn == -1) {
            System.err.println("未找到货号或单价列（文件: " + excelFile.getName() + "）");
            workbook.close();
            return;
        }

        // 3. 提取数据
        for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue; // 跳过空行
            }

            Cell skuCell = row.getCell(skuColumn); // 货号列
            Cell priceCell = row.getCell(priceColumn); // 价格列

            if (skuCell == null || priceCell == null) {
                continue; // 跳过空单元格
            }

            // 提取货号（兼容字符串和数值类型）
            String sku = getCellValueAsString(skuCell).trim();

            // 忽略货号为空的行
            if (sku.isEmpty()) {
                continue;
            }

            // 提取价格（兼容字符串和数值类型）
            double price;
            try {
                price = getCellValueAsNumeric(priceCell);
            } catch (NumberFormatException e) {
                System.err.println("价格格式错误（货号: " + sku + ", 文件: " + excelFile.getName() + "）: " + e.getMessage());
                continue;
            }

            // 保存有效数据
            if (price <= 100) {
                priceMap.put(sku, price);
            } else {
                System.err.println("忽略超过100元的价格（货号: " + sku + ", 价格: " + price + "）");
            }
        }

        workbook.close();
    }

    /**
     * 查找表头行
     */
    private static int findHeaderRow(Sheet sheet) {
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue; // 跳过空行
            }
            for (Cell cell : row) {
                String cellValue = getCellValueAsString(cell);
                if (cellValue.contains("货号") || cellValue.equals("商品编号") || cellValue.equals("单价") || cellValue.equals("价格")|| cellValue.contains("名称")) {
                    return i; // 返回表头行索引
                }
            }
        }
        return -1; // 未找到表头行
    }

    /**
     * 获取单元格的字符串值（兼容所有类型）
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue()); // 假设货号为整数
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 获取单元格的数值（兼容所有类型）
     */
    private static double getCellValueAsNumeric(Cell cell) throws NumberFormatException {
        if (cell == null) {
            throw new NumberFormatException("单元格为空");
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                String value = cell.getStringCellValue().replaceAll("[^\\d.]", ""); // 移除非数字字符
                return Double.parseDouble(value);
            default:
                throw new NumberFormatException("单元格类型不支持转换为数值");
        }
    }

    /**
     * 生成汇总的 Excel 文件
     */
    private static void createSummaryExcel(Map<String, Double> priceMap, String outputFilePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("汇总价格");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("货号");
        headerRow.createCell(1).setCellValue("单价");
        headerRow.createCell(2).setCellValue("纯货号");

        // 写入数据
        int rowNum = 1;
        for (Map.Entry<String, Double> entry : priceMap.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            String sku = entry.getKey();
            row.createCell(0).setCellValue(sku); // 货号
            row.createCell(1).setCellValue(entry.getValue()); // 单价

            // 提取纯货号，去掉中文和括号
            String pureSku = sku.replaceAll("[\\u4e00-\\u9fa5()（）]", "");
            row.createCell(2).setCellValue(pureSku); // 纯货号
        }

        // 自动调整列宽
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        // 保存文件
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {
            workbook.write(fileOutputStream);
        }
        workbook.close();
    }
}