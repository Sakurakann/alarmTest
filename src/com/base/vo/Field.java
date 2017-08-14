package com.base.vo;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.utils.StringUtils;


public class Field implements Serializable{
	private static final long serialVersionUID = -1862204190787437454L;


	private static Logger log = Logger.getLogger(Field.class);
	
	
	private String name="";
	private String value="";
	private String type="S";
	private String lowName="";
	private int top=-1;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Field(String name, String value) {
		this.name = name;
		this.value = value;
		lowName=name.toLowerCase();
		parseType();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	private void parseType(){
		if(value.startsWith("FIX(")){
			type = "FIX";
		}else if(value.startsWith("D")){
			type = "D";
			String[] v = StringUtils.splitLikeJson(value, '(', ')');
			if(v.length>0){
				if(v[0].startsWith("<")){
					try{
						top = Integer.valueOf(v[0].substring(1));
					}catch(Exception e){
						top =100;
					}
				}
			}
		}else if(value.startsWith("F")){
			type = "F";
			String[] v = StringUtils.splitLikeJson(value, '(', ')');
			if(v.length>0){
				if(v[0].startsWith("<")){
					try{
						top =(int) Math.floor(Float.valueOf(v[0].substring(1)));
					}catch(Exception e){
						top =100;
					}
				}
			}
		}else if(value.startsWith("S")){
			type = "S";
			String[] v = StringUtils.splitLikeJson(value, '(', ')');
			if(v.length>0){
				if(v[0].startsWith("<")){
					try{
						top = Integer.valueOf(v[0].substring(1));
					}catch(Exception e){
						top =100;
					}
				}
			}
		}else if(value.startsWith("T(")){
			type = "T";
		}else if(value.startsWith("IP")){
			type = "IP";
		}else{
			try{
				Integer.valueOf(value);
				type = "D";
				value = "D("+value+")";
				log.warn("字段["+name+"]对应的值["+value+"]定义不正确，系统根据设置判断字段为数字类型");
			}catch(Exception e){
				try{
					Float.valueOf(value);
					type = "F";
					value = "F("+value+")";
					log.warn("字段["+name+"]对应的值["+value+"]定义不正确，系统根据设置判断字段为浮点数类型");
				}catch(Exception e2){
					type = "S";
					value = "S("+value+")";
					log.warn("字段["+name+"]对应的值["+value+"]定义不正确，系统根据设置判断字段为字符串类型");
				}
			}
		}
	}
	
	
	public static void main(String[] arg){
		System.out.println("<100".split(",").length);
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public String getLowName() {
		return lowName;
	}
	public void setLowName(String lowName) {
		this.lowName = lowName;
	}
	
}
