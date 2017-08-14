package com.base.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.base.vo.TaskInfo;
import com.base.vo.TaskInfos;
import com.utils.DateUtils;

public class TaskInfoBean extends AbstractDaoBean {
	private static Logger log = Logger.getLogger(TaskInfoBean.class);

	private TaskInfos tasks = TaskInfos.getIntacne();

	private static TaskInfoBean _intance = new TaskInfoBean();

	private TaskInfoBean() {
	}

	public static TaskInfoBean getIntance() {
		return _intance;
	}

	/**
	 * 保存任务
	 * @return
	 */
	public boolean saveTaskInfos() {
		boolean ret = true;
		if (tasks.getInitFlag()) {
			// TODO 清空z_taskInfo表
			String[] sqls = tasks.clearTable();
			if (sqls != null) {
				try{
					for (String sql : sqls) {
						execSql(sql);
					}
				}catch(Exception e){
					ret =false;
					log.error("清除历史数据出错..",e);
					return ret;
				}
				
			}
		}
		Collection<TaskInfo> collection = tasks.getAllTasks();
		if (collection.size() > 0) {
			int seqs[] = getTaskIdBySpeekSequence(collection.size());
			int i = 0;
			for (TaskInfo task : collection) {
				task.setTaskid(seqs[i++]);
			}
			saveTasks();
			updateTaskInfoExtField();
		}
		
		return ret;
	}

	private int[] getTaskIdBySpeekSequence(int num) {
		int[] ret = new int[num];
		Connection con = null;
		int value = num + 10;
		try {

			con = getConnect();
			PreparedStatement sp1 = con
					.prepareStatement("Alter Sequence seq_z_taskinfo_taskid Increment By "
							+ value);
			PreparedStatement sp2 = con
					.prepareStatement("select seq_z_taskinfo_taskid.nextval from dual");
			PreparedStatement sp3 = con
					.prepareStatement("Alter Sequence seq_z_taskinfo_taskid Increment By 1");
			sp1.execute();
			ResultSet rst = sp2.executeQuery();
			if (rst.next()) {
				value = rst.getInt(1);
			}
			sp3.execute();
			sp1.close();
			sp2.close();
			sp3.close();
			con.close();
		} catch (Exception e) {
			log.error("获取告警点编号出错，请查看是否存在sequence[seq_z_taskinfo_taskid]", e);
		}
		value = value - 5;
		for (int i = num; i > 0; i--) {
			ret[i - 1] = value--;
		}
		return ret;
	}

