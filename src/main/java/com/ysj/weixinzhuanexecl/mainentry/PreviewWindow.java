package com.ysj.weixinzhuanexecl.mainentry;

import com.ysj.weixinzhuanexecl.executor.MainProcessExecutor;
import com.ysj.weixinzhuanexecl.executor.PriceReader;
import com.ysj.weixinzhuanexecl.handler.FileInfo;
import com.ysj.weixinzhuanexecl.util.FileSearchUtil;
import com.ysj.weixinzhuanexecl.util.FileUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PreviewWindow extends JFrame {
    private ArrayList<String> dataList;
    private String imageSearchFolder;
    private String priceDatabaseFile;
    private JTextArea feedbackArea;
    private JTextField newFieldInput;
    private JButton addFieldButton;
    private JTable previewTable;
    private DefaultTableModel tableModel;

    public PreviewWindow(ArrayList<String> dataList, String imageSearchFolder, String priceDatabaseFile, JTextArea feedbackArea) throws IOException {
        this.dataList = dataList;
        this.imageSearchFolder = imageSearchFolder;
        this.priceDatabaseFile = priceDatabaseFile;
        this.feedbackArea = feedbackArea;

        setTitle("Excel 预览");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 输入新字段的组件
        newFieldInput = new JTextField(20);
        addFieldButton = new JButton("添加字段");
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("新字段名称:"));
        inputPanel.add(newFieldInput);
        inputPanel.add(addFieldButton);
        add(inputPanel, BorderLayout.NORTH);

        addFieldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newField = newFieldInput.getText().trim();
                if (!newField.isEmpty()) {
                    tableModel.addColumn(newField);
                    newFieldInput.setText("");
                }
            }
        });

        // 生成预览数据
        Map<String, String> itemToNewFileNameMap = generatePreviewData();
        tableModel = generateTableModel(itemToNewFileNameMap);
        previewTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(previewTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // 保存按钮
        JButton saveButton = new JButton("保存为 Excel");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToExcel();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private Map<String, String> generatePreviewData() throws IOException {
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
                for (Map.Entry<String, Double> entry : priceMap.entrySet()) {
                    if (entry.getKey().contains(data)) {
                        price = entry.getValue();
                        break;
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
        return itemToNewFileNameMap;
    }

    private DefaultTableModel generateTableModel(Map<String, String> itemToNewFileNameMap) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("序号");
        model.addColumn("图片");
        model.addColumn("货号");
        model.addColumn("价格");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String targetFolderName = sdf.format(new Date()) + "施美毛巾";
        File targetFolder = new File(targetFolderName);

        int index = 1;
        for (Map.Entry<String, String> entry : itemToNewFileNameMap.entrySet()) {
            String data = entry.getKey();
            String newFileName = entry.getValue();
            String[] parts = newFileName.split("_");
            String price = parts.length > 1 ? parts[1].replace("." + FileUtils.getFileExtension(newFileName), "") : "未找到";
            String imagePath = targetFolder.getAbsolutePath() + File.separator + newFileName;
            model.addRow(new Object[]{index++, imagePath, data, price});
        }
        return model;
    }

    private void saveToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("施美毛巾");

        // 合并标题行的单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, tableModel.getColumnCount() - 1));

        // 设置标题样式（居中、加粗、字体大小）
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        Font titleFont = workbook.createFont();
        titleFont.setBold(true); // 加粗
        titleFont.setFontHeightInPoints((short) 16); // 字体大小
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        // 创建表头
        Row headerRow = sheet.createRow(1); // 表头从第二行开始
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(tableModel.getColumnName(i));
            cell.setCellStyle(headerStyle); // 设置表头样式
        }

        // 手动设置图片列的单元格大小（4:3 比例）
        int imageColumnWidth = 40 * 256; // 列宽为 40 个字符宽度
        int imageRowHeight = 20 * 10; // 行高为 20 点
        sheet.setColumnWidth(1, imageColumnWidth);

        // 设置数据单元格样式（居中）
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中

        // 填充数据
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Row row = sheet.createRow(i + 2); // 数据从第三行开始
            row.setHeightInPoints(imageRowHeight); // 设置图片列的行高

            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                Cell cell = row.createCell(j);
                Object value = tableModel.getValueAt(i, j);
                if (value != null) {
                    if (j == 1) { // 图片列
                        String imagePath = value.toString();
                        insertImage(sheet, i + 2, j, imagePath, workbook); // 图片从第三行开始
                    } else {
                        cell.setCellValue(value.toString());
                    }
                } else {
                    cell.setCellValue("");
                }
                cell.setCellStyle(dataStyle); // 设置数据单元格样式
            }
        }

        try (FileOutputStream outputStream = new FileOutputStream(new File("preview_data.xlsx"))) {
            workbook.write(outputStream);
            feedbackArea.append("Excel 文件已保存为 preview_data.xlsx\n");
            // 尝试打开生成的 Excel 文件
            File excelFile = new File("preview_data.xlsx");
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
        } catch (IOException e) {
            e.printStackTrace();
            feedbackArea.append("保存 Excel 文件时出错: " + e.getMessage() + "\n");
        }
    }

    private void insertImage(Sheet sheet, int rowIndex, int colIndex, String imagePath, Workbook workbook) {
        try (InputStream inputStream = new FileInputStream(imagePath)) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, colIndex, rowIndex, colIndex + 1, rowIndex + 1);

            // 插入图片
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            pict.resize(1.0); // 调整图片大小
        } catch (IOException e) {
            e.printStackTrace();
            sheet.getRow(rowIndex).getCell(colIndex).setCellValue("图片插入失败: " + e.getMessage());
        }
    }
}