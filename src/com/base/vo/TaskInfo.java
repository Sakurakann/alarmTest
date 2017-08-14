package com.base.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.utils.StringUtils;
/**
 * 告警组与任务之间是通过命名规范来关联的，在定义任务时，若任务时某告警组内的任务，必须以告警组编号_任务编号
 */
public class TaskInfo implements Serializable{
	private static final long serialVersionUID = 4839504995112372374L;
	private static Logger log = Logger.getLogger(TaskInfo.class);
	private String xlsNo = ""; // xls中的任务编号

	private int taskid = -1; // DB中的任务编号

	private int testCode = -1;

	/**
	 * alarmType 0:不告警; 1:测试方式告警; 2:按任务告警;
	 */
	private String alarmType = "0";

	private String alarmString = ""; // xls中告警参数设置

	private String extString = ""; // xls特殊字段
	// <告警点xlsNo,DBNO>;任务为按测试方式告警时，DBNO=-1;按任务告警时，存策略ID；默认 DBNO=-1
	private Map<String, Integer> alarmPoints = null;
	
	private Map<String,String> groupAlarm=null;

	private Map<String, String> taskExt = new HashMap<String, String>();

	private boolean execTask = true;

	/**
	 * 是否是可执行任务，若在初始化话过程中，有相关信息初始化错误，则为不可执行任务，返回false;默认true
	 * 
	 * @return
	 */
	public boolean isExecTask() {
		return execTask;
	}

	public void setExecTask(boolean execTask) {
		this.execTask = execTask;
	}

	public String getXlsNo() {
		return xlsNo;
	}

	public String getAlarmString() {
		return alarmString;
	}

	public String getExtString() {
		return extString;
	}

	public Map<String, String> getTaskExt() {
		return taskExt;
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

	public int getTestCode() {
		return testCode;
	}

	public void setTestCode(int testCode) {
		this.testCode = testCode;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public TaskInfo(String xlsNo, String testCode, String alarmType,
			String alarmString, String extString) {
		this.xlsNo = xlsNo;
		this.testCode = Integer.valueOf(testCode);
		;
		this.alarmType = alarmType;
		setAlarmString(alarmString);
		setExtString(extString);
	}

	/**
	 * @param alarmType
	 *            0:不告警; 1:测试方式告警; 2:按任务告警;
	 */
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public void addAlarmPoint(String xlsPointNo) {
		if (alarmPoints == null) {
			alarmPoints = new HashMap<String, Integer>();
		}
		alarmPoints.put(xlsPointNo+"_"+testCode, -1);
	}
	
	/**
	 * @param xlsPointNo alarmXlsNO;
	 * @param type 是否参与到组告警 1,参与；0,不参与;默认参与
	 */
	public void addGroupAlarm(String xlsPointNo,String type){
		if(groupAlarm==null){
			groupAlarm = new HashMap<String,String>();
		}
		groupAlarm.put(xlsPointNo, type);
	}
	/**
	 * @return 返回组关联的告警点
	 */
	public String getGroupAlarmStr(){
		if(groupAlarm==null){
			log.warn("任务["+xlsNo+"]没有告警点关联到告警组...");
			return "";
		}
		StringBuffer sb = new StringBuffer();
		AlarmPoints pi = AlarmPoints.getIntance();
		AlarmPoint p =null;
		for(Map.Entry<String, String> entry : groupAlarm.entrySet()){
			if("1".equals(entry.getValue())){
				if("2".equals(alarmType)){
					sb.append(alarmPoints.get(entry.getKey()+"_"+testCode)).append(",");
				}else{
					p=pi.getPointById(entry.getKey()+"_"+testCode);
					if(p!=null){
						sb.append(p.getAlarmNoForTest()).append(",");
					}
				}
			}
		}
		if(sb.length()==0){
			log.warn("任务["+xlsNo+"]没有告警点关联到告警组...");
			return "";
		}
		return sb.substring(0, sb.length()-1);
	}
	
	public void addGroupAlarm(String xlsPointNo){
		if(groupAlarm==null){
			groupAlarm = new HashMap<String,String>();
		}
		groupAlarm.put(xlsPointNo, "1");
	}

	public void addExt(String field, String value) {
		taskExt.put(field, value);
	}
	
	public String getExtValue(String fld){
		return taskExt.get(fld);
	}

	public void setAlarmString(String alarmString) {
		if (alarmString != null && !"".equals(alarmString)) {
			String[] points = alarmString.split(",");
			for (String point : points) {
				String[] params = point.split("#");
				if(params !=null && params.length>0){
					addAlarmPoint(params[0]);
					addGroupAlarm(params[0],params.length>1?params[1]:"1");
				}
			}
		}
		this.alarmString = alarmString;
	}

	public void setExtString(String extString) {
		if (extString != null && !"".equals(extString)) {
			String[] exts = extString.split(",");
			for (String ext : exts) {
				int index = ext.indexOf("(");
				if (index == -1 || index == ext.length() - 1) {
					execTask = false;
					log.error("任务编号["+this.xlsNo+"]设置特殊字段不正确，任务将不进行测试");
					return;
				} else {
					String filed = ext.substring(0, index).toLowerCase();
					String[] value = StringUtils.splitLikeJson(ext, '(', ')');
					if (value.length != 1) {
						execTask = false;
						log.error("任务编号["+this.xlsNo+"]设置特殊字段不正确，任务将不进行测试");
						return;
					}
					addExt(filed, value[0]);
				}
			}
		}
		this.extString = extString;
	}

	public Map<String, Integer> getAlarmPoints() {
		return alarmPoints;
	}
	
	
	
	public boolean isTaskAlarm(){
		return "2".equals(alarmType);
	}

}
