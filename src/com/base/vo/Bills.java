package com.base.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.utils.StringUtils;

public class Bills extends ITableVo<Bill> {
	private static Logger log = Logger.getLogger(Bills.class);
	private static Bills _intance= new Bills();
	
	private Map<Long,Bill> billList = new HashMap<Long,Bill>();
	
	private Bills(){
		
	}
	public static Bills getIntacne(){
		return _intance;
	}
	
	
	
	
	@Override
	protected void addVo(Bill t) {
		// TODO Auto-generated method stub
		billList.put(t.getBillNo(), t);
	}
	
	
	@Override
	protected Collection<Bill> getSaveObject() {
		List<Bill> list = new ArrayList<Bill>();
		for(Bill bill : billList.values()){
			if(bill.isExecFlag()){
				list.add(bill);
			}
		}
		return list;
	}
	public Bill createBill(String billString){
		Bill bill =null;
		String[] rSub = StringUtils.splitLikeJson(billString, '[', ']');
		if(rSub.length==1&& !"".equals(StringUtils.trim(rSub[0]))){
			billString = billString.replace(",["+rSub[0]+"]", "");
//			String[] params = billString.split(",");
			String[] params = StringUtils.split2(billString, ",");
			if(params.length==4){
				int i=0;
				bill = new Bill(params[i++],rSub[0],params[i++],params[i++],params[i++]);
//				billList.put(com.bill.getBillNo(), com.bill);
				addVo(bill);
			}else{
				log.warn("bill话单格式定义不正确["+billString+"]");
			}
		}else{
			log.warn("bill话单格式定义不正确["+billString+"]");
		}
		return bill;
	}
	
	public static boolean checkListExec(List<Bill> list){
		boolean b=true;
		for(Bill bill : list){
			if(!bill.checkExec()){
				b=false;
				break;
			}
		}
		return b;
	}
	/**
	 * 打印bill相关任务情况
	 * @param bill
	 */
	public static void printBillInfo(Bill bill){
		
	}
	
	@Override
	public String[] clearTable() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args){
		String str = "1,[{g001_t001,101010(10)11,[]},{g001_t002,101010(10)11,[]},{g001_t003,101010(10)11,[]},{g001_t004,101010(10)11,[]},{g001_t005,101010(10)11,[]}],select * from dual,正确,失败";
		Bills bs = new Bills();
		bs.createBill(str);
	}

}
