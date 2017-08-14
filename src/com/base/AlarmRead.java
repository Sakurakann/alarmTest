package com.base;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.base.vo.AlarmGroup;
import com.base.vo.AlarmGroups;
import com.base.vo.AlarmPoint;
import com.base.vo.AlarmPoints;
import com.base.vo.Bill;
import com.base.vo.Bills;
import com.base.vo.GroupAlarm;
import com.base.vo.GroupAlarms;
import com.base.vo.TaskInfo;
import com.base.vo.TaskInfos;
import com.base.vo.XlsRecord;
import com.base.vo.XlsRecords;
import com.utils.StringUtils;

public class AlarmRead {
	private static Logger log = Logger.getLogger(AlarmRead.class);

	public static List<XlsRecord> readConfigFile(String file) {
		List<XlsRecord> list = new ArrayList<XlsRecord>();
		try {
			InputStream in = new FileInputStream(file);
			POIFSFileSystem fs = new POIFSFileSystem(in);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			int capacity = sheet.getLastRowNum();
			int colNum = sheet.getRow(0).getLastCellNum();
			if (colNum != 7) {
				log.error("告警测试定义文件格式不正确，文件应定义为7列，分别为：[测试名称、普通告警定义、组告警定义、群告警定义、任务定义、话单与验、证循环定义]");
				return null;
			}
			XlsRecord record = null;
			HSSFRow row = null;
			for (int i = 1; i <= capacity; i++) {
				row = sheet.getRow(i);
				String taskName = getCellString(row.getCell((short) 0));
				String alarmString = getCellString(row.getCell((short) 1));
				String alarmGroupString = getCellString(row.getCell((short) 2));
				String groupAlarmString = getCellString(row.getCell((short) 3));
				String taskString = getCellString(row.getCell((short) 4));
				String billString = getCellString(row.getCell((short) 5));
				String cycleString = StringUtils.trim(getCellString(row.getCell((short) 6)));
				record = new XlsRecord();
				XlsRecords.getIntance().addXlsRecord(record);
				record.setTestName(taskName);
				if (!"".equals(cycleString)) {
					try {
						int cycle = Integer.valueOf(cycleString);
						record.setCycleTime(cycle*60*1000);
					} catch (Exception e) {
						log.error("第[" + record.getRecordLineNo()
								+ "]行,循环周期配置错误，不能转换成数字类型");
						continue;
					}
				}
				parseAlarmString(record, alarmString);
				parseAlarmGroupString(record, alarmGroupString);
				parseGroupAlarmString(record, groupAlarmString);
				parseTaskString(record, taskString);
				parseBillString(record, billString);

				list.add(record);
			}
			in.close();
		} catch (Exception e) {
			log.error("读取文件出错,文件路径[" + file + "]", e);
		}

		return list;
	}

	/**
	 * 解析普通告警
	 * @param record
	 * @param recordRtring
	 */
	private static void parseAlarmString(XlsRecord record, String recordRtring) {
		if (!"".equals(recordRtring)) {
			String[] alarmParams = StringUtils.splitLikeJson(recordRtring, '[',']');
			if (alarmParams.length != 0 && !"".equals(alarmParams[0])) {
				AlarmPoint point = null;
				String[] aStr = StringUtils.splitLikeJson(alarmParams[0], '{','}');
				for (String alarm : aStr) {
					point = AlarmPoints.getIntance().createPoint(alarm);
					if (point != null) {
						record.addAlarm(point.getXlsNo()+"_"+point.getTestCode());
					}
				}
			}
		}
	}

	/**
	 * 解析告警组
	 * @param record
	 * @param recordRtring
	 */
	private static void parseAlarmGroupString(XlsRecord record,
			String recordRtring) {
		if (!"".equals(recordRtring)) {
			String[] alarmGParams = StringUtils.splitLikeJson(recordRtring,'[', ']');
			if (alarmGParams.length != 0 && !"".equals(alarmGParams[0])) {
				AlarmGroup agroup = null;
				String[] aStr = StringUtils.splitLikeJson(alarmGParams[0], '{','}');
				for (String alarm : aStr) {
					agroup = AlarmGroups.getIntance().createAlarmGroup(alarm);
					if (agroup != null) {
						record.addAlarmGroups(agroup.getXlsNo());
					}
				}
			}
		}
	}