	private boolean saveTasks() {
		boolean ret = true;
		Connection con = null;
		try {
			con = getConnect();
			con.setAutoCommit(false);
			Collection<TaskInfo> collection = tasks.getAllTasks();
			PreparedStatement sp = con
					.prepareStatement("insert into z_taskinfo(TASKID,TASKSID,TASKNAME,TESTCODE,EXECUTEMODE,SENDSTATUS,TASKSTATUS,ALARMFLAG)values(?,?,?,?,?,?,?,?)");
			int count = 0;
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String testName = "测试告警自动数据_" + sf.format(new Date()) + "_";
			for (TaskInfo task : collection) {
				if (!task.isExecTask()) {
					continue;
				}
				int i = 1;
				count++;
				sp.setInt(i++, task.getTaskid());
				sp.setInt(i++, -1);
				sp.setString(i++, testName + i);
				sp.setInt(i++, task.getTestCode());
				sp.setString(i++, "3");
				sp.setString(i++, "2");
				sp.setString(i++, "2");
				sp.setString(i++, task.getAlarmType());
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
			log.error("告警任务导入报错", e);
			ret = false;
		}
		return ret;
	}

	private Map<String, Integer> fieldMD = null;

	public Map<String, Integer> getFieldInfo() {
		Connection con = null;
		Map<String, Integer> map = new HashMap<String,Integer>();
		try {
			con = getConnect();
			PreparedStatement sp = con
					.prepareStatement("select * from z_taskinfo where 1=2");// TODO ? 1=2 查不到数据
			sp.executeQuery();
			ResultSetMetaData metaData = sp.getMetaData();
			int count = metaData.getColumnCount();
			for (int i = 1; i <= count; i++) {
				map.put(metaData.getColumnName(i).toLowerCase(), metaData
						.getColumnType(i));
			}
			sp.close();
			con.close();
		} catch (Exception e) {
			log.error("查询z_taskinfo表字段属性报错", e);
		}
		return map;
	}

	public boolean updateTaskInfoExtField() {
		boolean ret = true;
		Collection<TaskInfo> collection = tasks.getAllTasks();
		for (TaskInfo task : collection) {
			if (!task.isExecTask() || task.getTaskExt() == null
					|| task.getTaskExt().size() == 0) {
				continue;
			}
			parseFieldExtValue(task);
			if (task.isExecTask()) {
				updateTaskInfoExt(task);
			}
		}
		return ret;
	}

	private void updateTaskInfoExt(TaskInfo task) {
		Connection con = null;
		try {
			con = getConnect();
			StringBuffer sb = new StringBuffer("update z_taskinfo set ");
			StringBuffer vsb = new StringBuffer();
			boolean f = true;
			for (String field : task.getTaskExt().keySet()) {
				if (f) {
					sb.append(field).append("=?");
					f = false;
				} else {
					sb.append(",").append(field).append("=?");
				}
			}
			sb.append(" where taskid=?");
			log.debug(sb.toString());
			PreparedStatement sp = con.prepareStatement(sb.toString());
			getFieldfieldMD();
			int fromIndex=1;
			for (String field : task.getTaskExt().keySet()) {
				int type = fieldMD.get(field);
				switch (type) {
				case Types.NUMERIC:
				case Types.DOUBLE: {
					sp.setDouble(fromIndex++, Double.valueOf(task.getTaskExt().get(field)));
					break;
				}
				case Types.FLOAT: {
					sp.setFloat(fromIndex++, Float.valueOf(task.getTaskExt().get(field)));
					break;
				}
				case Types.VARCHAR:
				case Types.CHAR: {
					sp.setString(fromIndex++, task.getTaskExt().get(field).toString());
					break;
				}
				case Types.DATE:
				case Types.TIMESTAMP:
				case Types.TIME: {
					sp.setTimestamp(fromIndex++, DateUtils.toTimestamp(task.getTaskExt().get(field)));
				}
				}
			}
			sp.setInt(fromIndex, task.getTaskid());
			sp.executeUpdate();
			sp.close();
			con.close();
		} catch (Exception e) {
			task.setExecTask(false);
			log.error("查询z_taskinfo表字段属性报错", e);
		}
	}

	private Map<String, Integer> getFieldfieldMD() {
		if (fieldMD == null) {
			fieldMD = getFieldInfo();
		}
		return fieldMD;
	}

	private void parseFieldExtValue(TaskInfo task) {
		getFieldfieldMD();
		boolean b = true;
		for (String field : task.getTaskExt().keySet()) {
			if (!fieldMD.containsKey(field)) {
				b = false;
				log.error("任务编号[" + task.getXlsNo() + "],在设置特殊字段中定义的字段["
						+ field + "]在z_taskinfo中不存在,任务将不会执行");
				break;
			}
		}
		if (b) {
			for (String field : task.getTaskExt().keySet()) {
				String value = task.getTaskExt().get(field);
				if (value.startsWith("sql:")) {
					try {
						List<String[]> ret = query(value.substring(3));
						String reVal = "";
						if (ret.size() > 0 && ret.get(0).length > 0) {
							reVal = ret.get(0)[0];
						}
						task.getTaskExt().put(field, reVal);
					} catch (Exception e) {
						b = false;
						log.error("任务编号[" + task.getXlsNo()
								+ "],在设置特殊字段中定义的字段[" + field
								+ "]的值在执行sql时报错,任务将不会执行", e);
						break;
					}
				}
			}
		}
		if (!b) {
			task.setExecTask(false);
		}
	}
	
}
