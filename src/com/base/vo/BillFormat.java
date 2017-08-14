package com.base.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.utils.StringUtils;

public class BillFormat implements Serializable{
	private static final long serialVersionUID = 5327410918980367623L;

	private static Logger log = Logger.getLogger(BillFormat.class);
	
	private static int index=1;
	
	private int lineNo = index++;
	
	private String xlsName ="";//话单格式编号
	
	private String sqlI = "" ;//插入语句
	
	private String flagField="";//话单的标记位；判断话单正确与失败
	
	private Field flagFld=null;
	
	private String rightFlag="";
	
	private String wrongFlag="";
	
	private List<Field> fields =null;
	
	private Map<Integer,String> testCodes = new HashMap<Integer,String>();
	
	private String table="";
	
	public String getTable() {
		return table;
	}


	/**
	 *解析sql，简单判断语句格式是否正确 
	 */
	public boolean parseSqlToField(){
		boolean ret = true;
		String[] groups = StringUtils.splitLikeJson(sqlI, '(', ')');
		ret = groups.length==2;
		if(ret){
			int from = sqlI.indexOf("(");
			String heard = sqlI.substring(0, from).toLowerCase();
			from  = heard.indexOf("into");
			ret = from>0;
			if(ret){
				table = StringUtils.trim(heard.substring(from+4));
			}
			
			String[] items = groups[0].split(",");
			String[] values = groups[1].split(",");
			ret = items.length == values.length;
			if(ret){
				for(int i=0,j=items.length;i<j;i++){
					add(new Field(items[i],values[i]));
				}
			}else{
				log.error("话单格式设置第["+lineNo+"]行的表SQL定义的字段与值数量不对称!");
			}
		}
		if(!ret){
			log.error("话单格式设置第["+lineNo+"]行的表SQL定义的不正确，请检查!");
		}
		
		return ret;
	}
	
	
	public BillFormat(String xlsName, String sqlI, String flagField,
			String rightFlag, String wrongFlag) {
		this.xlsName = xlsName;
		this.sqlI = sqlI;
		this.flagField = flagField;
		this.rightFlag = rightFlag;
		this.wrongFlag = wrongFlag;
	}

	public void addBillNum(int num,String codes){
		testCodes.put(num, ","+codes+",");
	}

	public String getXlsName() {
		return xlsName;
	}

	public void setXlsName(String xlsName) {
		this.xlsName = xlsName;
	}

	public String getSqlI() {
		return sqlI;
	}

	public void setSqlI(String sqlI) {
		this.sqlI = sqlI;
	}

	public String getRightFlag() {
		return rightFlag;
	}

	public void setRightFlag(String rightFlag) {
		this.rightFlag = rightFlag;
	}

	public String getWrongFlag() {
		return wrongFlag;
	}

	public void setWrongFlag(String wrongFlag) {
		this.wrongFlag = wrongFlag;
	}

	public List<Field> getFields() {
		return fields;
	}

	private void add(Field field) {
		if(fields==null){
			fields = new ArrayList<Field>();
		}
		if(flagField.equalsIgnoreCase(field.getName())){
			flagFld = field;
		}
		fields.add(field);
	}


	public Field getFlagFld() {
		return flagFld;
	}


	public String getFlagField() {
		return flagField;
	}


	public Map<Integer, String> getTestCodes() {
		return testCodes;
	}
	
	

}
