package com.base.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import org.apache.log4j.Logger;

import com.base.vo.GroupAlarm;
import com.base.vo.GroupAlarms;

public class GroupAlarmBean extends AbstractDaoBean {
private static Logger log = Logger.getLogger(GroupAlarmBean.class);
	
	private GroupAlarms groups = GroupAlarms.getIntance();

	private static GroupAlarmBean _intance = new GroupAlarmBean();

	private GroupAlarmBean() {
	}

	public static GroupAlarmBean getIntance() {
		return _intance;
	}
	
	public boolean saveGroupAlarms(){
		boolean ret=true;
		if (groups.getInitFlag()) {
			//TODO 清表 z_group_alaram
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
		
		groups.checkGroupAlarm();
		Collection<GroupAlarm> collection = groups.getAllGroups();
		int size = collection.size();
		if(size>0){
			int[] ids = getGroupAlarmBySpeekSequence(size);
			int i=0;
			for(GroupAlarm group : collection){
				group.setId(ids[i++]);
			}
			writeToDB();
		}
		return ret;
	}
	
	private int[] getGroupAlarmBySpeekSequence(int num) {
		int[] ret = new int[num];
		Connection con = null;
		int value = num + 10;
		try {

			con = getConnect();
			ResultSet rst =null;
			PreparedStatement sp2 = con
					.prepareStatement("select seq_z_group_alaram_id.nextval from dual");
			if (num > 1) {
				PreparedStatement sp1 = con
						.prepareStatement("Alter Sequence seq_z_group_alaram_id Increment By "
								+ value);

				PreparedStatement sp3 = con
						.prepareStatement("Alter Sequence seq_z_group_alaram_id Increment By 1");
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
			log.error("获取告警点编号出错，请查看是否存在sequence[seq_z_group_alaram_id]", e);
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
		Collection<GroupAlarm> collection = groups.getAllGroups();
		try {
			con = getConnect();
			con.setAutoCommit(false);
			PreparedStatement sp = con.prepareStatement("insert into z_group_alaram(ID,NAME,ALARMPARAM,ALARMTYPE,GROUPS,STATUS)values(?,?,?,?,?,1)");
			int count = 0;
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String testName = "测试告警群数据_" + sf.format(new Date()) + "_";
			for (GroupAlarm group : collection) {
				if(!group.isExecFlag()){
					continue;
				}
				int i = 1;
				count++;
				sp.setInt(i++, group.getId());
				sp.setString(i++, testName+count);
				sp.setString(i++, group.getValue());
				sp.setString(i++, group.getAlarmType());
				sp.setString(i++, group.getGroupIdsString());
				sp.addBatch();
				if (count % batchCount == 0) {
					sp.executeBatch();
				}
			}
			if (count % batchCount > 0) {
				sp.executeBatch();
			}
			con.commit();
			con.setAutoCommit(true);
			sp.close();
			con.close();
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			ret = false;
			log.error("生成告警群信息时报错...", e);
		}
		
		return ret;
	}
}
