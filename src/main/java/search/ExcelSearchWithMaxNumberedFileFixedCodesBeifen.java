package search;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelSearchWithMaxNumberedFileFixedCodesBeifen {
    public static void main(String[] args) {
        String folderPath = "C:\\Users\\sideyu\\Downloads";

        try {
            // 获取计算机当前时间并进行半小时向前取整
            Date currentTime = new Date();
            long timeInMillis = currentTime.getTime();
            long roundedTimeInMillis = timeInMillis - (timeInMillis % (30 * 60 * 1000));
            Date roundedTime = new Date(roundedTimeInMillis);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH_mm");
            String timePart = dateFormat.format(roundedTime);

            String fullFileNamePattern = "职位报考统计_" + timePart + "_00统计.xls";

            File targetFile = findFileByPattern(folderPath, fullFileNamePattern);
            if (targetFile == null) {
                System.out.println("没有找到符合条件的 Excel 文件。");
                return;
            }

            System.out.println("找到的文件名为：" + targetFile.getName());

            Workbook workbook;
            try (FileInputStream fis = new FileInputStream(targetFile)) {
                if (targetFile.getName().endsWith(".xls")) {
                    workbook = new HSSFWorkbook(fis);
                } else if (targetFile.getName().endsWith(".xlsx")) {
                    workbook = new XSSFWorkbook(fis);
                } else {
                    System.out.println("不支持的文件格式：" + targetFile.getName());
                    return;
                }
            }

            Sheet sheet = workbook.getSheetAt(0);

            String[] codes = {"13309002002000001", "13306003019000009", "13306002030000003", "13306003015000001"};

            System.out.println("匹配结果：");
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                for (String code : codes) {
                    Cell positionCodeCell = row.getCell(2);
                    if (positionCodeCell!= null && positionCodeCell.getCellType() == CellType.STRING && positionCodeCell.getStringCellValue().equals(code)) {
                        for (Cell cell : row) {
                            System.out.print(getCellValue(cell) + "\t");
                        }
                        System.out.println();
                        break;
                    }
                }
            }

            workbook.close();
        } catch (IOException e) {
            System.out.println("发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return Double.toString(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private static File findFileByPattern(String folderPath, String pattern) {
        File directory = new File(folderPath);
        File[] files = directory.listFiles((dir, name) -> name.matches(pattern));
        if (files == null || files.length == 0) return null;
        return files[0];
    }
}