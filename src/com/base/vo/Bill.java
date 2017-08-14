package com.base.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.utils.StringUtils;

public class Bill implements Comparable<Bill>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4578540996043408523L;

	private static Logger log = Logger.getLogger(Bill.class);
	
	private static long no=0;
	
	private static synchronized long getSynBillNo(){
		return no++;
	}
	
	private long billNo=-1L;
	
	private int order=-1;
	
	private List<BillRecord> bills=null;
	
	private String sql="";
	
	private String messageOk="";
	
	private String messageErr="";
	
	private boolean execFlag=false; //是否可执行 
	
	private String resultMessage="";
	
	public long getBillNo(){
		return billNo;
	}
	

	public Bill(String order, String billstr, String sql,
			String messageOk, String messageErr) {
		billNo = Bill.getSynBillNo();
		this.order = Integer.valueOf(order);
		setBills(billstr);
		this.sql = sql;
		this.messageOk = messageOk;
		this.messageErr = messageErr;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public List<BillRecord> getBills() {
		return bills;
	}
	
	public boolean checkExec(){
		TaskInfos tasks = TaskInfos.getIntacne();
		if(bills==null){
			return false;
		}
		TaskInfo task;
		boolean ret =true;
		for(BillRecord bill : bills){
			task = tasks.getTaskByXlsNo(bill.getXlsNo());
			if(task==null || !task.isExecTask()){
				log.error("话单定义中，任务编号["+bill.getXlsNo()+"]未正确定义.");
				return false;
			}
			bill.setTaskid(task.getTaskid());
			//0:不告警; 1:测试方式告警; 2:按任务告警;
			bill.setAlarmType("1".equals(task.getAlarmType())?"测试方式告警":"2".equals(task.getAlarmType())?"按任务告警":"不告警");
			if(!bill.parseExt()){
				ret=false;
				break;
			}
			sql = sql.replaceAll("\\<"+bill.getXlsNo()+"\\>", task.getTaskid()+"");
		}
		String[] ids = StringUtils.splitLikeJson(sql, '<', '>');
		GroupAlarms ga = GroupAlarms.getIntance();
		AlarmGroups ag = AlarmGroups.getIntance();
		AlarmPoints ps = AlarmPoints.getIntance();
		for(String id : ids){
			if(ga.containKey(id)){
				sql = sql.replaceAll("\\<"+id+"\\>", ga.getGroupAlarmByID(id).getId()+"");
			}else if(ag.containKey(id)){
				sql = sql.replaceAll("\\<"+id+"\\>", ag.getGroup(id).getGroupId()+"");
			}else if(ps.containKey(id)){
				sql = sql.replaceAll("\\<"+id+"\\>", ps.getPointById(id).getAlarmPointNO()+"");
			}else{
				ret=false;
				log.error("检验查询语句中的["+id+"]不能被替换，请确认是否填写正确");
			}
		}
		execFlag=ret;
		return ret;
	}

	private void addBillRecord(BillRecord record){
		if(bills==null){
			bills = new ArrayList<BillRecord>();
		}
		bills.add(record);
	}
	public void setBills(String bills) {
		String[] billSub = StringUtils.splitLikeJson(bills, '{', '}');
		BillRecord record =null;
		for(String bill : billSub){
			String[] r = bill.split(",");
			if(r.length==3){
				int i=0;
				String[] exts = StringUtils.splitLikeJson(r[2], '[', ']');
				record = new BillRecord(r[i++],r[i++],exts.length>0?exts[0]:"");
				addBillRecord(record);
			}else{
				log.warn("话单发送格式定义不正确["+r+"]");
			}
		}
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getMessageOk() {
		return messageOk;
	}

	public void setMessageOk(String messageOk) {
		this.messageOk = messageOk;
	}

	public String getMessageErr() {
		return messageErr;
	}

	public void setMessageErr(String messageErr) {
		this.messageErr = messageErr;
	}
	
	public int compareTo(Bill o) {
		return this.order>o.getOrder()?1:this.order==o.getOrder()?0:-1;
	}


	public String getResultMessage() {
		return resultMessage;
	}


	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}


	public boolean isExecFlag() {
		return execFlag;
	}


	public void setExecFlag(boolean execFlag) {
		this.execFlag = execFlag;
	}
	
}
