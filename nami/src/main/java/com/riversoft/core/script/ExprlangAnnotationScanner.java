/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.riversoft.core.script.annotation.ScriptSupport;

/**
 * @author Borball
 * 
 */
public class ExprlangAnnotationScanner {

    /**
     * @param scanPackage the scanPackage to set
     */
    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    private Logger logger = LoggerFactory.getLogger(ExprlangAnnotationScanner.class);

    private String scanPackage = "com.riversoft";

    private Map<String, Object> context = new HashMap<>();

    public void init() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ScriptSupport.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(scanPackage)) {
            String clazzName = bd.getBeanClassName();
            Class<?> clazz;
            try {
                clazz = Class.forName(clazzName);
                ScriptSupport elSupport = clazz.getAnnotation(ScriptSupport.class);

                context.put(elSupport.value(), clazz.newInstance());
            } catch (Exception e) {
                logger.warn("ExprlangAnnotationScanner scan failed:" + e.getMessage());
            }
        }

        for (String key : context.keySet()) {
            logger.info("Exprlang Util: " + key + "->" + context.get(key).getClass().getName());
        }
    }

    public Map<String, Object> getElSupports() {
        return context;
    }

}
