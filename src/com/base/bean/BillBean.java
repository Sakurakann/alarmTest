package com.base.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.base.GlobalConfig;
import com.base.vo.Bill;
import com.base.vo.BillFormat;
import com.base.vo.BillFormats;
import com.base.vo.BillRecord;
import com.base.vo.TaskInfos;
import com.base.vo.XlsRecord;
import com.bill.BillClinet;
import com.utils.StringUtils;

import edu.emory.mathcs.backport.java.util.concurrent.Callable;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.Future;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class BillBean extends AbstractDaoBean {
	private static Logger log = Logger.getLogger(BillBean.class);

	private GlobalConfig config = GlobalConfig.getIntance();
	private final long TASK_SLEEP_TIME = config.getTaskWaitTime();
	private final long BILL_CYCLE_TIME = config.getBillSendCycle();
	private List<XlsRecord> xlsRecordList = null;
	private BillClinet billClient = null;
	private ExecutorService eService = Executors.newFixedThreadPool(config.getPoolSize()); // bill生成线程池
	private ExecutorService brService = Executors.newFixedThreadPool(config.getPoolSize() * 4); // com.bill Record 生成线程池
	private ScheduledExecutorService rollService = Executors.newSingleThreadScheduledExecutor();// bill遍历线程
	private ExecutorService rollMessageService = Executors.newFixedThreadPool(1);// 结果打印表
	private BillFormats billformats = BillFormats.getIntance();
	private TaskInfos tasks = TaskInfos.getIntacne();

	public BillBean(List<XlsRecord> list) {
		xlsRecordList = list;
	}

	private void startBill() {
		String ip = StringUtils.trim(config.getProperty(GlobalConfig.BILL_IP));
		if ("".equals(ip)) {
			log.warn("配置文件没有定义BILL的连接地址,默认使用[127.0.0.1]");
			ip = GlobalConfig.DEFAULT_BILL_IP;
		}
		String port = StringUtils.trim(config
				.getProperty(GlobalConfig.BILL_PORT));
		int iport = -1;
		if ("".equals(port)) {
			log.warn("配置文件没有定义BILL的连接端口,默认使用[" + GlobalConfig.DEFAULT_BILL_PORT
					+ "]");
			iport = GlobalConfig.DEFAULT_BILL_PORT;
		} else {
			iport = Integer.valueOf(port);
		}
		billClient = new BillClinet(ip, iport, "Bill");
	}

	public void stratBillServer() {
		startBill();
		rollService.scheduleWithFixedDelay(new RollTask(), 0, 60,
				TimeUnit.SECONDS);
	}

	/**
	 * 任务轮询类
	 * @author Administrator
	 * 
	 */
	private class RollTask implements Runnable {
		public void run() {
			while (true) {
				try {
					while (!billClient.getStatus()) {
						log.error("com.bill 服务端未连接上");
						Thread.sleep(1 * 60 * 1000);
					}
					for (XlsRecord record : xlsRecordList) {
						if (record.isFree() && System.currentTimeMillis()- record.getLastExeTime() > record.getCycleTime()) {
							record.setFree(false);
							record.setLastExeTime(System.currentTimeMillis());
							eService.submit(new Task(record));
						}
					}
				} catch (Exception e) {
					log.error("轮询话单进程异常", e);
				}
			}
		}
	}

	/**
	 * 打印一次测试结果
	 *
	 */
	private class RollMessageTask implements Runnable {
		private XlsRecord record;

		public RollMessageTask(XlsRecord record) {
			this.record = record;
		}

		public void run() {
			try {
				List<Bill> bills = record.getBills();
				log.info("************测试任务[" + record.getTestName()+ "]测试报告*****************");
				for (Bill bill : bills) {
					log.info("第" + bill.getOrder() + "组话单的参与任务为：");
					List<BillRecord> records = bill.getBills();
					for (BillRecord r : records) {
						log.info("任务[" + r.getXlsNo() + "],数据库ID["+ r.getTaskid() + "],发送话单明细["+ r.getSendFlags() + "],告警方式：["+ r.getAlarmType() + "]");
					}
					log.info("最终告警结果：" + bill.getResultMessage());
				}
				log.info("************测试任务[" + record.getTestName()+ "]*****************");
				log.info("");
				log.info("");
				record.setFree(true);

			} catch (Exception e) {

			} finally {
				record = null;
			}
		}

	}

	private class TaskRcorder implements Callable {

		private BillRecord record = null;

		public TaskRcorder(BillRecord r) {
			record = r;
		}

		public Integer call() throws Exception {
			try {
				log.debug("开始发送任务编号[" + record.getXlsNo() + "]的话单。话单顺序["+ record.getSendFlags() + "]");
				String sendFlag = record.getSendFlags();
				List<BillFormat> formats = getFormatByTaskId(record.getXlsNo());
				for (int i = 0, j = sendFlag.length(); i < j; i++) {
					char flag = sendFlag.charAt(i);
					int num = 1; // 发送次数
					// 存在 1(10)配置，表示1发10次
					if (i + 1 < j) {
						if ('(' == sendFlag.charAt(i + 1)) {
							StringBuffer sb = new StringBuffer();
							i = i + 2;
							while (Character.isDigit(sendFlag.charAt(i))) {
								sb.append(sendFlag.charAt(i));
								i++;
							}
							try {
								num = Integer.valueOf(sb.toString());
							} catch (Exception e) {
								log.error("话单发送顺序格式设置不正确[" + sendFlag+ "],系统按照1次发送", e);
							}
						}
					}
					for (int k = 0; k < num; k++) {
						String progId = generate_Programid() + "";
						for (BillFormat format : formats) {
							int bc = getBillCountByTaskId(record.getXlsNo(),format);
							for (int f = 0; f < bc; f++) {
								String bs = billformats.createInsertSql(record,format, f, progId, '1' == flag);
								int cc=0;
								int tt=10;

								// TODO 将sendSyc中的参数改为tlv格式的sql
								while (billClient.getStatus()&& !billClient.sendSyc(bs) && cc++ <tt);
								if(cc>=tt){
									log.error("任务["+record.getXlsNo()+"]发送["+format.getXlsName()+"]话单不成功！"+bs);
								}
							}
						}
						try {
							Thread.sleep(BILL_CYCLE_TIME);
						} catch (Exception e) {
							log.error("话单线程不能正常休息，可能会导致发给告警程序的话单顺序不正确!Progid["+ progId + "]", e);
						}
					}
				}
			} catch (Exception e) {

			}finally{
				record =null;
			}

			return 1;
		}

	}

	private class Task implements Runnable {
		private XlsRecord record = null;

		// private List<String> message = new ArrayList<String>();

		public Task(XlsRecord record) {
			this.record = record;
		}

		public void run() {
			try {
				log.debug("开始测试[" + record.getTestName() + "]");
				List<Bill> bills = record.getBills();
				int count = 1;
				for (Bill bill : bills) {
					log.debug("开始测试[" + record.getTestName() + "]");
					List<BillRecord> records = bill.getBills();
					List<Future> fList = new ArrayList<Future>();
					for (BillRecord record : records) {
						fList.add(brService.submit(new TaskRcorder(record)));
					}
					for(Future f : fList){
						//校验?
						f.get();
					}
					log.debug("所有任务发送完");
					if (!"".equals(bill.getSql())) {
						try {
							Thread.sleep(TASK_SLEEP_TIME);
						} catch (Exception e) {
							log.error("等待告警程序出结果，执行睡眠操作是异常", e);
						}
						checkAlarmResult(bill);
					}
				}
				rollMessageService.submit(new RollMessageTask(record));
			} catch (Exception e) {
				log.error("第[" + record.getRecordLineNo() + "]行任务，发送话单报错", e);
			} finally {
				record = null;
			}
		}
	}

	/**
	 * 打印一次测试结果
	 * 
	 * @param record
	 */
	// private void printlnMessage(XlsRecord record) {
	// List<Bill> bills = record.getBills();
	// for(Bill com.bill : bills){
	// log.info("************测试任务["+record.getTestName()+"]*****************");
	// log.info("第"+com.bill.getOrder()+"组话单的参与任务为：");
	// List<BillRecord> records = com.bill.getBills();
	// for(BillRecord r : records){
	// log.info("任务["+r.getXlsNo()+"],数据库ID["+r.getTaskid()+"],发送话单明细["+r.getSendFlags()+"],告警方式：["+r.getAlarmType()+"]");
	// }
	// log.info("最终告警结果："+com.bill.getResultMessage());
	// log.info("************测试任务["+record.getTestName()+"]*****************");
	// }
	// record.setFree(true);
	// }
	/**
	 * 检查告警程序执行情况
	 * 
	 * @param bill
	 */
	private void checkAlarmResult(Bill bill) {
		try {
			List<String[]> ret = this.query(bill.getSql());
			if (ret != null && ret.size() > 0) {
				String[] r = ret.get(0);
				if (r != null && r.length > 0) {
					try {
						int i = Integer.valueOf(r[0]);
						if (i > 0) {
							bill.setResultMessage(bill.getMessageOk());
						} else {
							bill.setResultMessage(bill.getMessageErr());
						}
					} catch (Exception e) {
						bill.setResultMessage("验证执行不成功，请查找相关日志信息");
						log.error("执行告警验证结果不能转换成数字[" + bill.getSql()+ "]..", e);
					}
				}
			}
		} catch (Exception e) {
			bill.setResultMessage("验证执行不成功，请查找相关日志信息");
			log.error("执行告警验证sql是报错[" + bill.getSql() + "]..", e);
		}
	}

	/**
	 * 根据任务xlsNo 获取List<BillFormat>
	 * 
	 * @param xlsNo
	 * @return
	 */
	private List<BillFormat> getFormatByTaskId(String xlsNo) {
		int testcode = tasks.getTaskByXlsNo(xlsNo).getTestCode();
		return billformats.getFormatByTestCode(testcode);
	}

	private int getBillCountByTaskId(String xlsNo, BillFormat format) {
		int testcode = tasks.getTaskByXlsNo(xlsNo).getTestCode();
		return billformats.getBillCountByTaskId(testcode, format);
	}

	private static long Mill = 0;

	public synchronized static long generate_Programid() {
		long lRet = 0;
		lRet = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) * 8 * 60 * 60
				+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 3600
				+ Calendar.getInstance().get(Calendar.MINUTE) * 60 + Calendar
				.getInstance().get(Calendar.SECOND))
				* 1000 + (Mill++) % 1000;
		if (Mill >= 100000)
			Mill = 0;

		return lRet;
	}

}
