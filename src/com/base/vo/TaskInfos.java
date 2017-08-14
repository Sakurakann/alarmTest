package com.base.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.utils.StringUtils;

public class TaskInfos extends ITableVo<TaskInfo> {
	private static Logger log = Logger.getLogger(TaskInfos.class);
	private static TaskInfos _intance= new TaskInfos();
	
	private TaskInfos(){
		
	}
	public static TaskInfos getIntacne(){
		return _intance;
	}
	
	private Map<String,TaskInfo> tasks = new HashMap<String,TaskInfo>();
	
	public void addTask(TaskInfo task) {
		if(tasks.containsKey(task.getXlsNo())){
			log.warn("任务编号：["+task.getXlsNo()+"]被重复定义，系统覆盖原始版本");
		}
		tasks.put(task.getXlsNo(), task);
	}
	
	
	@Override
	protected void addVo(TaskInfo t) {
		// TODO Auto-generated method stub
		addTask(t);
	}
	

	@Override
	protected Collection<TaskInfo> getSaveObject() {
		List<TaskInfo> list = new ArrayList<TaskInfo>();
		for(TaskInfo task : tasks.values()){
			if(task.isExecTask()){
				list.add(task);
			}
		}
		return list;
	}
	
	public TaskInfo getTaskByXlsNo(String xlsNo){
		return tasks.get(xlsNo);
	}
	
	public Collection<TaskInfo> getAllTasks(){
		return tasks.values();
	}
	
	public Set<String> getAllTaskIds(){
		return tasks.keySet();
	}
	
	public boolean containTaskId(String taskId){
		return tasks.containsKey(taskId);
	}
	
	public TaskInfo createTaskInfo(String taskString){
		TaskInfo task=null;
		String[] taskSub = StringUtils.splitLikeJson(taskString, '[', ']');
		if(taskSub.length<1 || taskSub[0].length()==0){
			log.warn("任务设置告警不正确，忽略此任务设置["+taskString+"]");
			return task;
		}
		int index = taskString.indexOf(",[");
		String[] params = taskString.substring(0, index).split(",");
		if(params.length==3){
			int i=0;
			task = new TaskInfo(params[i++],params[i++],params[i++],taskSub[0],taskSub.length>1?taskSub[1]:"");
			addTask(task);
		}
		return task;
	}
	@Override
	public String[] clearTable() {
		return new String[]{"truncate table z_taskinfo"};
	}

}
