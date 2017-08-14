package com.base.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class XlsRecord implements Serializable{
	private static final long serialVersionUID = -4742326337060968732L;

	private static int index = 1;

	private int recordNo = index++;
	
	private String testName = "";

	/**
	 * 告警点不需要记录，告警告警组，群，需要替换参数，所以需要记录下一行数据
	 */
	private List<String> alarmGroups = null; // 组告警

	private List<String> groupAlarms = null; // 群告警
	
	private List<String> alarms=null;

	private List<String> taskdis = null;

	private List<Bill> bills = null;

	private long cycleTime = 365 * 24 * 60 * 60*1000; // 循环周期(秒)

	private long lastExeTime = -1; // 最近执行时间
	
	private boolean execFlag=false;
	
	private boolean free = true;

	public void addBill(Bill bill) {
		if (bills == null) {
			bills = new ArrayList<Bill>();
			execFlag=true;
		}
		bills.add(bill);
	}
	

	public boolean isExecFlag() {
		return execFlag;
	}


	public void setExecFlag(boolean execFlag) {
		this.execFlag = execFlag;
	}


	public void addAlarmGroups(String no) {
		if (alarmGroups == null) {
			alarmGroups = new ArrayList<String>();
		}
		alarmGroups.add(no);
	}

	public void addGroupAlarms(String no) {
		if (groupAlarms == null) {
			groupAlarms = new ArrayList<String>();
		}
		groupAlarms.add(no);
	}

	public void addAlarm(String no) {
		if (alarms == null) {
			alarms = new ArrayList<String>();
		}
		alarms.add(no);
	}
	
	public void addTask(String no) {
		if (taskdis == null) {
			taskdis = new ArrayList<String>();
		}
		taskdis.add(no);
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public List<String> getAlarmGroups() {
		return alarmGroups;
	}

	public void setAlarmGroups(List<String> alarmGroups) {
		this.alarmGroups = alarmGroups;
	}

	public List<String> getGroupAlarms() {
		return groupAlarms;
	}

	public void setGroupAlarms(List<String> groupAlarms) {
		this.groupAlarms = groupAlarms;
	}

	public List<String> getTaskdis() {
		return taskdis;
	}

	public void setTaskdis(List<String> taskdis) {
		this.taskdis = taskdis;
	}

	public long getCycleTime() {
		return cycleTime;
	}

	public void setCycleTime(long cycleTime) {
		this.cycleTime = cycleTime;
	}

	public long getLastExeTime() {
		return lastExeTime;
	}

	public void setLastExeTime(long lastExeTime) {
		this.lastExeTime = lastExeTime;
	}

	public int getRecordLineNo(){
		return recordNo;
	}


	public boolean isFree() {
		return free;
	}


	public void setFree(boolean free) {
		this.free = free;
	}


	public List<Bill> getBills() {
		return bills;
	}
	
	public void printRecordInfo(){
		Bills.printBillInfo(null);
	}

}
