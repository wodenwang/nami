/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 格式化
 * 
 * @author Woden
 * 
 */
public class Formatter {

    static Logger logger = LoggerFactory.getLogger(Formatter.class);

    /**
     * 格式化日期成字符串（yyyy-MM-dd）
     * 
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (date == null) {
            return "";
        }
        return dateFormat.format(date);
    }

    /**
     * 格式化日期成字符串（yyyy-MM-dd HH:mm:ss）
     * 
     * @param date
     * @return
     */
    public static String formatDatetime(Date date) {
        return formatDatetime(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 格式化日期成自定义格式字符串
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDatetime(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 格式化价格
     * 
     * @param price
     * @param length
     * @return
     */
    public static String formatPrice(BigDecimal price, String pattern) {
        if (price == null) {
            return "";
        }

        if (pattern == null || pattern.equals("")) {
            pattern = "#,##0.00##";
        }

        return new DecimalFormat(pattern).format(price);
    }

    /**
     * 格式化价格
     * 
     * @param price
     * @return
     */
    public static String formatPrice(BigDecimal price) {

        return formatPrice(price, null);
    }

    /**
     * 格式化价格
     * 
     * @param price
     * @param pattern
     * @return
     */
    public static String formatPrice(Double price, String pattern) {
        if (pattern == null || pattern.equals("")) {
            pattern = "#,##0.00##";
        }
        return new DecimalFormat(pattern).format(price);
    }

    /**
     * 格式化价格
     * 
     * @param price
     * @return
     */
    public static String formatPrice(Double price) {
        return formatPrice(price, null);
    }

    /**
     * 格式化百分比
     * 
     * @param num
     * @return
     */
    public static String formatPercent(Double num) {
        if (num == null) {
            return "";
        }
        return formatPrice(num * 100, "0.00") + "%";
    }

    /**
     * 格式化数字
     * 
     * @param num
     * @param pattern
     * @return
     */
    public static String formatNumber(Number num, String pattern) {
        if (num == null) {
            return "";
        }
        return new DecimalFormat(pattern).format(num);
    }

    /**
     * 格式化数字
     * 
     * @param num
     * @return
     */
    public static String formatNumber(Number num) {
        return formatNumber(num, "0.##");
    }

    /**
     * 格式化百分比
     * 
     * @param num
     * @return
     */
    public static String formatPercent(BigDecimal num) {
        if (num == null) {
            return "";
        }
        return formatPrice(num.multiply(BigDecimal.valueOf(100)), "0.00") + "%";
    }

    /**
     * 数字格式化成大写
     * 
     * @param price
     * @return
     */
    public static String formatChinesePrice(BigDecimal price) {

        if (price == null) {
            return "";
        }

        double n = price.doubleValue();
        String fraction[] = { "角", "分" };
        String digit[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
        String unit[][] = { { "元", "万", "亿" }, { "", "拾", "佰", "仟" } };

        String head = n < 0 ? "负" : "";
        n = Math.abs(n);
        String s = "";

        for (int i = 0; i < fraction.length; i++) {
            s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
        }

        if (s.length() < 1) {
            s = "整";
        }

        int integerPart = (int) Math.floor(n);
        for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
            String p = "";
            for (int j = 0; j < unit[1].length && n > 0; j++) {
                p = digit[integerPart % 10] + unit[1][j] + p;
                integerPart = integerPart / 10;
            }

            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }
        return head
                + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零")
                        .replaceAll("^整$", "零元整");

    }

    /**
     * 将秒数格式化成天-小时-分-秒<br>
     * 返回两段式即可,即:X天Y小时或X小时Y分钟
     * 
     * @param s
     * @return
     */
    public static String formatDuring(Long s) {
        long days = s / (60 * 60 * 24);
        long hours = (s % (60 * 60 * 24)) / (60 * 60);
        long minutes = (s % (60 * 60)) / (60);
        long seconds = (s % (60));

        int level = 0;
        StringBuffer buffer = new StringBuffer();
        if (days > 0) {
            buffer.append(days).append("天");
            level++;
        }
        if (buffer.length() > 0 || hours > 0) {
            buffer.append(hours).append("小时");
            level++;
        }
        if (level < 2 && (buffer.length() > 0 || minutes > 0)) {
            buffer.append(minutes).append("分钟");
            level++;
        }
        if (level < 2 && (buffer.length() > 0 || seconds > 0)) {
            buffer.append(seconds).append("秒");
        }

        if (buffer.length() < 1) {
            buffer.append("小于1秒");
        }
        return buffer.toString();
    }
}
