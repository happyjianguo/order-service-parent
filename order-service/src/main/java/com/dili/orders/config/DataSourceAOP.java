package com.dili.orders.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 数据源切换配置
 * @author jiang
 *
 */
@Aspect
@Component
@Lazy(false)
// Order设定AOP执行顺序 使之在数据库事务上先执行
@Order(0)
public class DataSourceAOP {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceAOP.class);

	// 横切点
	@Before("execution(* com.dili.orders.service.*.*(..))")
	public void process(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();

		SelectDB db = signature.getMethod().getAnnotation(SelectDB.class);

		if (db != null && db.value().equals(DBType.READ.getValue())) {
			DataSourceContextHolder.setDbType("selectDataSource");
			logger.info("使用的是读数据源：selectDataSource");
		} else {
			DataSourceContextHolder.setDbType("updateDataSource");
			logger.info("使用的是写数据源：updateDataSource");
		}

//		if (methodName.startsWith("get") || methodName.startsWith("count") || methodName.startsWith("find") || methodName.startsWith("list") || methodName.startsWith("select")
//				|| methodName.startsWith("check")) {
//			DataSourceContextHolder.setDbType("selectDataSource");
//			logger.info("使用的是读数据源：selectDataSource");
//		} else {
//			DataSourceContextHolder.setDbType("updateDataSource");
//			logger.info("使用的是写数据源：updateDataSource");
//		}
	}
}
