package com.utils.db;

import java.sql.Connection;

import com.utils.AppException;
import com.utils.Configure;

public abstract class JdbcManager {
	public static int SERVER_DATABASE_MODE = 0;
	  public static String SERVER_JDBC_POOL_TYPE = "default";
	  public static String SERVER_DATABASE_POOLNAME = "POOL_XXXX_NAME";
	  public static String SERVER_DATABASE_PROCEDURE_PACKAGE = "PKG_XXXX_NAME";

	  public static String SERVER_DATABASE_HSQL_URL = "jdbc:hsqldb:file:./database/hsqldb";

	  public static String PARAMETET_SQLPARAM_PRIMARY_SPLITER = ";";

	  public static String PARAMETET_SQLPARAM_EXTENDS_SPLITER = ",";

	  public static String SERVSER_DATABASE_DRIVER = "ORACLE";
	  public static final String ERROR_SQL = "Cannot find SQL statement";
	  public static final String ERROR_CONNECTION = "Cannot open connection";
	  public static final String ERROR_LOGON = "Error phone or password";
	  
	  static
	  {
	    try
	    {
	      Configure con = new Configure("/db.properties");

	      if (con.getProperty("SERVER_DATABASE_MODE") != null)
	        SERVER_DATABASE_MODE = Integer.parseInt(con.getProperty("SERVER_DATABASE_MODE").trim());
	      if (con.getProperty("SERVER_JDBC_POOL_TYPE") != null)
	        SERVER_JDBC_POOL_TYPE = con.getProperty("SERVER_JDBC_POOL_TYPE").trim();
	      if (con.getProperty("SERVER_DATABASE_POOLNAME") != null)
	        SERVER_DATABASE_POOLNAME = con.getProperty("SERVER_DATABASE_POOLNAME").trim();
	      if (con.getProperty("SERVER_DATABASE_PROCEDURE_PACKAGE") != null) {
	        SERVER_DATABASE_PROCEDURE_PACKAGE = con.getProperty("SERVER_DATABASE_PROCEDURE_PACKAGE").trim().toUpperCase();
	      }
	      if ((SERVER_JDBC_POOL_TYPE.equals("hsql")) && (con.getProperty("jdbc.url") != null))
	        SERVER_DATABASE_HSQL_URL = con.getProperty("jdbc.url").trim();
	    } catch (Exception e) {
	      System.err.println("加载配置文件[/db.properties]出错......");
	    }
	  }
	  
	  
	  public abstract Connection getConnection() throws AppException;
}
