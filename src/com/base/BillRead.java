package com.base;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.base.vo.BillFormats;
import com.base.vo.XlsRecord;
import com.utils.StringUtils;

public class BillRead {
	private static Logger log = Logger.getLogger(BillRead.class);
	
	public static void readConfigFile(String file){
		BillFormats format = BillFormats.getIntance();
		try{
			InputStream in = new FileInputStream(file);
			POIFSFileSystem fs = new POIFSFileSystem(in);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			log.debug("开始读取第一个sheet页，内容应该为：话单格式");
			int capacity = sheet.getLastRowNum();
			int colNum = sheet.getRow(0).getLastCellNum();
			if (colNum != 5) {
				log.error("话单格式定义文件格式不正确，文件应定义为5列，分别为：[话单格式编号、表sql、标记字段、成功值、失败值]");
			}
			XlsRecord record = null;
			HSSFRow row = null;
			for (int i = 1; i <= capacity; i++) {
				row = sheet.getRow(i);
				String billName = StringUtils.trim(getCellString(row.getCell((short) 0)));
				String sql = StringUtils.trim(getCellString(row.getCell((short) 1)));
				String fld = StringUtils.trim(getCellString(row.getCell((short) 2)));
				String rflag = StringUtils.trim(getCellString(row.getCell((short) 3)));
				String wflag = StringUtils.trim(getCellString(row.getCell((short) 4)));
				format.createForamt(billName, sql, fld, rflag, wflag);
			}
			log.debug("开始读取第二个sheet页，内容应该为：测试类型对应话单");
			 sheet = wb.getSheetAt(1);
			capacity = sheet.getLastRowNum();
			colNum = sheet.getRow(0).getLastCellNum();
			if (colNum != 3) {
				log.error("测试类型对应话单定义文件格式不正确，文件应定义为3列，分别为：[话单格式编号、话单数量、测试类型]");
			}
			for (int i = 1; i <= capacity; i++) {
				row = sheet.getRow(i);
				String billName = StringUtils.trim(getCellString(row.getCell((short) 0)));
				String num = StringUtils.trim(getCellString(row.getCell((short) 1)));
				String codes = StringUtils.trim(getCellString(row.getCell((short) 2)));
				if(format.containsKey(billName)){
					format.getFromat(billName).addBillNum(Integer.valueOf(num), codes);
				}else{
					log.error("在定义【测试类型对应话单】时，第["+i+1+"]行，定义的名称在话单格式时没定义过，再后继业务中将不会发生对应话单。影响的测试类型["+codes+"]");
				}
			}
			in.close();
		}catch(Exception e){
			log.error("读取话单配置文件报错...",e);
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
}
