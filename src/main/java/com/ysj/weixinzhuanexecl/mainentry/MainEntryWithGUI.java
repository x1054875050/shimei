package com.ysj.weixinzhuanexecl.mainentry;

import com.ysj.weixinzhuanexecl.executor.MainProcessExecutor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainEntryWithGUI extends JFrame {
    private JTextField inputField;
    private JButton confirmButton;
    private JTextField imageSearchFolderField;
    private JTextField priceDatabaseFileField;
    private JButton imageSearchFolderSelectButton;
    private JButton priceDatabaseFileSelectButton;
    private JTextArea feedbackArea;

    public MainEntryWithGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("FileChooser.useSystemExtensionHiding", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("货号输入界面");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 图片搜索文件夹相关组件
        JLabel imageSearchFolderLabel = new JLabel("图片搜索文件夹:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(imageSearchFolderLabel, gbc);

        imageSearchFolderField = new JTextField("C:\\Users\\sideyu\\Desktop\\tmp");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        add(imageSearchFolderField, gbc);

        imageSearchFolderSelectButton = new JButton("选择文件夹");
        imageSearchFolderSelectButton.setPreferredSize(new Dimension(100, 25));
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        add(imageSearchFolderSelectButton, gbc);

        imageSearchFolderSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(MainEntryWithGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFolder = fileChooser.getSelectedFile();
                    imageSearchFolderField.setText(selectedFolder.getAbsolutePath());
                }
            }
        });

        // 价格数据库文件相关组件
        JLabel priceDatabaseFileLabel = new JLabel("价格数据库文件:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(priceDatabaseFileLabel, gbc);

        // 获取最近日期的文件路径
        String recentFilePath = getRecentPriceDatabaseFile("C:\\Users\\sideyu\\Desktop\\数据表");
        priceDatabaseFileField = new JTextField(recentFilePath);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.8;
        add(priceDatabaseFileField, gbc);

        priceDatabaseFileSelectButton = new JButton("选择文件");
        priceDatabaseFileSelectButton.setPreferredSize(new Dimension(100, 25));
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        add(priceDatabaseFileSelectButton, gbc);

        priceDatabaseFileSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(MainEntryWithGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    priceDatabaseFileField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        // 货号输入相关组件
        JLabel label = new JLabel("请输入要检索的货号，用逗号分隔：");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(label, gbc);

        inputField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(inputField, gbc);

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    confirmButton.doClick();
                }
            }
        });

        // 确认按钮
        confirmButton = new JButton("确认");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(confirmButton, gbc);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText();
                input = input.replace('，', ',');
                if (!input.isEmpty()) {
                    String[] dataArray = input.split(",");
                    ArrayList<String> dataList = new ArrayList<>(Arrays.asList(dataArray));
                    String imageSearchFolder = imageSearchFolderField.getText();
                    String priceDatabaseFile = priceDatabaseFileField.getText();
                    try {
                        feedbackArea.setText("开始处理...\n");
                        new PreviewWindow(dataList, imageSearchFolder, priceDatabaseFile, feedbackArea).setVisible(true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // 反馈区域
        feedbackArea = new JTextArea(10, 50);
        feedbackArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        setVisible(true);
    }

    /**
     * 获取最近日期的价格数据库文件路径
     * @param directoryPath 价格数据库文件所在目录
     * @return 最近日期的文件路径
     */
    private String getRecentPriceDatabaseFile(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return "";
        }

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            return "";
        }

        LocalDate recentDate = LocalDate.MIN;
        File recentFile = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
        Pattern pattern = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2}日)汇总价格.xlsx");

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith("汇总价格.xlsx")) {
                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.find()) {
                    String dateStr = matcher.group(1);
                    try {
                        LocalDate date = LocalDate.parse(dateStr, formatter);
                        if (date.isAfter(recentDate)) {
                            recentDate = date;
                            recentFile = file;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return recentFile != null ? recentFile.getAbsolutePath() : "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainEntryWithGUI();
            }
        });
    }
}