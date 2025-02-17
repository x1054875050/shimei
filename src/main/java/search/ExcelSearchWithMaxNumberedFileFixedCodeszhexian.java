//package search;
//
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.DateAxis;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.data.time.Day;
//import org.jfree.data.time.TimeSeries;
//import org.jfree.data.time.TimeSeriesCollection;
//import org.jfree.data.xy.XYDataset;
//
//import java.awt.*;
//import java.io.*;
//import java.net.URL;
//import java.net.URLConnection;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class ExcelSearchWithMaxNumberedFileFixedCodeszhexian {
//    public static void main(String[] args) {
//        String folderPath = "C:\\Users\\sideyu\\Downloads";
//        String downloadUrl = "https://omwb.oss-cn-beijing.aliyuncs.com/zjgwy2025/d6dbd18a468e503dbe34b174ee1d6ab1fa72494638d7201f";
//
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    // 获取计算机当前时间并进行半小时向前取整
//                    Date currentTime = new Date();
//                    long timeInMillis = currentTime.getTime();
//                    long roundedTimeInMillis = timeInMillis - (timeInMillis % (30 * 60 * 1000));
//                    Date roundedTime = new Date(roundedTimeInMillis);
//
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH_mm");
//                    String timePart = dateFormat.format(roundedTime);
//
//                    String baseFileName = "职位报考统计_" + timePart + "_00统计";
//                    String fileExtension = ".xls";
//
//                    // 检查是否存在符合条件的本地文件，如果没有则从链接下载
//                    File targetFile = findMaxNumberedFileByPattern(folderPath, baseFileName, fileExtension);
//                    if (targetFile == null) {
//                        targetFile = downloadFileFromUrl(folderPath, downloadUrl, baseFileName, fileExtension);
//                        if (targetFile == null) {
//                            System.out.println("无法下载文件且本地也没有符合条件的文件。");
//                            return;
//                        }
//                    }
//
//                    System.out.println("找到的文件名为：" + targetFile.getName());
//
//                    Workbook workbook;
//                    try (FileInputStream fis = new FileInputStream(targetFile)) {
//                        if (targetFile.getName().endsWith(".xls")) {
//                            workbook = new HSSFWorkbook(fis);
//                        } else if (targetFile.getName().endsWith(".xlsx")) {
//                            workbook = new XSSFWorkbook(fis);
//                        } else {
//                            System.out.println("不支持的文件格式：" + targetFile.getName());
//                            return;
//                        }
//                    }
//
//                    Sheet sheet = workbook.getSheetAt(0);
//
//                    String[] codes = {"13309002002000001", "13306003019000009", "13306002030000003", "13306003015000001"};
//
//                    System.out.println("匹配结果：");
//                    for (Row row : sheet) {
//                        if (row.getRowNum() == 0) continue;
//                        for (String code : codes) {
//                            Cell positionCodeCell = row.getCell(2);
//                            if (positionCodeCell!= null && positionCodeCell.getCellType() == CellType.STRING && positionCodeCell.getStringCellValue().equals(code)) {
//                                for (Cell cell : row) {
//                                    System.out.print(getCellValue(cell) + "\t");
//                                }
//                                System.out.println();
//                                break;
//                            }
//                        }
//                    }
//
//                    // 假设数据在第三列是时间，第四列是数值，生成折线图
//                    TimeSeries series = new TimeSeries("数值变化");
//                    for (Row row : sheet) {
//                        if (row.getRowNum() > 0) {
//                            Cell timeCell = row.getCell(2);
//                            Cell valueCell = row.getCell(3);
//                            if (timeCell!= null && valueCell!= null && DateUtil.isCellDateFormatted(timeCell)) {
//                                Date date = timeCell.getDateCellValue();
//                                double value = valueCell.getNumericCellValue();
//                                series.add(new Day(date), value);
//                            }
//                        }
//                    }
//
//                    XYDataset dataset = new TimeSeriesCollection(series);
//                    JFreeChart chart = ChartFactory.createTimeSeriesChart("数值随时间变化", "时间", "数值", dataset);
//                    XYPlot plot = chart.getXYPlot();
//                    DateAxis axis = (DateAxis) plot.getDomainAxis();
//                    axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
//
//                    ChartPanel panel = new ChartPanel(chart);
//                    Frame frame = new Frame("折线图");
//                    frame.add(panel);
//                    frame.setSize(800, 600);
//                    frame.setVisible(true);
//
//                    workbook.close();
//                } catch (IOException e) {
//                    System.out.println("发生错误：" + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }, 0, 30 * 60 * 1000);
//    }
//
//    private static String getCellValue(Cell cell) {
//        if (cell == null) return "";
//        switch (cell.getCellType()) {
//            case STRING:
//                return cell.getStringCellValue();
//            case NUMERIC:
//                if (DateUtil.isCellDateFormatted(cell)) {
//                    return cell.getDateCellValue().toString();
//                } else {
//                    return Double.toString(cell.getNumericCellValue());
//                }
//            case BOOLEAN:
//                return Boolean.toString(cell.getBooleanCellValue());
//            default:
//                return "";
//        }
//    }
//
//    private static File findMaxNumberedFileByPattern(String folderPath, String baseFileName, String fileExtension) {
//        File directory = new File(folderPath);
//        List<File> candidateFiles = new ArrayList<>();
//        for (File file : directory.listFiles()) {
//            if (file.getName().startsWith(baseFileName) && file.getName().endsWith(fileExtension)) {
//                candidateFiles.add(file);
//            }
//        }
//
//        if (candidateFiles.isEmpty()) {
//            return null;
//        }
//
//        int maxNumber = -1;
//        File maxNumberedFile = null;
//        for (File file : candidateFiles) {
//            String fileName = file.getName();
//            int numberIndex = fileName.indexOf("(");
//            if (numberIndex > 0) {
//                String numberPart = fileName.substring(numberIndex + 1, fileName.indexOf(")"));
//                int number = Integer.parseInt(numberPart);
//                if (number > maxNumber) {
//                    maxNumber = number;
//                    maxNumberedFile = file;
//                }
//            } else {
//                // 如果没有数字后缀，认为它是最大的（如果只有一个文件时）
//                if (maxNumberedFile == null) {
//                    maxNumberedFile = file;
//                }
//            }
//        }
//        return maxNumberedFile;
//    }
//
//    private static File downloadFileFromUrl(String folderPath, String urlStr, String baseFileName, String fileExtension) {
//        try {
//            URL url = new URL(urlStr);
//            URLConnection connection = url.openConnection();
//            InputStream inputStream = connection.getInputStream();
//
//            File downloadFolder = new File(folderPath);
//            if (!downloadFolder.exists()) {
//                downloadFolder.mkdirs();
//            }
//
//            File downloadedFile = new File(downloadFolder, baseFileName + fileExtension);
//            try (OutputStream outputStream = new FileOutputStream(downloadedFile)) {
//                byte[] buffer = new byte[4096];
//                int bytesRead;
//                while ((bytesRead = inputStream.read(buffer))!= -1) {
//                    outputStream.write(buffer, 0, bytesRead);
//                }
//            }
//
//            return downloadedFile;
//        } catch (IOException e) {
//            System.out.println("下载文件时发生错误：" + e.getMessage());
//            return null;
//        }
//    }
//}