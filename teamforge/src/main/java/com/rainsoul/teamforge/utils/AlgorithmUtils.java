package com.rainsoul.teamforge.utils;

import java.util.List;
import java.util.Objects;

/**
 * 算法工具类，提供编辑距离算法的实现。
 * 原理: https://blog.csdn.net/DBC_121/article/details/104198838
 */
public class AlgorithmUtils {

    /**
     * 使用编辑距离算法计算两个标签列表之间的相似度。
     * 编辑距离是一个字符串相似度的度量，表示从一个字符串转换成另一个字符串所需的最少操作次数，
     * 其中允许的操作包括删除一个字符、插入一个字符、替换一个字符。
     *
     * @param tagList1 第一个标签列表。
     * @param tagList2 第二个标签列表。
     * @return 两个标签列表之间的编辑距离。
     */
    public static int minDistance(List<String> tagList1, List<String> tagList2) {
        int n = tagList1.size();
        int m = tagList2.size();

        // 如果其中一个列表为空，直接返回两个列表长度之和
        if (n * m == 0) {
            return n + m;
        }

        // 初始化编辑距离矩阵
        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        // 动态规划计算编辑距离
        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1; // 删除操作
                int down = d[i][j - 1] + 1; // 插入操作
                int left_down = d[i - 1][j - 1];
                if (!Objects.equals(tagList1.get(i - 1), tagList2.get(j - 1))) {
                    left_down += 1; // 替换操作
                }
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }

    /**
     * 使用编辑距离算法计算两个字符串之间的相似度。
     * 编辑距离是一个字符串相似度的度量，表示从一个字符串转换成另一个字符串所需的最少操作次数，
     * 其中允许的操作包括删除一个字符、插入一个字符、替换一个字符。
     *
     * @param word1 第一个字符串。
     * @param word2 第二个字符串。
     * @return 两个字符串之间的编辑距离。
     */
    public static int minDistance(String word1, String word2) {
        int n = word1.length();
        int m = word2.length();

        if (n * m == 0) {
            return n + m;
        }

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int left_down = d[i - 1][j - 1];
                if (word1.charAt(i - 1) != word2.charAt(j - 1)) {
                    left_down += 1;
                }
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }
}
