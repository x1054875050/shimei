package com.ysj.weixinzhuanexecl.executor;

import com.ysj.weixinzhuanexecl.handler.FileHandler;
import com.ysj.weixinzhuanexecl.util.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

public class ExcelGenerator {
    public static void generateExcel(Map<String, String> itemToNewFileNameMap, File targetFolder) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font secondRowFont = workbook.createFont();
        secondRowFont.setFontHeightInPoints((short) 11);

        CellStyle secondRowStyle = workbook.createCellStyle();
        secondRowStyle.setFont(secondRowFont);
        secondRowStyle.setAlignment(HorizontalAlignment.CENTER);
        secondRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Row nameRow = sheet.createRow(0);
        Cell nameCell = nameRow.createCell(0);
        nameCell.setCellValue("施美毛巾");
        nameCell.setCellStyle(style);

        Row secondRow = sheet.createRow(1);
        Cell indexCell = secondRow.createCell(0);
        indexCell.setCellValue("序号");
        indexCell.setCellStyle(secondRowStyle);
        Cell pictureCell = secondRow.createCell(1);
        pictureCell.setCellValue("图片");
        pictureCell.setCellStyle(secondRowStyle);

        int rowNum = 2;
        for (Map.Entry<String, String> entry : itemToNewFileNameMap.entrySet()) {
            String data = entry.getKey();
            String newFileName = entry.getValue();
            String path = targetFolder.getAbsolutePath() + File.separator + newFileName;

            Row row = sheet.createRow(rowNum);
            row.setHeightInPoints(160);
            sheet.setColumnWidth(1, 29 * 256);

            for (int col = 2; col < row.getPhysicalNumberOfCells(); col++) {
                sheet.setColumnWidth(col, sheet.getColumnWidth(1));
            }

            for (int col = 0; col < row.getPhysicalNumberOfCells(); col++) {
                Cell cell = row.getCell(col);
                if (cell == null) {
                    cell = row.createCell(col);
                }
                cell.setCellStyle(secondRowStyle);
            }

            Cell indexCellInData = row.createCell(0);
            indexCellInData.setCellValue(rowNum - 1);
            indexCellInData.setCellStyle(secondRowStyle);

            String[] parts = path.split("\\\\");
            String lastPart = parts[parts.length - 1];
            if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".JPG") || path.endsWith(".PNG")) {
                lastPart = lastPart.substring(0, lastPart.length() - 4);
            }
            Cell pictureCellInData = row.createCell(1);

            if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".JPG") || path.endsWith(".PNG")) {
                try {
                    File imageFile = new File(path);
                    int pictureIdx = workbook.addPicture(FileHandler.getBytesFromFile(imageFile), Workbook.PICTURE_TYPE_JPEG);
                    sheet.createDrawingPatriarch().createPicture(new XSSFClientAnchor(100, 100, 100, 100, 1, rowNum, 1 + 1, rowNum + 1), pictureIdx);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            String[] lastPartParts = lastPart.split("_");
            int colIndex = 2;
            for (String part : lastPartParts) {
                Cell partCell = row.createCell(colIndex);
                partCell.setCellValue(part);
                partCell.setCellStyle(secondRowStyle);
                colIndex++;
            }

            rowNum++;
        }

        int maxColumn = 0;
        for (Row row : sheet) {
            if (row.getPhysicalNumberOfCells() > maxColumn) {
                maxColumn = row.getPhysicalNumberOfCells();
            }
        }
        System.out.println("当前表格有 " + maxColumn + " 列。");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxColumn - 1));

        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == CellType.STRING && StringUtils.isNumeric(cell.getStringCellValue())) {
                    try {
                        NumberFormat format = NumberFormat.getInstance();
                        Number number = format.parse(cell.getStringCellValue());
                        cell.setCellValue(number.doubleValue());
                    } catch (ParseException ex) {
                        // 如果不是可转换为数字的文本，忽略
                    }
                }
            }
        }

        try (FileOutputStream outputStream = new FileOutputStream(new File("wechat_data.xlsx"))) {
            workbook.write(outputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}