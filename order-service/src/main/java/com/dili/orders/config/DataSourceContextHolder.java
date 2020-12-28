package com.dili.orders.config;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 数据源切换器
 * 
 * @author jiang
 *
 */
@Component
@Lazy(false)
public class DataSourceContextHolder {

	private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

	/**
	 * 设置数据源类型
	 * 
	 * @param dbType
	 */
	public static void setDbType(String dbType) {
		contextHolder.set(dbType);
	}

	/**
	 * 获取数据源类型
	 * 
	 * @return
	 */
	public static String getDbType() {
		return contextHolder.get();
	}

	/**
	 * 重置
	 */
	public static void clearDbType() {
		contextHolder.remove();
	}

}
