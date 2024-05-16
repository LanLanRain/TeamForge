package com.rainsoul.teamforge.importuser;

import com.alibaba.excel.EasyExcel;

import java.util.List;
/**
 * ImportExcel 类用于演示如何从 Excel 文件中导入数据。
 */
public class ImportExcel {

    /**
     * 主函数，用于演示同步读取 Excel 文件的内容。
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        String fileName = "D:\\Code\\JavaProject\\TeamForge\\teamforge\\src\\main\\resources\\testExcel.xlsx";
        synchronousRead(fileName);
    }

    /**
     * 通过监听器方式读取 Excel 文件。
     * @param fileName 需要读取的 Excel 文件的路径
     */
    public static void readByListener(String fileName) {
        // 使用监听器模式读取 Excel
        EasyExcel.read(fileName, UserInfoTable.class, new TableListener()).sheet().doRead();
    }

    /**
     * 同步方式读取 Excel 文件。
     * @param fileName 需要读取的 Excel 文件的路径
     * @return 读取到的 UserInfoTable 对象列表
     */
    public static void synchronousRead(String fileName) {
        // 同步读取 Excel 文件的所有内容
        List<UserInfoTable> list = EasyExcel.read(fileName).head(UserInfoTable.class).sheet().doReadSync();
        // 遍历并打印读取到的数据
        for (UserInfoTable userInfoTable : list) {
            System.out.println("userInfoTable = " + userInfoTable);
        }
    }
}
