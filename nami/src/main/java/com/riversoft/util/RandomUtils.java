/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author Woden
 * 
 */
public class RandomUtils {

    /**
     * 生成随机码图片
     * 
     * @param rand
     * @param heigh
     * @param width
     * @return
     */
    public static BufferedImage createImage(String rand, int heigh, int width) {
        BufferedImage image = new BufferedImage(width, heigh, BufferedImage.TYPE_INT_RGB);
        Random random = new Random();

        Graphics g = image.getGraphics();

        // 设定背景色
        g.setColor(new Color(0xfafafa));
        g.fillRect(0, 0, width, heigh);

        // 画边框
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, heigh - 1);

        g.setColor(Color.RED);
        for (int i = 0; i < rand.length(); i++) {
            g.setFont(new Font("Gungsuh", Font.PLAIN, heigh - 2 - random.nextInt(1)));
            g.drawString(rand.substring(i, i + 1), 2 + width / rand.length() * i, heigh - 2 - random.nextInt(2));
        }

        g.setColor(Color.black);
        for (int i = 0; i < 30; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(heigh);
            g.drawOval(x, y, 0, 0);
        }
        g.dispose();
        return image;
    }

    /**
     * 生成随机码
     * 
     * @param length
     * @return
     */
    public static String createRandomCode(int length) {
        Random random = new Random();
        String result = String.valueOf(Math.abs(random.nextLong()));// 绝对值

        if (result.length() >= length) {
            return result.substring(0, length);
        } else {
            return result + createRandomCode(length - result.length());
        }

    }
}
