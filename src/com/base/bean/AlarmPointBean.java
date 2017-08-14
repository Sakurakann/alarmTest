package com.base.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.base.vo.AlarmPoint;
import com.base.vo.AlarmPoints;

/**
 * 单例类,操作类
 * 
 * @author Administrator
 */
public class AlarmPointBean extends AbstractDaoBean {
	private static Logger log = Logger.getLogger(AlarmPointBean.class);

	private AlarmPoints points = AlarmPoints.getIntance();

	private static AlarmPointBean _intance = new AlarmPointBean();

	private AlarmPointBean() {
	}

	public static AlarmPointBean getIntance() {
		return _intance;
	}

	private boolean _init = false;

	/**
	 * 保存告警点，并同步生成按测试方式告警策略
	 */
	public boolean savePoints() {
		if (_init) {
			return true;
		}
		_init = true;
		boolean ret = true;
		if (points.getInitFlag()) {
			// TODO 清空z_alarmpoint\z_alarmpolicy
			String[] sqls = points.clearTable();
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

		Collection<AlarmPoint> collection = points.getAllPoints();
		int len = collection.size();
		if (len == 0) {
			log.warn("未正确配置一个告警点.....");
		}
		int[] pointNums = getAlarmPointBySpeekSequence(len);
		int[] policyNums = getAlarmpolicyBySpeekSequence(len);
		int i = 0;
		for (AlarmPoint point : collection) {
			point.setAlarmPointNO(pointNums[i]);
			point.setAlarmNoForTest(policyNums[i++]);
		}
		ret = saveDBPoint();
		if (ret)
			ret = saveDBPolicy();
		return ret;

	}

	private int[] getAlarmPointBySpeekSequence(int num) {
		int[] ret = new int[num];
		Connection con = null;
		int value = num + 10;
		try {
			con = getConnect();
			ResultSet rst = null;
			PreparedStatement sp2 = con
					.prepareStatement("select seq_z_alarmpoint_id.nextval from dual");
			if (num > 1) {
				PreparedStatement sp1 = con
						.prepareStatement("Alter Sequence seq_z_alarmpoint_id Increment By "
								+ value);
				PreparedStatement sp3 = con
						.prepareStatement("Alter Sequence seq_z_alarmpoint_id Increment By 1");
				sp1.execute();
				rst = sp2.executeQuery();
				if (rst.next()) {
					value = rst.getInt(1);
				}
				sp3.execute();
				sp1.close();
				sp3.close();

				value = value - 5;
				for (int i = num; i > 0; i--) {
					ret[i - 1] = value--;
				}
			} else {
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
			log.error("获取告警点编号出错，请查看是否存在sequence[seq_z_alarmpoint_id]", e);
		}
		return ret;
	}

	private int[] getAlarmpolicyBySpeekSequence(int num) {
		int[] ret = new int[num];
		Connection con = null;
		int value = num + 10;
		try {
			con = getConnect();
			ResultSet rst =null;
			PreparedStatement sp2 = con
					.prepareStatement("select seq_z_alarmpolicy_id.nextval from dual");
			if (num > 1) {
				PreparedStatement sp1 = con
						.prepareStatement("Alter Sequence seq_z_alarmpolicy_id Increment By "
								+ value);

				PreparedStatement sp3 = con
						.prepareStatement("Alter Sequence seq_z_alarmpolicy_id Increment By 1");
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
			log.error("获取告警点编号出错，请查看是否存在sequence[seq_z_alarmpolicy_id]", e);
		}
		value = value - 5;
		for (int i = num; i > 0; i--) {
			ret[i - 1] = value--;
		}
		return ret;
	}

	private boolean saveDBPoint() {
		boolean ret = true;
		Connection con = null;
		try {
			con = getConnect();
			con.setAutoCommit(false);
			PreparedStatement sp = con
					.prepareStatement("insert into z_alarmpoint(ALARMNUMBER,POINTNAME,SMPNAME,TESTCODE,ALARMCLASS,ALARMLEVEL,ALARMTYPE,ALARMPARAMTYPE,ALARMPARAM,ALARMDATA,CLEARMODE,CLEARPARAMTYPE,CLEARPARAM,ISTDATE,UPTDATE) values(?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,sysdate)");
			Collection<AlarmPoint> collection = points.getAllPoints();
			int count = 0;
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String testName = "测试告警数据_" + sf.format(new Date()) + "_";
			for (AlarmPoint point : collection) {
				int i = 1;
				count++;
				sp.setInt(i++, point.getAlarmPointNO());
				sp.setString(i++, testName + count);
				sp.setString(i++, testName + count);
				sp.setInt(i++, point.getTestCode());
				sp.setString(i++, "数据告警");
				sp.setString(i++, "2");
				sp.setString(i++, "1");
				sp.setInt(i++, point.getALARMPARAMTYPE());
				sp.setString(i++, point.getAlarmValue());
				sp.setString(i++, point.getValue());
				sp.setString(i++, "0");
				sp.setInt(i++, point.getCLEARPARAMTYPE());
				sp.setString(i++, point.getClearValue());
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
			log.debug("告警点导入成功.....");
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			ret = false;
			log.error("获取告警点编号出错，请查看是否存在sequence[seq_z_alarmpoint_id]", e);
		}
		return ret;
	}

	private boolean saveDBPolicy() {
		boolean ret = true;
		Connection con = null;
		try {
			con = getConnect();
			PreparedStatement sp = con.prepareStatement("insert into Z_ALARMPOLICY(ID,ALARMNUMBER,TASKID,TESTCODE,ALARMPARAMTYPE,ALARMPARAM,ALARMDATA,CLEARMODE,CLEARPARAMTYPE,CLEARPARAM,ISTDATE,UPTDATE) values(?,?,?,?,?,?,?,?,?,?,sysdate,sysdate)");
			Collection<AlarmPoint> collection = points.getAllPoints();
			int count = 0;
			int nos=10000;
			for (AlarmPoint point : collection) {
				int i = 1;
				count++;
				sp.setInt(i++, point.getAlarmNoForTest());
				sp.setInt(i++, point.getAlarmPointNO());
				sp.setInt(i++, -1);
				sp.setInt(i++, point.getTestCode());
				sp.setInt(i++, point.getALARMPARAMTYPE());
				sp.setString(i++, point.getAlarmValue());
				sp.setString(i++, point.getValue());
				sp.setString(i++, "0");
				sp.setInt(i++, point.getCLEARPARAMTYPE());
				sp.setString(i++, point.getClearValue());
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
			log.debug("按测试方式告警导入成功.....");
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			ret = false;
			log.error("生成按测试方式告警时插入报错....", e);
		}
		return ret;
	}

	/**
	 * 写按任务告警策略
	 * @param map<alarmXlsNo,DbNo>
	 * @param taskId
	 * @return
	 */
	public void saveTaskPolicy(Map<String,Integer> map,int taskid) {
		if(map ==null || map.size()==0)return;
		int size = map.size();
		int[] ids = getAlarmpolicyBySpeekSequence(size);
		int i=0;
		for(String axls : map.keySet()){
			map.put(axls, ids[i++]);
		}
		Connection con = null;
		try {
			con = getConnect();
			PreparedStatement sp = con
					.prepareStatement("insert into Z_ALARMPOLICY(ID,ALARMNUMBER,TASKID,TESTCODE,ALARMPARAMTYPE,ALARMPARAM,ALARMDATA,CLEARMODE,CLEARPARAMTYPE,CLEARPARAM,ISTDATE,UPTDATE) values(?,?,?,?,?,?,?,?,?,?,sysdate,sysdate)");
			int count = 0;
			AlarmPoint point=null;
			for (String key : map.keySet()) {
				point = points.getPointById(key);
				if(point==null){
					continue;
				}
				i = 1;
				count++;
				sp.setInt(i++, map.get(key));
				sp.setInt(i++, point.getAlarmPointNO());
				sp.setInt(i++, taskid);
				sp.setInt(i++, point.getTestCode());
				sp.setInt(i++, point.getALARMPARAMTYPE());
				sp.setString(i++, point.getAlarmValue());
				sp.setString(i++, point.getValue());
				sp.setString(i++, "0");
				sp.setInt(i++, point.getCLEARPARAMTYPE());
				sp.setString(i++, point.getClearValue());
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
			log.debug("生成任务["+taskid+"]的告警告警导入成功.....");
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			log.error("生成任务["+taskid+"]的告警时出错", e);
		}
	}
}
