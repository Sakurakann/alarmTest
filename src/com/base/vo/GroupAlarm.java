package com.base.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.utils.StringUtils;

public class GroupAlarm implements Serializable{
	private static final long serialVersionUID = -3347685336948038982L;
	private static Logger log = Logger.getLogger(GroupAlarm.class);
	private String xlsNo="";
	
	private String alarmType="1";//1:统计策略;2:限定策略,3综合策略
	
	private int id=-1; //数据库ID
	
	private String value="";
	
	private String groupString="";
	
	private boolean execFlag=true;
	
	//xlsNo,dbId
	private Map<String,Integer> groupIds=null;
	
	
	public GroupAlarm(String xlsNo, String alarmType, String value,
			String groupString) {
		this.xlsNo = xlsNo;
		this.alarmType = alarmType;
		this.value = value;
		setGroupString(groupString);
		parseValue();
	}

	
	

	public String getXlsNo() {
		return xlsNo;
	}


	public void setXlsNo(String xlsNo) {
		this.xlsNo = xlsNo;
	}


	public String getAlarmType() {
		return alarmType;
	}


	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getGroupString() {
		return groupString;
	}
	
	public void addGroups(String id){
		if(groupIds ==null){
			groupIds = new HashMap<String,Integer>();
		}
		groupIds.put(id, -1);
	}
	
	public void addRelGroupId(String xlsNo,int DBId){
		groupIds.put(xlsNo, DBId);
	}
	/**
	 * @param xlsNo 组编号
	 * @return 群中是否存在该编号的组
	 */
	public boolean containGroupId(String xlsNo){
		return groupIds==null?false:groupIds.containsKey(xlsNo);
	}

	//检查告警组设置是否正确
	private void parseValue(){
		if(!"1".equals(alarmType) && !"".equals(StringUtils.trim(value))){
			AlarmGroups groups = AlarmGroups.getIntance();
			String[] gSub = value.split("\\$");
			for(String g : gSub){
				String[] params = g.split("#");
				if(params.length==2){
					if(!groups.containKey(params[0])){
						execFlag=false;
						log.error("告警群["+xlsNo+"]告警参数中定义的告警组ID未定义!");
						break;
					}
				}
			}
		}
	}

	public void setGroupString(String groupString) {
		this.groupString = groupString;
		String[] groupSub = StringUtils.splitLikeJson(groupString, '{', '}');
		for(String group : groupSub){
			if(group.startsWith("group:")){
				String id = group.substring(6);
				if(AlarmGroups.getIntance().containKey(id)){
					addGroups(id);
				}else{
					log.warn("配置的告警组引用["+group+"]不存在");
				}
			}else{
				AlarmGroup ag = AlarmGroups.getIntance().createAlarmGroup(group);
				if(ag !=null){
					addGroups(ag.getXlsNo());
				}
			}
		}
	}
	
	public String getGroupIdsString(){
		String ids="";
		for(Integer id : groupIds.values()){
			ids += id+",";
		}
		return ids.substring(0, ids.length()-1);
	}


	public Set<String> getGroupIds() {
		return groupIds==null?null:groupIds.keySet();
	}


	public boolean isExecFlag() {
		return execFlag;
	}


	public void setExecFlag(boolean execFlag) {
		this.execFlag = execFlag;
	}




	public int getId() {
		return id;
	}




	public void setId(int id) {
		this.id = id;
	}
	
	

}
