package com.base.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.utils.AppException;
import com.utils.ResultSetUtils;
import com.utils.db.ProxoolJDBCManager;

public abstract class AbstractDaoBean {

	private static ProxoolJDBCManager connectManager = ProxoolJDBCManager
			.getIntance();

	/**
	 * 最大批处理数量 theLargestNumberOfDeals
	 */
	protected static final int batchCount = 700;

	public Connection getConnect() throws AppException {
		return connectManager.getConnection();
	}

	public List<String[]> query(String sql) throws AppException {
		Connection con = null;
		List<String[]> ret = null;
		try {
			con = getConnect();
			PreparedStatement sp = con.prepareStatement(sql);
			ResultSet rs = sp.executeQuery();
			ret = ResultSetUtils.toResultList(rs);
			rs.close();
			sp.close();
			con.close();
		} catch (Exception e) {
			throw new AppException("查询出错["+sql+"]...");
		}
		return ret;
	}
	
	public void execSql(String sql) throws AppException {
		Connection con = null;
		List<String[]> ret = null;
		try {
			con = getConnect();
			PreparedStatement sp = con.prepareStatement(sql);
			sp.execute();
			sp.close();
			con.close();
		} catch (Exception e) {
			throw new AppException("执行语句["+sql+"]出错了...");
		}
	}

	/**
	 * 测试数据库连接是否正常
	 */
	public static boolean isDbInit() {
		boolean reb = false;
		try {
			Connection con = connectManager.getConnection();
			con.close();
			reb = true;
		} catch (Exception e) {
			reb = false;
		}
		return reb;
	}
}
