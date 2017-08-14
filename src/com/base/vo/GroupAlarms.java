package com.base.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.utils.StringUtils;

public class GroupAlarms extends ITableVo<GroupAlarm> {

	private static Logger log = Logger.getLogger(GroupAlarms.class);
	private static GroupAlarms _intance= new GroupAlarms();
	
	private GroupAlarms(){}
	
	public static GroupAlarms getIntance(){
		return _intance;
	}
	
	//群<xlsNo, AlarmGroup>
	private Map<String,GroupAlarm> groupMap =new HashMap<String,GroupAlarm>(20);
	
	
	@Override
	protected void addVo(GroupAlarm t) {
		// TODO Auto-generated method stub
		addGroupAlarm(t);
	}
	
	

	@Override
	protected Collection<GroupAlarm> getSaveObject() {
		List<GroupAlarm> list = new ArrayList<GroupAlarm>();
		for(GroupAlarm group : groupMap.values()){
			if(group.isExecFlag()){
				list.add(group);
			}
		}
		return list;
	}

	public void addGroupAlarm(GroupAlarm group){
		if(groupMap.containsKey(group.getXlsNo())){
			log.warn("告警群编号["+group.getXlsNo()+"]被重复定义，系统覆盖原始版本");
		}
		groupMap.put(group.getXlsNo(), group);
	}
	
	public boolean containKey(String key){
		return groupMap.containsKey(key);
	}
	
	public GroupAlarm getGroupAlarmByID(String xlsNo){
		return groupMap.get(xlsNo);
	}
	
	public Collection<GroupAlarm> getAllGroups(){
		return groupMap==null?null:groupMap.values();
	}
	
	public String getGroupAlarmIdByAlarmGroupXlsNo(String xlsNo){
		for(GroupAlarm g : groupMap.values()){
			if(g.containGroupId(xlsNo)){
				return g.getXlsNo()+"_";
			}
		}
		return "";
	}
	
	public void checkGroupAlarm(){
		for(GroupAlarm group : groupMap.values()){
			if(!group.isExecFlag()) continue;
			Set<String> groupIds = group.getGroupIds();
			if(groupIds !=null && groupIds.size()>0){
				AlarmGroups aGs = AlarmGroups.getIntance();
				for(String groupId : groupIds){
					if(aGs.containKey(groupId) && aGs.getGroup(groupId).isExecFlag()){
						group.addRelGroupId(groupId, aGs.getGroup(groupId).getGroupId());
						String value = group.getValue();
						value = value.replaceAll(groupId, aGs.getGroup(groupId).getGroupId()+"");
						group.setValue(value);
					}else{
						group.setExecFlag(false);
						log.error("群["+group.getXlsNo()+"]，中定义的组编号["+groupId+"]不可用或未定义");
						break;
					}
				}
			}else{
				log.error("群["+group.getXlsNo()+"]，中未定义告警组");
				group.setExecFlag(false);
			}
		}
	}
	
	public GroupAlarm createGroupAlarm(String groupString){
		GroupAlarm group =null;
		String[] agSub = StringUtils.splitLikeJson(groupString, '[', ']');
		if(agSub.length==0 || agSub.length>1 || "".equals(StringUtils.trim(agSub[0]))){
			log.error("群告警设置中，添加告警组格式添加错误.["+groupString+"]");
			return null;
		}
		int index = groupString.indexOf(",[");
		if(index>0){
			String[] params = groupString.substring(0, index).split(",");
			if(params.length==3){
				int i=0;
				group = new GroupAlarm(params[i++],params[i++],params[i++],agSub[0]);
				addGroupAlarm(group);
			}else{
				log.warn("群告警设置中，群定义不正确。");
			}
		}else{
			log.warn("群告警设置中，未能定位告警组的起始位置。--------"+groupString);
		}
		return group;
	}
	@Override
	public String[] clearTable() {
		return new String[]{"truncate table z_group_alaram"};
	}

}
