/*
 * 
 */
package com.riversoft.core;

import java.util.Map;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 核心对象工厂.
 * 
 */
public class BeanFactory {

    /**
     * 当前实例.
     */
    private static BeanFactory instance = new BeanFactory();
    /**
     * Spring 配置.
     */
    private ApplicationContext ac;

    /**
     * 获取单例.
     * 
     * @return <code>BeanFactory</code>实例.
     */
    public static BeanFactory getInstance() {
        return instance;
    }

    /**
     * 对象工厂初始化.
     */
    public static void init() {
        init("classpath*:applicationContext-*.xml");
    }

    /**
     * 对象工厂初始化.
     * 
     * @param applicationContextPath 配置路径，支持多个，使用逗号分割
     */
    public static void init(String applicationContextPath) {
        if (instance.ac == null)
            instance.ac = new ClassPathXmlApplicationContext(applicationContextPath.split(",", -1));
    }

    private BeanFactory() {
        // empty.
    }

    /**
     * 获取或创建对象.
     * 
     * @param beanName 对象名
     * @param beanClass 类
     * @param isAutowire
     *            <code>true</code>表示对象自动装载.
     * @param isSingle
     *            <code>true</code>表示使用单例模式
     * @param arguments 构造函数入参数组.
     * @param propertyMap 实例创建之后属性设置.
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T getBean(String beanName, Class<T> beanClass, boolean isAutowire, boolean isSingle,
            Object[] arguments, Map<String, ?> propertyMap) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ((AbstractRefreshableApplicationContext) ac)
                .getBeanFactory();

        if (!beanFactory.containsBean(beanName)) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition();
            beanDefinition.setBeanClass(beanClass);
            beanDefinition.setAutowireCandidate(true);
            if (!isAutowire) {
                beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
            } else {
                beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
            }

            if (!isSingle) {
                beanDefinition.setScope(AbstractBeanDefinition.SCOPE_PROTOTYPE);
            } else {
                beanDefinition.setScope(AbstractBeanDefinition.SCOPE_SINGLETON);
            }

            if (arguments != null && arguments.length > 0) {
                ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                for (int i = 0; i < arguments.length; i++) {
                    constructorArgumentValues.addIndexedArgumentValue(i, arguments[i]);
                    beanDefinition.setConstructorArgumentValues(constructorArgumentValues);
                }
            }

            if (propertyMap != null && !propertyMap.isEmpty()) {
                beanDefinition.setPropertyValues(new MutablePropertyValues(propertyMap));
            }

            beanFactory.registerBeanDefinition(beanName, beanDefinition);
        }

        return (T) beanFactory.getBean(beanName);
    }

    /**
     * 在对象工厂中获取已存在的实例.
     * 
     * @param name 实例配置名.
     * @return 对象,如果对象不存在则返回<code>null</code>.
     */
    public Object getBean(String name) {
        return ac.getBean(name);
    }

    /**
     * 使用单例模式获取实例.
     * 
     * @param beanClass 实例类.
     * @param objects 构造入参.
     * @return 单例对象.
     */
    public <T> T getSingleBean(Class<T> beanClass, Object... objects) {
        return getBean(beanClass.getName() + "-single", beanClass, true, true, objects, null);
    }

    /**
     * 创建实例.
     * 
     * @param beanClass 实例类.
     * @param objects 构造入参.
     * @return 对象.
     */
    public <T> T getBean(Class<T> beanClass, Object... objects) {
        return getBean(beanClass.getName(), beanClass, true, false, objects, null);
    }
}
