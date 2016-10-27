/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.core;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * @author woden
 * 
 */
public class IDGenerator {
    private static TimeBasedGenerator timeBasedGenerator;
    static {
        synchronized (IDGenerator.class) {
            if (timeBasedGenerator == null) {
                timeBasedGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
            }
        }
    }

    private final static char[] TRANSLATE = new char[64];
    static {
        int ix = 0;
        for (char i = '0'; i <= '9'; i++) {
            TRANSLATE[ix++] = i;
        }

        for (char i = 'a'; i <= 'z'; i++) {
            TRANSLATE[ix++] = i;
        }

        for (char i = 'A'; i <= 'Z'; i++) {
            TRANSLATE[ix++] = i;
        }
        TRANSLATE[ix++] = '-';
        TRANSLATE[ix++] = '_';
    }

    /**
     * 获取UUID(唯一值,安全)
     * 
     * @return
     */
    public static String uuid() {
        return timeBasedGenerator.generate().toString();
    }

    /**
     * 获取流水号(以时间为种子,集群部署可能重复.不允许用于业务中,只允许用于配置类参数)
     * 
     * @return
     */
    public static String next() {
        long val = timeBasedGenerator.generate().timestamp();
        val += -5725694900785995309l;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 64; i += 6) {
            builder.append(TRANSLATE[(int) (val >> i) & 63]);
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println(timeBasedGenerator.generate().timestamp());
        System.out.println(timeBasedGenerator.generate().timestamp());
        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis());
        System.out.println(timeBasedGenerator.generate().timestamp());
        System.out.println(timeBasedGenerator.generate().timestamp());
    }

}
