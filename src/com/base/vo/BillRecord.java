package com.base.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.utils.StringUtils;

public class BillRecord implements Serializable{


	private static final long serialVersionUID = 7759206323597661039L;
	
	private static Logger log = Logger.getLogger(BillRecord.class);
	private String xlsNo="";
	
	private int taskid=-1;
	
	private String sendFlags="";
	
	private String extFields="";
	
	private String alarmType="";
	
	private Map<String, String> billExt = new HashMap<String, String>();

	public BillRecord(String xlsNo, String sendFlags, String extFields) {
		this.xlsNo = xlsNo;
		this.sendFlags = sendFlags;
		this.extFields = extFields;
	}
	
	public void addExt(String field, String value) {
		billExt.put(field, value);
	}

	public Map<String, String> getBillExt() {
		return billExt;
	}

	public String getXlsNo() {
		return xlsNo;
	}

	public void setXlsNo(String xlsNo) {
		this.xlsNo = xlsNo;
	}


	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}

	public String getSendFlags() {
		return sendFlags;
	}

	public void setSendFlags(String sendFlags) {
		this.sendFlags = sendFlags;
	}

	public String getExtFields() {
		return extFields;
	}

	public void setExtFields(String extFields) {
		this.extFields = extFields;
	}
	
	public boolean parseExt(){
		boolean ret = true;
		
		if (extFields != null && !"".equals(extFields)) {
			String[] exts = extFields.split(",");
			for (String ext : exts) {
				int index = ext.indexOf("(");
				if (index == -1 || index == ext.length() - 1) {
					log.error("任务编号["+this.xlsNo+"]的话单特殊字段设置不正确，话单将不进行测试发送");
					return false;
				} else {
					String filed = ext.substring(0, index).toLowerCase();
					String[] value = StringUtils.splitLikeJson(ext, '(', ')');
					if (value.length != 1) {
						log.error("任务编号["+this.xlsNo+"]的话单特殊字段设置不正确，话单将不进行测试发送");
						return false;
					}
					addExt(filed, value[0]);
				}
			}
		}
		return ret;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	
	
}
