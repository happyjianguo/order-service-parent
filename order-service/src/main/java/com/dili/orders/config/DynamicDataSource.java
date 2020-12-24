package com.dili.orders.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DynamicDataSource extends AbstractRoutingDataSource {

	private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);
	@Autowired
	@Qualifier("selectDataSource")
	private DataSource selectDataSource;
	@Autowired
	@Qualifier("updateDataSource")
	private DataSource updateDataSource;

	/**
	 * 返回生效的数据源名称
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		logger.info("DataSourceContextHolder：{}", DataSourceContextHolder.getDbType());
		return DataSourceContextHolder.getDbType();
	}

	/**
	 * 配置使用的数据源信息，如果不存在就使用默认的数据源
	 */
	@Override
	public void afterPropertiesSet() {
		Map<Object, Object> map = new HashMap<>();
		map.put("selectDataSource", selectDataSource);
		map.put("updateDataSource", updateDataSource);
		// 注册数据源
		setTargetDataSources(map);
		setDefaultTargetDataSource(updateDataSource);
		super.afterPropertiesSet();
	}

}
