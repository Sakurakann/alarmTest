package com.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.base.bean.AbstractDaoBean;
import com.base.bean.AlarmGroupBean;
import com.base.bean.AlarmPointBean;
import com.base.bean.BillBean;
import com.base.bean.GroupAlarmBean;
import com.base.bean.TaskInfoBean;
import com.base.vo.AlarmGroups;
import com.base.vo.AlarmPoints;
import com.base.vo.BillFormats;
import com.base.vo.Bills;
import com.base.vo.GroupAlarms;
import com.base.vo.TaskInfo;
import com.base.vo.TaskInfos;
import com.base.vo.XlsRecord;
import com.base.vo.XlsRecords;
import com.utils.StringUtils;

public class AlarmTest implements Runnable {
	private static Logger log = Logger.getLogger(AlarmTest.class);
	private GlobalConfig config = GlobalConfig.getIntance();

	private void printf() {
		log.info("告警配置文件地址："+ StringUtils.trim(config.getProperty(GlobalConfig.ALARMTEST_CONFIG)));
		log.info("话单格式配置文件地址："+ StringUtils.trim(config.getProperty(GlobalConfig.BILL_CONFIG)));
		log.info("BILL服务器地址：["+ StringUtils.trim(config.getProperty(GlobalConfig.BILL_IP))+ ":"+ StringUtils.trim(config.getProperty(GlobalConfig.BILL_PORT))+ "]");
	}

	public void run() {
		printf();
		if (!AbstractDaoBean.isDbInit()) {
			log.error("数据库连接异常,程序退出..");
			return;
		}
		boolean b = false;
		if (config.reBuildTasks()) {
			List<XlsRecord> list = AlarmRead.readConfigFile(StringUtils.trim(config.getProperty(GlobalConfig.ALARMTEST_CONFIG)));
			if (list.size() > 0) {
				BillRead.readConfigFile(StringUtils.trim(config.getProperty(GlobalConfig.BILL_CONFIG)));
			}
			BillFormats format = BillFormats.getIntance();
			if (format.parseFormats()) {
				b = writeToDB();
			}
		}else{

//			b = readFromFile();
			BillRead.readConfigFile(StringUtils.trim(config.getProperty(GlobalConfig.BILL_CONFIG)));
			BillFormats format = BillFormats.getIntance();
			b = format.parseFormats();
			if(b){
				b = readFromFile();
			}
			if(!b){
				log.error("读取缓存文件失败,请重新按新建任务执行测试...");
			}
		}
		if (b)
			startBillServer();

	}

	private boolean writeToDB() {
		boolean b = AlarmPointBean.getIntance().savePoints(); // 写告警点并生成按测试方式告警 清表! z_alarmpoint\z_alarmpolicy
		if (!b) {
			log.error("生成告警点时出错，请检查告警点配置...");
			return b;
		}
		b = TaskInfoBean.getIntance().saveTaskInfos();// 写任务 清表! z_taskinfo
		if (!b) {
			log.error("生成任务时出错，请检查任务配置...");
			return b;
		}
		TaskInfos tasks = TaskInfos.getIntacne();
		for (TaskInfo task : tasks.getAllTasks()) {
			if (task.isTaskAlarm() && task.isExecTask()) {
				AlarmPointBean.getIntance().saveTaskPolicy(task.getAlarmPoints(), task.getTaskid());
			}
		}
		b = AlarmGroupBean.getIntance().saveGroups();//清表! z_alarm_group\z_task_alarm_group
		if (!b) {
			log.error("生成告警组时出错，请检查告警组配置...");
			return b;
		}
		b = GroupAlarmBean.getIntance().saveGroupAlarms(); //清表! z_group_alaram
		if (!b) {
			log.error("生成告警群时出错，请检查告警群配置...");
			return b;
		}
		writeToFile();
		return b;
	}

	private void startBillServer() {

		List<XlsRecord> eableRecord = getEableExecRecord();
		BillBean billBean = new BillBean(eableRecord);
		billBean.stratBillServer();
	}

	/**
	 * 返回可执行测试任务集
	 *
	 * @param
	 * @return list
	 */
	private List<XlsRecord> getEableExecRecord() {
		List<XlsRecord> eableRecord = new ArrayList<XlsRecord>();
		for (XlsRecord record : XlsRecords.getIntance().getAllXlsRecord()) {
			if (record.isExecFlag() && Bills.checkListExec(record.getBills())) {
				eableRecord.add(record);
			} else {
				log.error("告警配置文件中第[" + record.getRecordLineNo()+ "]行定义的话单未能正常发送，可能是话单相关的任务未正确定义");
			}
		}
		return eableRecord;
	}

	/**
	 * 写入文件
	 */
	private void writeToFile() {
		String path = this.getClass().getResource("/").getPath()+"data";
		File pathF = new File(path);
		if(!pathF.exists()){
			pathF.mkdirs();
		}
		AlarmPoints.getIntance().writeToFile(path + "/alarmPoint.tmp");
		TaskInfos.getIntacne().writeToFile(path + "/tasks.tmp");
		AlarmGroups.getIntance().writeToFile(path + "/alarmGroup.tmp");
		GroupAlarms.getIntance().writeToFile(path + "/groupAlarm.tmp");
		Bills.getIntacne().writeToFile(path + "/com.bill.tmp");
		XlsRecords.getIntance().writeToFile(path + "/xlsRecord.tmp");
	}

	private boolean readFromFile() {
		String path = this.getClass().getResource("/").getPath()+"data";
		return AlarmPoints.getIntance().readFromFile(path + "/alarmPoint.tmp")
				&& TaskInfos.getIntacne().readFromFile(path + "/tasks.tmp")
				&& AlarmGroups.getIntance().readFromFile(path + "/alarmGroup.tmp")
				&& GroupAlarms.getIntance().readFromFile(path + "/groupAlarm.tmp")
				&& Bills.getIntacne().readFromFile(path + "/com.bill.tmp")
				&& XlsRecords.getIntance().readFromFile(path + "/xlsRecord.tmp");
	}
}
