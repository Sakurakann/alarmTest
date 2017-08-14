package com.utils.db;

import java.sql.Connection;

import com.utils.AppException;

public class ProxoolJDBCManager extends JdbcManager {
	
	private ProxoolJDBCManager(){
	}
	
	private static ProxoolJDBCManager _intance = new ProxoolJDBCManager();
	
	public static ProxoolJDBCManager getIntance(){
		return _intance;
	}
	
	private ProxoolConnectManager DBCM = null;
	@Override
	public Connection getConnection() throws AppException {
		if (this.DBCM == null) this.DBCM = ProxoolConnectManager.getInstance();

	    return this.DBCM.getConnection(SERVER_DATABASE_POOLNAME);
	}

}
