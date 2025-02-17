//package com.ysj.weixinzhuanexecl.mainentry;
//import com.ysj.weixinzhuanexecl.handler.FileHandler;
//import com.ysj.weixinzhuanexecl.handler.FileInfo;
//import com.ysj.weixinzhuanexecl.util.ImageRetrievalUtil;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.*;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
//import java.text.NumberFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//public class MainEntry {
//    public static void main(String[] args) {
//        try {
//            // 执行第一份代码的功能
//            String sourceFolder = "C:\\Users\\sideyu\\Desktop\\tmp";
//            String dataFolder = "C:\\Users\\sideyu\\Desktop\\数据表";
//            ArrayList<String> dataList = new ArrayList<>();
//            String[] dataArray = {"1122", "7780", "7115", "7550", "9991", "7889", "9112", "7887"};
//            for (String data : dataArray) {
//                dataList.add(data);
//            }
//
//            Map<String, Double> priceMap = readPriceFromExcel(dataFolder + File.separator + "汇总价格_新.xlsx");
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//            String targetFolderName = sdf.format(new Date()) + "施美毛巾";
//            File targetFolder = new File(targetFolderName);
//            if (!targetFolder.exists()) {
//                targetFolder.mkdirs();
//            }
//            System.out.println("输出目录为: " + targetFolder.getAbsolutePath());
//
//            File root = new File(sourceFolder);
//            for (String data : dataList) {
//                boolean found = false;
//                FileInfo recentFile = searchRecentFile(root, data);
//                if (recentFile != null) {
//                    found = true;
//                    Double price = priceMap.get(data);
//                    String newFileName;
//                    if (price != null) {
//                        newFileName = data + "_" + price + "." + getFileExtension(new File(recentFile.filePath).getName());
//                    } else {
//                        newFileName = data + "." + getFileExtension(new File(recentFile.filePath).getName());
//                    }
//                    File targetFile = new File(targetFolder.getAbsolutePath() + File.separator + newFileName);
//                    Files.copy(new File(recentFile.filePath).toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                }
//                if (!found) {
//                    System.out.println("没有找到包含 " + data + " 的文件");
//                }
//            }
//
//            // 执行第二份代码的功能
//            String[] fullImagePaths = ImageRetrievalUtil.getFullImagePaths(targetFolderName);
//
//            Workbook workbook = new XSSFWorkbook();
//            Sheet sheet = workbook.createSheet("Data");
//
//            // 创建字体样式，设置字体大小和加粗
//            Font font = workbook.createFont();
//            font.setFontHeightInPoints((short) 16);
//            font.setBold(true);
//
//            // 创建单元格样式并应用字体样式
//            CellStyle style = workbook.createCellStyle();
//            style.setFont(font);
//            style.setAlignment(HorizontalAlignment.CENTER); // 设置单元格内容居中
//            style.setVerticalAlignment(VerticalAlignment.CENTER); // 设置单元格内容垂直居中
//
//            // 创建第二行的字体样式，字体大小为 11 号
//            Font secondRowFont = workbook.createFont();
//            secondRowFont.setFontHeightInPoints((short) 11);
//
//            // 创建第二行的单元格样式并应用字体样式
//            CellStyle secondRowStyle = workbook.createCellStyle();
//            secondRowStyle.setFont(secondRowFont);
//            secondRowStyle.setAlignment(HorizontalAlignment.CENTER);
//            secondRowStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 设置垂直居中
//
//            // 在第一行添加“施美毛巾”并应用样式
//            Row nameRow = sheet.createRow(0);
//            Cell nameCell = nameRow.createCell(0);
//            nameCell.setCellValue("施美毛巾");
//            nameCell.setCellStyle(style);
//
//            // 在第二行添加“序号”和“图片”并应用样式
//            Row secondRow = sheet.createRow(1);
//            Cell indexCell = secondRow.createCell(0);
//            indexCell.setCellValue("序号");
//            indexCell.setCellStyle(secondRowStyle);
//            Cell pictureCell = secondRow.createCell(1);
//            pictureCell.setCellValue("图片");
//            pictureCell.setCellStyle(secondRowStyle);
//
//            int rowNum = 2; // 从第三行开始添加数据
//            for (String path : fullImagePaths) {
//                Row row = sheet.createRow(rowNum);
//                // 设置行高为 100 磅
//                row.setHeightInPoints(160);
//                // 设置第二列列宽为 29 个字符宽度
//                sheet.setColumnWidth(1, 29 * 256);
//
//                // 从第三列开始列宽设置为与第二列相同
//                for (int col = 2; col < row.getPhysicalNumberOfCells(); col++) {
//                    sheet.setColumnWidth(col, sheet.getColumnWidth(1));
//                }
//
//                // 设置整行单元格样式为居中
//                for (int col = 0; col < row.getPhysicalNumberOfCells(); col++) {
//                    Cell cell = row.getCell(col);
//                    if (cell == null) {
//                        cell = row.createCell(col);
//                    }
//                    cell.setCellStyle(secondRowStyle);
//                }
//
//                // 设置序号列
//                Cell indexCellInData = row.createCell(0);
//                indexCellInData.setCellValue(rowNum - 1);
//                indexCellInData.setCellStyle(secondRowStyle);
//
//                String[] parts = path.split("\\\\");
//                String lastPart = parts[parts.length - 1];
//                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".JPG") || path.endsWith(".PNG")) {
//                    lastPart = lastPart.substring(0, lastPart.length() - 4);
//                }
//                // 设置图片列
//                Cell pictureCellInData = row.createCell(1);
//
//                // 处理图片插入逻辑
//                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".JPG") || path.endsWith(".PNG")) {
//                    try {
//                        File imageFile = new File(path);
//                        int pictureIdx = workbook.addPicture(FileHandler.getBytesFromFile(imageFile), Workbook.PICTURE_TYPE_JPEG);
//                        sheet.createDrawingPatriarch().createPicture(new XSSFClientAnchor(100, 100, 100, 100, 1, rowNum, 1 + 1, rowNum + 1), pictureIdx);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                String[] lastPartParts = lastPart.split("_");
//                int colIndex = 2;
//                for (String part : lastPartParts) {
//                    Cell partCell = row.createCell(colIndex);
//                    partCell.setCellValue(part);
//                    partCell.setCellStyle(secondRowStyle);
//                    colIndex++;
//                }
//
//                rowNum++;
//            }
//
//            int maxColumn = 0;
//            for (Row row : sheet) {
//                if (row.getPhysicalNumberOfCells() > maxColumn) {
//                    maxColumn = row.getPhysicalNumberOfCells();
//                }
//            }
//            System.out.println("当前表格有 " + maxColumn + " 列。");
//            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, maxColumn - 1));
//
//            for (Row row : sheet) {
//                for (Cell cell : row) {
//                    if (cell.getCellType() == CellType.STRING && isNumeric(cell.getStringCellValue())) {
//                        try {
//                            NumberFormat format = NumberFormat.getInstance();
//                            Number number = format.parse(cell.getStringCellValue());
//                            cell.setCellValue(number.doubleValue());
//                        } catch (ParseException e) {
//                            // 如果不是可转换为数字的文本，忽略
//                        }
//                    }
//                }
//            }
//
//            try (FileOutputStream outputStream = new FileOutputStream(new File("wechat_data.xlsx"))) {
//                workbook.write(outputStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static FileInfo searchRecentFile(File root, String data) {
//        FileInfo recentFile = null;
//        if (root.isDirectory()) {
//            File[] files = root.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    FileInfo subRecentFile = searchRecentFile(file, data);
//                    if (subRecentFile != null) {
//                        if (recentFile == null || subRecentFile.lastModified > recentFile.lastModified) {
//                            recentFile = subRecentFile;
//                        }
//                    }
//                }
//            }
//        } else {
//            String fileName = root.getName();
//            if (fileName.contains(data)) {
//                long lastModified = root.lastModified();
//                if (recentFile == null || lastModified > recentFile.lastModified) {
//                    recentFile = new FileInfo(root.getAbsolutePath(), lastModified);
//                }
//            }
//        }
//        return recentFile;
//    }
//
//    private static String getFileExtension(String fileName) {
//        int index = fileName.lastIndexOf('.');
//        if (index > 0 && index < fileName.length() - 1) {
//            return fileName.substring(index + 1);
//        }
//        return "";
//    }
//
//    private static Map<String, Double> readPriceFromExcel(String excelFilePath) {
//        Map<String, Double> priceMap = new HashMap<>();
//        try (FileInputStream fis = new FileInputStream(excelFilePath);
//             Workbook workbook = new XSSFWorkbook(fis)) {
//            Sheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//            // 跳过表头行
//            if (rowIterator.hasNext()) {
//                rowIterator.next();
//            }
//            while (rowIterator.hasNext()) {
//                Row row = rowIterator.next();
//                Cell itemCell = row.getCell(2);
//                Cell priceCell = row.getCell(1);
//                if (itemCell != null && priceCell != null) {
//                    String item = itemCell.getStringCellValue();
//                    double price = priceCell.getNumericCellValue();
//                    priceMap.put(item, price);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return priceMap;
//    }
//
//    public static boolean isNumeric(String str) {
//        try {
//            Double.parseDouble(str);
//            return true;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//}
