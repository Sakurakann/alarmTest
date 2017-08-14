package com.base.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.utils.StringUtils;
/**
 * 告警组集合
 * @author Administrator
 *
 */
public class AlarmGroups extends ITableVo<AlarmGroup> {
	private static Logger log = Logger.getLogger(AlarmGroups.class);
	private static AlarmGroups _intance= new AlarmGroups();
	
	private AlarmGroups(){}
	
	public static AlarmGroups getIntance(){
		return _intance;
	}
	//组<xlsNo, AlarmGroup>
	private Map<String,AlarmGroup> aGroupMap =new HashMap<String,AlarmGroup>(20);
	
	
	
	@Override
	protected void addVo(AlarmGroup t) {
		// TODO Auto-generated method stub
		addAlarmGroup(t);
	}

	public void addAlarmGroup(AlarmGroup group){
		if(aGroupMap.containsKey(group.getXlsNo())){
			log.warn("告警组编号["+group.getXlsNo()+"]被重复定义，系统覆盖原始版本");
		}
		aGroupMap.put(group.getXlsNo(), group);
	}
	
	
	
	@Override
	protected Collection<AlarmGroup> getSaveObject() {
		List<AlarmGroup> list = new ArrayList<AlarmGroup>();
		for(AlarmGroup group : aGroupMap.values()){
			if(group.isExecFlag()){
				list.add(group);
			}
		}
		return list;
	}

	public boolean containKey(String key){
		return aGroupMap.containsKey(key);
	} 
	
	public AlarmGroup getGroup(String key){
		return aGroupMap.get(key);
	}
	
	public Collection<AlarmGroup> getAllGroup(){
		return aGroupMap.values();
	}
	
	public AlarmGroup createAlarmGroup(String groupString){
		AlarmGroup group = null;
		String[] pointString = StringUtils.splitLikeJson(groupString, '[', ']');
		if(pointString.length==0 || pointString.length>1 || "".equals(StringUtils.trim(pointString[0]))){
			log.info("告警组设置中，未添加告警点.");
		}
		int index = groupString.indexOf(",[");
		if(index>0){
			groupString = groupString.substring(0, index);
		}
		String[] params = groupString.substring(0, index).split(",");
		if(params.length==4){
			int i=0;
			group = new AlarmGroup(params[i++],params[i++],params[i++],params[i++],pointString.length>0?pointString[0]:"");
			addAlarmGroup(group);
		}
		return group;
	}
	
	public void checkGroup(){
		TaskInfos tasks = TaskInfos.getIntacne();
		GroupAlarms gAs = GroupAlarms.getIntance();
		Set<String> tds = tasks.getAllTaskIds();
		for(AlarmGroup group : aGroupMap.values()){
			String groupAlarmId = gAs.getGroupAlarmIdByAlarmGroupXlsNo(group.getXlsNo()); //群xlsNo
			if(!"1".equals(group.getAlarmType())){
				List<String> taskids =group.getTasks();
				for(String taskId : taskids){
					if(tasks.containTaskId(taskId) && tasks.getTaskByXlsNo(taskId).isExecTask()){
						String value =group.getValue();
						value = value.replaceAll(taskId.substring(taskId.indexOf(group.getXlsNo())+group.getXlsNo().length()+1), tasks.getTaskByXlsNo(taskId).getTaskid()+"");
						group.setRelValue(value);
					}else{
						if(!"".equals(groupAlarmId)){
							String gTaskid =groupAlarmId + taskId;
							if(tasks.containTaskId(gTaskid) && tasks.getTaskByXlsNo(gTaskid).isExecTask()){
								String value =group.getValue();
								value = value.replaceAll(taskId.substring(taskId.indexOf(group.getXlsNo())+group.getXlsNo().length()+1), tasks.getTaskByXlsNo(gTaskid).getTaskid()+"");
								group.setRelValue(value);
							}
						}else{
							group.setExecFlag(false);
							log.error("告警组["+group.getXlsNo()+"]中定义的任务编号没有在任务定义中找到!");
							break;
						}
					}
				}
			}
			//任务有可能定义在群上
			for(String key : tds){
				if((key.startsWith(group.getXlsNo()) || key.startsWith(groupAlarmId+group.getXlsNo())) && tasks.getTaskByXlsNo(key).isExecTask()){
					group.addRelTask(key);
				}
			}
			if(!checkGroupValue(group)){
				log.warn("告警组["+group.getXlsNo()+"]中定义的任务编号没有!");
				group.setExecFlag(false);
			}
		}
		
	}
	/**
	 * 检查组告警参数定义是否正确
	 * 函数为写完整，只是初步实现
	 * @param group
	 * @return
	 */
	private boolean checkGroupValue(AlarmGroup group){
		boolean ret = group.getRelTasks() !=null && group.getRelTasks().size()>0;
		return ret;
	}
	
	@Override
	public String[] clearTable() {
		return new String[]{"truncate table z_alarm_group","truncate table Z_Task_Alarm_Group"};
	}

}
