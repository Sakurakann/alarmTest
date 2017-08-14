package com.base.vo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


public class AlarmPoints extends ITableVo<AlarmPoint> {
	private static Logger log = Logger.getLogger(AlarmPoints.class);
	private static AlarmPoints _intance= new AlarmPoints();
	
//	private List<AlarmPoint> points = new ArrayList<AlarmPoint>(10);
	/**
	 * key:告警编号_测试类型
	 */
	private Map<String,AlarmPoint> pointMap =new HashMap<String,AlarmPoint>(20);
	
	private AlarmPoints(){}
	
	public static AlarmPoints getIntance(){
		return _intance;
	}

	@Override
	protected void addVo(AlarmPoint t) {
		addAlarmPoint(t);
	}

	public void addAlarmPoint(AlarmPoint point) {
		if(pointMap.containsKey(point.getXlsNo()+"_"+point.getTestCode())){
			log.warn("告警编号["+point.getXlsNo()+"];测试类型["+point.getTestCode()+"]被重复定义，系统覆盖原始版本");
		}
		pointMap.put(point.getXlsNo()+"_"+point.getTestCode(), point);
	}
	
	
	
	@Override
	protected Collection<AlarmPoint> getSaveObject() {
		return pointMap.values();
	}

	public boolean containKey(String key){
		return pointMap.containsKey(key);
	}
	
	public AlarmPoint getPointById(String key){
		return pointMap.get(key);
	}
	
	public Collection<AlarmPoint> getAllPoints(){
		return pointMap.values();
	}
	
	public AlarmPoint createPoint(String pointStr) {
		AlarmPoint point =null;
		if(pointStr !=null && !"".equals(pointStr)){
			if(pointStr.startsWith("alarm:")){
				String poitnNo =  pointStr.substring(6);
				point = pointMap.get(poitnNo);
			}else{
				String[] params = pointStr.split(",");
				if(params.length==7){
					int i=0;
					point = new AlarmPoint(params[i++],params[i++],params[i++],params[i++],params[i++],params[i++],params[i++]);
					addAlarmPoint(point);
				}
			}
		}
		return point;
	}
	
	public String[] clearTable() {
		return new String[] { "truncate table z_alarmpoint","truncate table z_alarmpolicy" };
	}

}
