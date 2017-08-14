package com.base.vo;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 * 
 * ALARMPARAMTYPE,CLEARPARAMTYPE: 1:连续次数;2:窗口时间百分比;3:窗口时间次数
 * 
 */
public class AlarmPoint implements Serializable{

	private static final long serialVersionUID = -4464885416353903535L;
	
	private String xlsNo = "1";// execl 告警编号
	private int alarmPointNO = -1; // Table(z_alarmPoint) alarmnumber
	private int alarmNoForTest = -1; // Table(z_alarmpolicy) ID
	
	// 告警参数  1:连续次数;2:窗口时间百分比;3:窗口时间次数
	private int ALARMPARAMTYPE = 1;
	private String alarmValue = "";
	// 清楚告警参数
	private int CLEARPARAMTYPE = 1;
	private String clearValue = "";
	// 告警数据
	private String value = "";
	
	private int testCode = -1;

	public AlarmPoint(String xlsNo, String alarmparamtype, String alarmValue,
			String clearparamtype, String clearValue, String value,
			String testCode) {
		this.xlsNo = xlsNo;
		ALARMPARAMTYPE = Integer.valueOf(alarmparamtype);
		this.alarmValue = alarmValue;
		CLEARPARAMTYPE = Integer.valueOf(clearparamtype);
		this.clearValue = clearValue;
		this.value = value.replaceAll("\\|", ",");
		this.testCode = Integer.valueOf(testCode);
	}

	public String getXlsNo() {
		return xlsNo;
	}

	public void setXlsNo(String xlsNo) {
		this.xlsNo = xlsNo;
	}

	public int getAlarmPointNO() {
		return alarmPointNO;
	}

	public void setAlarmPointNO(int alarmPointNO) {
		this.alarmPointNO = alarmPointNO;
	}

	public int getAlarmNoForTest() {
		return alarmNoForTest;
	}

	public void setAlarmNoForTest(int alarmNoForTest) {
		this.alarmNoForTest = alarmNoForTest;
	}


	public String getAlarmValue() {
		return alarmValue;
	}

	public void setAlarmValue(String alarmValue) {
		this.alarmValue = alarmValue;
	}


	public int getALARMPARAMTYPE() {
		return ALARMPARAMTYPE;
	}

	public void setALARMPARAMTYPE(int alarmparamtype) {
		ALARMPARAMTYPE = alarmparamtype;
	}

	public int getCLEARPARAMTYPE() {
		return CLEARPARAMTYPE;
	}

	public void setCLEARPARAMTYPE(int clearparamtype) {
		CLEARPARAMTYPE = clearparamtype;
	}

	public int getTestCode() {
		return testCode;
	}

	public void setTestCode(int testCode) {
		this.testCode = testCode;
	}

	public String getClearValue() {
		return clearValue;
	}

	public void setClearValue(String clearValue) {
		this.clearValue = clearValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
