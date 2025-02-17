//package com.ysj.weixinzhuanexecl.mainentry;
//
//import com.ysj.weixinzhuanexecl.handler.FileHandler;
//import com.ysj.weixinzhuanexecl.handler.FileInfo;
//import org.apache.poi.ss.usermodel.Font;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
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
//
//public class MainEntryWithGUIUpdate extends JFrame {
//    private JTextField inputField;
//    private JButton confirmButton;
//    private JTextField imageSearchFolderField;
//    private JTextField priceDatabaseFileField;
//    private JButton imageSearchFolderSelectButton;
//    private JButton priceDatabaseFileSelectButton;
//
//    public MainEntryWithGUIUpdate() {
//        try {
//            // 在程序启动时设置系统默认的外观和感觉
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            UIManager.put("FileChooser.useSystemExtensionHiding", true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        setTitle("货号输入界面");
//        setSize(600, 350);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLayout(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(5, 5, 5, 5);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        // 图片搜索文件夹标签和输入框
//        JLabel imageSearchFolderLabel = new JLabel("图片搜索文件夹:");
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        add(imageSearchFolderLabel, gbc);
//
//        imageSearchFolderField = new JTextField("C:\\Users\\sideyu\\Desktop\\tmp");
//        gbc.gridx = 1;
//        gbc.gridy = 0;
//        gbc.weightx = 0.8;
//        add(imageSearchFolderField, gbc);
//
//        // 图片搜索文件夹选择按钮
//        imageSearchFolderSelectButton = new JButton("选择文件夹");
//        imageSearchFolderSelectButton.setPreferredSize(new Dimension(100, 25));
//        gbc.gridx = 2;
//        gbc.gridy = 0;
//        gbc.weightx = 0.2;
//        add(imageSearchFolderSelectButton, gbc);
//
//        imageSearchFolderSelectButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser fileChooser = new JFileChooser();
//                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//                int result = fileChooser.showOpenDialog(MainEntryWithGUIUpdate.this);
//                if (result == JFileChooser.APPROVE_OPTION) {
//                    File selectedFolder = fileChooser.getSelectedFile();
//                    imageSearchFolderField.setText(selectedFolder.getAbsolutePath());
//                }
//            }
//        });
//
//        // 价格数据库文件标签和输入框
//        JLabel priceDatabaseFileLabel = new JLabel("价格数据库文件:");
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        add(priceDatabaseFileLabel, gbc);
//
//        priceDatabaseFileField = new JTextField("C:\\Users\\sideyu\\Desktop\\数据表\\汇总价格_新.xlsx");
//        gbc.gridx = 1;
//        gbc.gridy = 1;
//        gbc.weightx = 0.8;
//        add(priceDatabaseFileField, gbc);
//
//        // 价格数据库文件选择按钮
//        priceDatabaseFileSelectButton = new JButton("选择文件");
//        priceDatabaseFileSelectButton.setPreferredSize(new Dimension(100, 25));
//        gbc.gridx = 2;
//        gbc.gridy = 1;
//        gbc.weightx = 0.2;
//        add(priceDatabaseFileSelectButton, gbc);
//
//        priceDatabaseFileSelectButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser fileChooser = new JFileChooser();
//                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//                int result = fileChooser.showOpenDialog(MainEntryWithGUIUpdate.this);
//                if (result == JFileChooser.APPROVE_OPTION) {
//                    File selectedFile = fileChooser.getSelectedFile();
//                    priceDatabaseFileField.setText(selectedFile.getAbsolutePath());
//                }
//            }
//        });
//
//        // 货号输入标签和输入框
//        JLabel label = new JLabel("请输入要检索的货号，用逗号分隔：");
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        add(label, gbc);
//
//        inputField = new JTextField(30);
//        gbc.gridx = 1;
//        gbc.gridy = 2;
//        gbc.gridwidth = 2;
//        add(inputField, gbc);
//
//        inputField.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                    confirmButton.doClick();
//                }
//            }
//        });
//
//        // 确认按钮
//        confirmButton = new JButton("确认");
//        gbc.gridx = 1;
//        gbc.gridy = 3;
//        gbc.gridwidth = 1;
//        add(confirmButton, gbc);
//
//        confirmButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String input = inputField.getText();
//                input = input.replace('，', ',');
//                if (!input.isEmpty()) {
//                    String[] dataArray = input.split(",");
//                    ArrayList<String> dataList = new ArrayList<>(Arrays.asList(dataArray));
//                    String imageSearchFolder = imageSearchFolderField.getText();
//                    String priceDatabaseFile = priceDatabaseFileField.getText();
//                    try {
//                        executeMainProcess(dataList, imageSearchFolder, priceDatabaseFile);
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        setVisible(true);
//    }
//
//    private void executeMainProcess(ArrayList<String> dataList, String imageSearchFolder, String priceDatabaseFile) throws IOException {
//        Map<String, Double> priceMap = readPriceFromExcel(priceDatabaseFile);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        String targetFolderName = sdf.format(new Date()) + "施美毛巾";
//        File targetFolder = new File(targetFolderName);
//        if (!targetFolder.exists()) {
//            targetFolder.mkdirs();
//        }
//        System.out.println("输出目录为: " + targetFolder.getAbsolutePath());
//
//        File root = new File(imageSearchFolder);
//        Map<String, String> itemToNewFileNameMap = new LinkedHashMap<>();
//        for (String data : dataList) {
//            boolean found = false;
//            FileInfo recentFile = searchRecentFile(root, data);
//            if (recentFile != null) {
//                found = true;
//                Double price = priceMap.get(data);
//                String newFileName;
//                if (price != null) {
//                    newFileName = data + "_" + price + "." + getFileExtension(new File(recentFile.filePath).getName());
//                } else {
//                    newFileName = data + "." + getFileExtension(new File(recentFile.filePath).getName());
//                }
//                File targetFile = new File(targetFolder.getAbsolutePath() + File.separator + newFileName);
//                Files.copy(new File(recentFile.filePath).toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                itemToNewFileNameMap.put(data, newFileName);
//            }
//            if (!found) {
//                System.out.println("没有找到包含 " + data + " 的文件");
//            }
//        }
//
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Data");
//
//        Font font = workbook.createFont();
//        font.setFontHeightInPoints((short) 16);
//        font.setBold(true);
//
//        CellStyle style = workbook.createCellStyle();
//        style.setFont(font);
//        style.setAlignment(HorizontalAlignment.CENTER);
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//
//        Font secondRowFont = workbook.createFont();
//        secondRowFont.setFontHeightInPoints((short) 11);
//
//        CellStyle secondRowStyle = workbook.createCellStyle();
//        secondRowStyle.setFont(secondRowFont);
//        secondRowStyle.setAlignment(HorizontalAlignment.CENTER);
//        secondRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//        Row nameRow = sheet.createRow(0);
//        Cell nameCell = nameRow.createCell(0);
//        nameCell.setCellValue("施美毛巾");
//        nameCell.setCellStyle(style);
//
//        Row secondRow = sheet.createRow(1);
//        Cell indexCell = secondRow.createCell(0);
//        indexCell.setCellValue("序号");
//        indexCell.setCellStyle(secondRowStyle);
//        Cell pictureCell = secondRow.createCell(1);
//        pictureCell.setCellValue("图片");
//        pictureCell.setCellStyle(secondRowStyle);
//
//        int rowNum = 2;
//        for (Map.Entry<String, String> entry : itemToNewFileNameMap.entrySet()) {
//            String data = entry.getKey();
//            String newFileName = entry.getValue();
//            String path = targetFolder.getAbsolutePath() + File.separator + newFileName;
//
//            Row row = sheet.createRow(rowNum);
//            row.setHeightInPoints(160);
//            sheet.setColumnWidth(1, 29 * 256);
//
//            for (int col = 2; col < row.getPhysicalNumberOfCells(); col++) {
//                sheet.setColumnWidth(col, sheet.getColumnWidth(1));
//            }
//
//            for (int col = 0; col < row.getPhysicalNumberOfCells(); col++) {
//                Cell cell = row.getCell(col);
//                if (cell == null) {
//                    cell = row.createCell(col);
//                }
//                cell.setCellStyle(secondRowStyle);
//            }
//
//            Cell indexCellInData = row.createCell(0);
//            indexCellInData.setCellValue(rowNum - 1);
//            indexCellInData.setCellStyle(secondRowStyle);
//
//            String[] parts = path.split("\\\\");
//            String lastPart = parts[parts.length - 1];
//            if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".JPG") || path.endsWith(".PNG")) {
//                lastPart = lastPart.substring(0, lastPart.length() - 4);
//            }
//            Cell pictureCellInData = row.createCell(1);
//
//            if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".JPG") || path.endsWith(".PNG")) {
//                try {
//                    File imageFile = new File(path);
//                    int pictureIdx = workbook.addPicture(FileHandler.getBytesFromFile(imageFile), Workbook.PICTURE_TYPE_JPEG);
//                    sheet.createDrawingPatriarch().createPicture(new XSSFClientAnchor(100, 100, 100, 100, 1, rowNum, 1 + 1, rowNum + 1), pictureIdx);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            String[] lastPartParts = lastPart.split("_");
//            int colIndex = 2;
//            for (String part : lastPartParts) {
//                Cell partCell = row.createCell(colIndex);
//                partCell.setCellValue(part);
//                partCell.setCellStyle(secondRowStyle);
//                colIndex++;
//            }
//
//            rowNum++;
//        }
//
//        int maxColumn = 0;
//        for (Row row : sheet) {
//            if (row.getPhysicalNumberOfCells() > maxColumn) {
//                maxColumn = row.getPhysicalNumberOfCells();
//            }
//        }
//        System.out.println("当前表格有 " + maxColumn + " 列。");
//        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, maxColumn - 1));
//
//        for (Row row : sheet) {
//            for (Cell cell : row) {
//                if (cell.getCellType() == CellType.STRING && isNumeric(cell.getStringCellValue())) {
//                    try {
//                        NumberFormat format = NumberFormat.getInstance();
//                        Number number = format.parse(cell.getStringCellValue());
//                        cell.setCellValue(number.doubleValue());
//                    } catch (ParseException ex) {
//                        // 如果不是可转换为数字的文本，忽略
//                    }
//                }
//            }
//        }
//
//        try (FileOutputStream outputStream = new FileOutputStream(new File("wechat_data.xlsx"))) {
//            workbook.write(outputStream);
//        } catch (IOException ex) {
//            ex.printStackTrace();
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
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new MainEntryWithGUIUpdate();
//            }
//        });
//    }
//}