	/**
	 * 解析群告警
	 * @param record
	 * @param recordRtring
	 */
	private static void parseGroupAlarmString(XlsRecord record,
			String recordRtring) {
		if (!"".equals(recordRtring)) {
			String[] alarmGParams = StringUtils.splitLikeJson(recordRtring,'[', ']');
			if (alarmGParams.length != 0 && !"".equals(alarmGParams[0])) {
				GroupAlarm groupA = null;
				String[] aStr = StringUtils.splitLikeJson(alarmGParams[0], '{','}');
				if(aStr.length==0){
					log.error("群解析不正确！----"+recordRtring);
				}
				for (String alarm : aStr) {
					groupA = GroupAlarms.getIntance().createGroupAlarm(alarm);
					if (groupA != null) {
						record.addGroupAlarms(groupA.getXlsNo());
					}
				}
			}
		}
	}

	/**
	 * 解析任务
	 * @param record
	 * @param recordRtring
	 */
	private static void parseTaskString(XlsRecord record, String recordRtring) {
		if (!"".equals(recordRtring)) {
			String[] taskParams = StringUtils.splitLikeJson(recordRtring,'[', ']');
			if (taskParams.length != 0 && !"".equals(taskParams[0])) {
				TaskInfo task = null;
				String[] aStr = StringUtils.splitLikeJson(taskParams[0], '{','}');
				for (String t : aStr) {
					task = TaskInfos.getIntacne().createTaskInfo(t);
					if (task != null) {
						record.addTask(task.getXlsNo());
					}
				}
			}
		}
	}

	/**
	 * 解析话单
	 * @param record
	 * @param recordRtring
	 */
	private static void parseBillString(XlsRecord record, String recordRtring) {
		if (!"".equals(recordRtring)) {
			String[] billParams = StringUtils.splitLikeJson(recordRtring,'[', ']');
			if (billParams.length != 0 && !"".equals(billParams[0])) {
				Bill bill = null;
				String[] aStr = StringUtils.splitLikeJson(billParams[0], '{','}');
				for (String t : aStr) {
					bill = Bills.getIntacne().createBill(t);
					if (bill != null) {
						record.addBill(bill);
					}
				}
			}
		}
	}

	public static String getCellString(HSSFCell cell) {
		String rtn = "";
		if (cell == null)
			return rtn;
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			rtn = NumFormat(cell.getNumericCellValue(), 0);
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			rtn = cell.getStringCellValue();
		}
		return rtn;
	}

	public static String NumFormat(double dbl, int numFig) {
		String rtn = "";
		try {
			String format = "###0";
			if (numFig > 0) {
				format = format.concat(".");
				for (int i = 0; i < numFig; i++) {
					format = format.concat("0");
				}
			}
			DecimalFormat f = new DecimalFormat(format);
			rtn = f.format(dbl);
		} catch (Exception e) {
			e.printStackTrace();
			rtn = "";
		}
		return rtn;
	}

	public static void main(String[] args) {
//		AlarmRead.readConfigFile("E:\\yu项目\\告警模拟程序\\alarm.xls");
//		String d="insert into GWCALLBILL (task_id,redo,prog_id,PCMNo,TimeSlot,CallerNo,CalleeNo,CallType,Start_Time,End_Time,duration,Talk_status,IAI_time,ACM_Time,Respond_Time,SS7_Content,code_data,remark) values(111754,0,871153002,1,1,'','',0,sysdate,sysdate,60,2,sysdate,674,192,'178.46.108.123','617','c825c825c825c825$d219d219d219d219')";
//		 String regex = "insert\\s+into\\s+(\\S+)\\s*\\((.+)\\)\\s+values\\s*\\((.+)\\)";
//	        Pattern p = Pattern.compile(regex);
//	        Matcher m = p.matcher(d);
//	        if (m.find()) {
//	            String table = m.group(1);
//	            String[] items = m.group(2).split(",");
//	        }
//		System.out.println(d.replaceAll("\\[task01\\]", "001"));
		Random random=new Random();
		for(int i=0;i<10;i++){
			int j= random.nextInt(0);
			if(j<0){
				System.out.println(j);
			}
		}
	}
}
