package com.base.vo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.utils.StringUtils;

public class BillFormats {
	private static Logger log = Logger.getLogger(BillFormats.class);
	private static BillFormats _intance= new BillFormats();
	private static Random random=new Random();
	private static DecimalFormat df = new DecimalFormat("0.######");
	private BillFormats(){}
	
	public static BillFormats getIntance(){
		return _intance;
	}
	//组<xlsNo, AlarmGroup>
	private Map<String,BillFormat> fromats =new HashMap<String,BillFormat>(20);
	
	public void addFormat(BillFormat format){
		if(fromats.containsKey(format.getXlsName())){
			log.warn("话单格式["+format.getXlsName()+"]被重复定义，系统覆盖原始版本");
		}
		fromats.put(format.getXlsName(), format);
	}
	
	public boolean containsKey(String key){
		return fromats.containsKey(key);
	} 
	
	public BillFormat getFromat(String key){
		return fromats.get(key);
	}
	
	
	public List<BillFormat> getFormatByTestCode(int testcode){
		List<BillFormat> list = new ArrayList<BillFormat>();
		String test = ","+testcode+",";
		for(BillFormat format : fromats.values()){
			for(String tests : format.getTestCodes().values()){
				if(tests.indexOf(test)>-1){
					list.add(format);
					break;
				}
			}
		}
		return list;
	}
	
	public int getBillCountByTaskId(int testcode,BillFormat format){
		int ret=1;
		String test = ","+testcode+",";
		for(int key : format.getTestCodes().keySet()){
			if(format.getTestCodes().get(key).indexOf(test)>-1){
				return key;
			}
		}
		log.error("奇怪，没找到对应测试类型,先按一条发着吧");
		return ret;
	}
	
	
	public BillFormat createForamt(String xlsName, String sqlI, String flagField,
			String rightFlag, String wrongFlag){
		BillFormat f = new BillFormat(xlsName,sqlI,flagField,rightFlag,wrongFlag); 
		addFormat(f);
		return f;
	}
	
