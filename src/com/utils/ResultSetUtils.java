package com.utils;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * 从记录集中获取数据
 */
public class ResultSetUtils {

	/**
	 * 根据给定的ResultSet对象产生一个二维字符串数组.数组的总行数等于ResultSet的数据行数,
	 * 总列数等于ResultSet的总列数.从当前位置开始取数
	 * 
	 * @param rs
	 *            一个ResultSet对象
	 * @return 一个二维字符串，如果rs为null，则返回null
	 * @throws SQLException
	 */
	public static List<String[]> toResultList(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		if (columnCount < 1) {
			return null;
		}

		List<String[]> rows = new ArrayList<String[]>();
		String[] cols = null;
		while (rs.next()) {
			cols = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				cols[i - 1] = getStringValue(rs, i, rsmd.getColumnType(i));
			}
			rows.add(cols);
		}
		return rows;
	}

	

	private static int getColumnIndex(String header, String[] rsmeta) {
		int index = 0;
		boolean found = false;
		for (String m : rsmeta) {
			index++;
			if (header.equalsIgnoreCase(m)) {
				found = true;
				break;
			}
		}
		return found ? index:-1;
	}

	

	/**
	 * 获取rs中的列名称
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private static String[] getMeta(ResultSet rs) throws SQLException {
		if (rs == null) {
			return null;
		}

		ResultSetMetaData rsMeta = rs.getMetaData();
		int len = rsMeta.getColumnCount();

		String[] meta = new String[len];
		for (int i = 0; i < len; i++) {
			meta[i] = rsMeta.getColumnName(i + 1);
		}

		return meta;
	}

	public static String getStringValue(ResultSet rs, int i, int type)
			throws SQLException {
//		String s = rs.getString(i);
//		if (s == null) {
//			s = "";
//		}
		String s="";
		switch (type) {
		case java.sql.Types.FLOAT:
		case java.sql.Types.DOUBLE:
		case java.sql.Types.NUMERIC:
		case java.sql.Types.DECIMAL: {
			BigDecimal bd = rs.getBigDecimal(i);
			int n = MathUtils.intValue(bd);
			if (MathUtils.equals(bd, BigDecimal.valueOf(n))) {
				// 对无小数的数字，只返回整数部分
				s = String.valueOf(n);
			} else {
				s = MathUtils.toString(bd);
			}
			break;
		}
		case java.sql.Types.TIMESTAMP:
		case java.sql.Types.DATE: {
			Timestamp t = rs.getTimestamp(i);
			s = DateUtils.toDatetimeString(t);
			if (s != null &&( s.startsWith("1970-01-01") || s.startsWith("1900-01-01"))) {
				s = "";
				break;
			}
			if (s != null && s.endsWith("00:00:00")) {
				s = DateUtils.getDatePart(s);
			}
			break;
		}
		case java.sql.Types.CLOB:
		{
			Clob clob = rs.getClob(i);
			s = clob.getSubString(1, (int)clob.length());
			break;
		}
		default:{
			s = rs.getString(i);
		}
		}
		if (s == null) {
			s = "";
		}
		return s;
	}
}
