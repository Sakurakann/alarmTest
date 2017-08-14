package com.utils.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;

import com.utils.AppException;

public class ProxoolConnectManager {
	private static Logger log = Logger.getLogger(ProxoolConnectManager.class);
	private static final String PREFIX = "proxool";
	private static final String DOT = ".";
	private static final String PROPERTY_PREFIX = "proxool.";
	private static ProxoolConnectManager instance = null;

	public static synchronized ProxoolConnectManager getInstance()
			throws AppException {
		if (instance == null) {
			instance = new ProxoolConnectManager();
		}
		return instance;
	}

	private ProxoolConnectManager() throws AppException {
		init();
	}

	private void init() throws AppException {
		InputStream is = getClass().getResourceAsStream("/db.properties");
		Properties properties = new Properties();
		try {
			properties.load(is);
		} catch (Exception e) {
			throw new AppException("加载配置文件");
		}
		try {
			PropertyConfigurator.configure(properties);
		} catch (ProxoolException e) {
			throw new AppException(e);
		}
	}

	public Connection getConnection(String name) throws AppException {
		try {
			return DriverManager.getConnection("proxool." + name);
		} catch (SQLException e) {
			log.error("获取数据库连接出错....",e);
			throw new AppException("获取数据库连接出错....");
		}
		
	}

}
