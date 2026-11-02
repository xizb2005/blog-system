package com.example.demo3.util;

public class TextCutUtil {
    // 留给文章内容最大字符，预留提示词空间
    private static final int MAX_INPUT_LENGTH = 1800;

    /**
     * 清洗文本 + 超长截断
     * @param content 原始文章内容
     * @return 处理后的安全文本
     */
    public static String cutText(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        // 把多个换行、空格、制表符替换成单个空格，减少无效字符
        String cleanContent = content.replaceAll("\\s+", " ");

        if (cleanContent.length() <= MAX_INPUT_LENGTH) {
            return cleanContent;
        }
        // 截断，预留20字符放后缀提示
        return cleanContent.substring(0, MAX_INPUT_LENGTH - 20) + "...(内容过长已截断)";
    }
}