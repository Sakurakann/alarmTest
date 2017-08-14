package com.base.vo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

import org.apache.log4j.Logger;

public abstract class ITableVo<T> {
	private static Logger log = Logger.getLogger(ITableVo.class);
	//TODO no clean data
	private boolean INIT_CLEAR = false;
//	private boolean INIT_CLEAR = true; // 是否清楚历史数据

	/**
	 * 获取清楚表数据标记
	 * 
	 * @return 默认返回 true
	 */
	public boolean getInitFlag() {
		return INIT_CLEAR;
	}

	public abstract String[] clearTable();// 返回清楚表语句

	protected abstract void addVo(T t);
	
	protected abstract Collection<T> getSaveObject();

	public boolean writeToFile(String file) {
		boolean ret = true;
		Collection<T> cols = getSaveObject();
		try {
			File f = new File(file);
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream fout = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeInt(cols.size());
			for (T col : cols) {
				out.writeObject(col);
			}
			out.close();
		} catch (Exception e) {
			ret = false;
			log.error("对象保存文件是错...", e);
		}
		return ret;
	}

	public boolean readFromFile(String file) {
		boolean ret = true;
		try {
			File f = new File(file);
			if (f.exists()) {
				FileInputStream fin = new FileInputStream(file);
				ObjectInputStream in = new ObjectInputStream(fin);
				int size = in.readInt();
				for(int i=0;i<size;i++){
					T obj = (T)in.readObject();
					if(obj !=null){
						addVo(obj);
					}
				}
			}else{
				ret = false;
				log.error("对象["+file+"]文件不存在....");
			}
		} catch (Exception e) {
			ret = false;
			log.error("对象保存文件是错...", e);
		}
		return ret;
	}
}
