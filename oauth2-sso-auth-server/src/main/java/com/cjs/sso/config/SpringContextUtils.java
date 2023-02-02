package com.cjs.sso.config;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 
 * @ClassName: SpringContextUtils 
 * @Description: Spring上下文获取工具
 * @author tugang
 * @date 2021年1月22日 下午4:52:42
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;
	
	private static DefaultListableBeanFactory defaultListableBeanFactory;
	

	/**
	 * 实现ApplicationContextAware接口的回调方法，设置上下文环境
	 * 
	 * @param applicationContext
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		if(SpringContextUtils.applicationContext == null){
			SpringContextUtils.applicationContext = applicationContext;
			defaultListableBeanFactory = (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
		}
	}

	/**
	 * @return ApplicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	
	/**
	 * 
	 * @param name Bean名称
	 * @return 返回Bean对象
	 * @author shizhipeng
	 * 
	 */
	public static Object getBean(String name){
		return applicationContext.getBean(name);
	}
	
	

	/**
	 * 获取Bean对象
	 * @param beanName Bean名称
	 * @param obj 转换对象
	 * @return 返回Bean对象
	 * @author shizhipeng
	 * 
	 */
	public static <T> T getBean(String beanName,Class<T> obj){
		return applicationContext.getBean(beanName,obj);
	}
	
	/**
	 * 获取Bean对象
	 * @param obj Bean类型
	 * @return 返回Bean对象
	 * @author shizhipeng
	 * 
	 */
	public static <T> T getBean(Class<T> obj){
        boolean empty = ObjectUtils.isEmpty(applicationContext);
        System.out.println(empty);
        return applicationContext.getBean(obj);
	}
	
	
	/**
	 * 根据BeanName删除Bean
	 * @author shizhipeng
	 * 
	 */
	public static void removeBean(String beanName){
		defaultListableBeanFactory.removeBeanDefinition(beanName);
	}
	
}
