package com.base.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.base.vo.AlarmGroup;
import com.base.vo.AlarmGroups;
import com.base.vo.TaskInfo;
import com.base.vo.TaskInfos;

public class AlarmGroupBean extends AbstractDaoBean {
	private static Logger log = Logger.getLogger(AlarmGroupBean.class);
	
	private AlarmGroups groups = AlarmGroups.getIntance();

	private static AlarmGroupBean _intance = new AlarmGroupBean();

	private AlarmGroupBean() {
	}

	public static AlarmGroupBean getIntance() {
		return _intance;
	}
	
	public boolean saveGroups(){
		boolean ret=true;
		if (groups.getInitFlag()) {
			// TODO 清空z_alarm_group\z_task_alarm_group
			String[] sqls = groups.clearTable();
			if (sqls != null) {
				try {
					for (String sql : sqls) {
						execSql(sql);
					}
				} catch (Exception e) {
					ret = false;
					log.error("清除历史数据出错..", e);
					return ret;
				}
			}
		}
		groups.checkGroup(); //设置存在可写入组策略
		
		Collection<AlarmGroup> collection = groups.getAllGroup();
		if(collection.size()>0){
			int[] ids = getAlarmGroupBySpeekSequence(collection.size());
			int i=0;
			for(AlarmGroup group : collection){
				group.setGroupId(ids[i++]);
			}
			ret = writeToDB();
		}
		
		
		
		return ret;
	}
	
	
	private int[] getAlarmGroupBySpeekSequence(int num) {
		int[] ret = new int[num];
		Connection con = null;
		int value = num + 10;
		try {
			con = getConnect();
			ResultSet rst =null;
			PreparedStatement sp2 = con
					.prepareStatement("select seq_z_alarm_group_id.nextval from dual");
			if (num > 1) {
				PreparedStatement sp1 = con
						.prepareStatement("Alter Sequence seq_z_alarm_group_id Increment By "
								+ value);

				PreparedStatement sp3 = con
						.prepareStatement("Alter Sequence seq_z_alarm_group_id Increment By 1");
				sp1.execute();
				rst = sp2.executeQuery();
				if (rst.next()) {
					value = rst.getInt(1);
				}
				sp3.execute();
				sp1.close();
				
				sp3.close();
			}else{
				rst = sp2.executeQuery();
				if (rst.next()) {
					value = rst.getInt(1);
				}
				ret[0] = value;
			}
			rst.close();
			sp2.close();
			con.close();
		} catch (Exception e) {
			log.error("获取告警点编号出错，请查看是否存在sequence[seq_z_alarm_group_id]", e);
		}
		value = value - 5;
		for (int i = num; i > 0; i--) {
			ret[i - 1] = value--;
		}
		return ret;
	}
	
	private int[] getTaskAlarmGroupBySpeekSequence(int num) {
		int[] ret = new int[num];
		Connection con = null;
		int value = num + 10;
		try {
			con = getConnect();
			ResultSet rst =null;
			PreparedStatement sp2 = con
					.prepareStatement("select seq_z_task_alarm_group_id.nextval from dual");
			if (num > 1) {
				PreparedStatement sp1 = con
						.prepareStatement("Alter Sequence seq_z_task_alarm_group_id Increment By "
								+ value);

				PreparedStatement sp3 = con
						.prepareStatement("Alter Sequence seq_z_task_alarm_group_id Increment By 1");
				sp1.execute();
				rst = sp2.executeQuery();
				if (rst.next()) {
					value = rst.getInt(1);
				}
				sp3.execute();
				sp1.close();
				
				sp3.close();
			}else{
				rst = sp2.executeQuery();
				if (rst.next()) {
					value = rst.getInt(1);
				}
				ret[0] = value;
			}
			rst.close();
			sp2.close();
			con.close();
		} catch (Exception e) {
			log.error("获取告警点编号出错，请查看是否存在sequence[seq_z_task_alarm_group_id]", e);
		}
		value = value - 5;
		for (int i = num; i > 0; i--) {
			ret[i - 1] = value--;
		}
		return ret;
	}
	
	private boolean writeToDB(){
		boolean ret =true;
		Connection con = null;
		Collection<AlarmGroup> collection = groups.getAllGroup();
		TaskInfos taskInfos = TaskInfos.getIntacne();
		try {
			con = getConnect();
			con.setAutoCommit(false);
			PreparedStatement sp = con.prepareStatement("insert into z_alarm_group(GROUPID,GROUPNAME,ALARMPARAM,ALARMTYPE,ALARMFLAG,STATE)values(?,?,?,?,?,1)");
			PreparedStatement sp2 = con.prepareStatement("insert into Z_Task_Alarm_Group(ID,TASKID,GROUPID,REMARK,ALARMFLAG)values(seq_z_task_alarm_group_id.nextval,?,?,?,?)");
			PreparedStatement sp3 = con.prepareStatement("update z_taskinfo set alarmflag=case alarmflag when '2' then '5' when '1' then '4' else alarmflag end where taskid=? ");
			int count = 0;
			int countT=0;
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String testName = "测试告警组数据_" + sf.format(new Date()) + "_";
			for (AlarmGroup group : collection) {
				if(!group.isExecFlag()){
					continue;
				}
				int i = 1;
				count++;
				sp.setInt(i++, group.getGroupId());
				sp.setString(i++, testName+count);
				sp.setString(i++, group.getValue());
				sp.setString(i++, group.getAlarmType());
				sp.setString(i++, group.getFlag());
				sp.addBatch();
				if (count % batchCount == 0) {
					sp.executeBatch();
				}
				
				List<String> tasks = group.getRelTasks();
				TaskInfo taskinfo=null;
				for(String xlsNo : tasks){
					int j=1;
					taskinfo = taskInfos.getTaskByXlsNo(xlsNo);
					countT++;
					sp2.setInt(j++, group.getGroupId());
					sp2.setInt(j++, taskinfo.getTaskid());
					sp2.setString(j++, taskinfo.getGroupAlarmStr());
					sp2.setInt(j++, Integer.valueOf(taskinfo.getAlarmType()));
					sp2.addBatch();
					
					sp3.setInt(1, taskinfo.getTaskid());
					sp3.addBatch();
					if (countT % batchCount == 0) {
						sp2.executeBatch();
						sp3.executeBatch();
					}
				}
			}
			if (count % batchCount > 0) {
				sp.executeBatch();
			}
			if (countT % batchCount > 0) {
				sp2.executeBatch();
				sp3.executeBatch();
			}
			con.commit();
			con.setAutoCommit(true);
			sp.close();
			sp2.close();
			sp3.close();
			con.close();
			log.debug("组告警生成成功.....");
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			ret = false;
			log.error("生成告警组信息时报错...", e);
		}
		
		return ret;
	}

}
