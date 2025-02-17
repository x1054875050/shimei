package com.ysj.weixinzhuanexecl.weixin;

import javax.swing.*;

public class Test {
    public static void main(String[] args) {
        String input = JOptionPane.showInputDialog(null, "请输入内容：");
        System.out.println("用户输入: " + input);

        JOptionPane.showMessageDialog(null, "这是一个提示消息！");
    }
}
