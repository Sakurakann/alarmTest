package com.base.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.utils.StringUtils;

/**
 * 告警组与任务之间是通过命名规范来关联的，在定义任务时，若任务时某告警组内的任务，必须以告警组编号_任务编号;
 * 定义告警组，若为非统计策略时，定义告警参数中定义的任务编号是不带告警组编号的，不同任务组之间可以有相同的任务编号，告警组编号+任务编号是唯一的
 */
public class AlarmGroup implements Serializable {
	private static final long serialVersionUID = 8781561443287488288L;

	private String xlsNo="";
	
	//数据库编号
	private int groupId=-1;
	
	//1:统计策略;2:限定策略,3综合策略
	private String alarmType="";
	
	//是否屏蔽普通告警 1:屏蔽普通告警;0:不屏蔽
	private String flag="1";
	
	
	private String value=""; //告警参数
	
	private String pointString="";
	
	private List<String> points = null;
	
	private List<String> tasks=null;
	
	private List<String> relTasks = null;
	
	private boolean execFlag=true; //是否可执行组,在检查组时，会赋一次值
	
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
		parseTaskIds();
	}

	public String getFlag() {
		return flag;
	}

	/**
	 * @param flag 1:屏蔽普通告警;0:不屏蔽
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getValue() {
		return value;
	}
	
	public void setRelValue(String value){
		this.value = value;
	}

	public void setValue(String value) {
		this.value = value;
		parseTaskIds();
	}

	public List<String> getPoints() {
		return points;
	}

	public void addPoint(String point) {
		if(points==null){
			points = new ArrayList<String>();
		}
		points.add(point);
	}

	public String getPointString() {
		return pointString;
	}
	
	private String createPoint(String point){
		AlarmPoint alarmPoint = AlarmPoints.getIntance().createPoint(point);
		if(alarmPoint !=null){
			return alarmPoint.getXlsNo()+"_"+alarmPoint.getTestCode();
		}
		return "";
	}

	public void setPointString(String pointString) {
		if(pointString !=null && !"".equals(pointString)){
			String[] ps = StringUtils.splitLikeJson(pointString, '{', '}');
			for(String p : ps){
				String no = createPoint(p);
				if(no.length()>0){
					addPoint(no);
				}
			}
		}
		this.pointString = pointString;
	}
	
	public static void main(String[] args){
		String s="[{1,2,3,4,{s,f,g}},{sd,er,io,{sde,sd,e}]";
		long time1 = System.currentTimeMillis();
		String[] d = StringUtils.splitLikeJson(s, '<', '>');
		System.out.println(s.indexOf("["));
		System.out.println(System.currentTimeMillis()-time1);
		for(String ds : d){
			System.out.println(ds);
		}
	}
	//组任务在定义时，是组编号+任务编号
	private void parseTaskIds(){
		if(!"1".equals(alarmType) && !"".equals(StringUtils.trim(value))){
			tasks = new ArrayList<String>();
			String[] taskSub = value.split("\\$");
			for(String task : taskSub){
				String[] params = task.split("#");
				if(params.length==2){
					tasks.add(xlsNo+"_"+params[0]);
				}
			}
		}
	}

	public List<String> getTasks() {
		return tasks;
	}

	public AlarmGroup(String xlsNo, String alarmType, String flag,
			String value, String pointString) {
		this.xlsNo = xlsNo;
		setAlarmType(alarmType);
		this.flag = flag;
		setValue(value);
		setPointString(pointString);
	}

	public boolean isExecFlag() {
		return execFlag;
	}
	/**
	 * 是否可执行
	 * @param execFlag
	 */
	public void setExecFlag(boolean execFlag) {
		this.execFlag = execFlag;
	}
	

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public List<String> getRelTasks() {
		return relTasks;
	}
	
	public void addRelTask(String taskXlsNo){
		if(relTasks==null){
			relTasks = new ArrayList<String>();
		}
		relTasks.add(taskXlsNo);
	}
}