	public boolean parseFormats(){
		boolean ret = true;
		for(BillFormat format : fromats.values()){
			ret = format.parseSqlToField();
			if(!ret){
				break;
			}
		}
		return ret;
	}
	/**
	 * @param record 
	 * @param format
	 * @param index
	 * @param progId
	 * @param billType true:正确话单，false：错误话单
	 * @return 话单字符串
	 */
	public String createInsertSql(BillRecord record,BillFormat format,int index,String progId,boolean billType){
		StringBuffer sb = new StringBuffer();
		TaskInfo task = TaskInfos.getIntacne().getTaskByXlsNo(record.getXlsNo());
		List<Field> flds = format.getFields();
		Map<String,String> fv = new HashMap<String,String>();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sf.format(new Date());
		Map<String,String> values = record.getBillExt();
		for(Field fld :flds){
			String type = fld.getType();
			String[] v = StringUtils.splitLikeJson(fld.getValue(), '(', ')');
			if(v.length==1){
				String vkey = StringUtils.trim(v[0]);
				if("taskid".equalsIgnoreCase(vkey)){
					fv.put(fld.getLowName(), task.getTaskid()+"");
					continue;
				}else if("progid".equalsIgnoreCase(vkey)){
					fv.put(fld.getLowName(), progId);
					continue;
				}else if("testcode".equalsIgnoreCase(vkey)){
					fv.put(fld.getLowName(), task.getTestCode()+"");
					continue;
				}else if(vkey.toLowerCase().startsWith("z_taskinfo")){
					String[] zmap =  vkey.split("\\.");
					if(zmap.length==2){
						String zv = StringUtils.trim(task.getExtValue(zmap[1].toLowerCase()));
						v = new String[]{zv};
					}
				}
			}
			if(values.containsKey(fld.getLowName())){
				fv.put(fld.getLowName(),values.get(fld.getLowName()) );
				continue;
			}
			if("D".equals(type)){
				if(v.length==0){
					fv.put(fld.getLowName(), getRandomInt()+"");
				}else{
					fv.put(fld.getLowName(), (fld.getTop()>-1?getRandomInt(fld.getTop()):v[0]) +"");
				}
			}else if("F".equals(type)){
				if(v.length==0){
					fv.put(fld.getLowName(), getRandomFloat()+"");
				}else{
					fv.put(fld.getLowName(), (fld.getTop()>-1?getRandomFloat(fld.getTop()):v[0]) +"");
					
				}
			}else if("IP".equals(type)){
				if(v.length==0){
					fv.put(fld.getLowName(), "'"+getRandomIP()+"'");
				}else{
					fv.put(fld.getLowName(), "'"+v[0]+"'");
				}
			}else if("S".equals(type)){
				if(v.length==0){
					fv.put(fld.getLowName(), "'"+getRandomString()+"'");
				}else{
					fv.put(fld.getLowName(), "'"+(fld.getTop()>-1?getRandomInt(fld.getTop()):v[0]) +""+"'");
				}
			}else if("FIX".equals(type)){
				String[] r =v[0].split("@@");
			
				fv.put(fld.getLowName(), r.length>index?r[index]:r[r.length-1]);
				
				
			}else if("T".equals(type)){
				if(v.length==0 || "sysdate".equalsIgnoreCase(v[0])){
					fv.put(fld.getLowName(), "to_date('"+date+"','yyyy-mm-dd hh24:mi:ss')");
				}else{
					fv.put(fld.getLowName(), "to_date('"+v[0]+"','yyyy-mm-dd hh24:mi:ss')");
				}
				
			}
		}

		if(!"".equals(format.getFlagField())){
			String key = format.getFlagField().toLowerCase();
			if(billType){
				String right =  format.getRightFlag();
				String[] rights = right.split(",");
				if("S".equals(format.getFlagFld().getType())){
					fv.put(key, "'"+(rights.length==1?rights[0]:rights[getRandomInt(rights.length)])+"'");
				}else{
					fv.put(key, rights.length==1?rights[0]:rights[getRandomInt(rights.length)]);
				}
			}else{
				String wrong = format.getWrongFlag();
				String[] wrongs = wrong.split(",");
				if("S".equals(format.getFlagFld().getType())){
					fv.put(key, "'"+(wrongs.length==1?wrongs[0]:wrongs[getRandomInt(wrongs.length)])+"'");
				}else{
					fv.put(key, wrongs.length==1?wrongs[0]:wrongs[getRandomInt(wrongs.length)]);
				}
			}
		}

		sb.append("insert into ").append(format.getTable()).append(" (");
		StringBuffer vsb = new StringBuffer(" values(");
		for(int i=0,j=format.getFields().size();i<j;i++){
			String key = format.getFields().get(i).getLowName();
			sb.append(key).append(",");
			vsb.append(fv.get(key)).append(",");
		}
		sb = new StringBuffer(sb.substring(0, sb.length()-1)).append(")").append(vsb.substring(0, vsb.length()-1)).append(")");
		return sb.toString();
	}
	
	
	private int getRandomInt(int n){
		int i=n;
		if(i>1){
			i=random.nextInt(n);
		}
		return i;
	}
	
	private int getRandomInt(){
		return getRandomInt(1000);
	}
	
	private String getRandomString(){
		return getRandomString(getRandomInt(20));
	}
	private String getRandomString(int n){
		return StringUtils.randomString(n);
	}
	private float getRandomFloat(){
		return getRandomFloat(getRandomInt(20));
	}
	
	private float getRandomFloat(int n){
		int i = getRandomInt(n);
		float f = random.nextFloat();
		return Float.valueOf(df.format(i+f));
	}
	
	private String getRandomIP(){
		int[] ips = new int[4];
		for(int i=0;i<4;i++){
			int p=-1;
			do{
				p = getRandomInt(255);
				ips[i]=p;
			}while(p==0);
		}
		return ips[0]+"."+ips[1]+"."+ips[2]+"."+ips[3];
	}
}
