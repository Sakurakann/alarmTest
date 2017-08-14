package com.base.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XlsRecords extends ITableVo<XlsRecord> {
	private static XlsRecords _intance = new XlsRecords();

	private XlsRecords() {
	}

	public static XlsRecords getIntance() {
		return _intance;
	}

	private Map<Integer, XlsRecord> records = new HashMap<Integer, XlsRecord>();

	@Override
	protected void addVo(XlsRecord t) {
		records.put(t.getRecordLineNo(), t);
	}

	public void addXlsRecord(XlsRecord record) {
		addVo(record);
	}
	
	public Collection<XlsRecord> getAllXlsRecord(){
		return records.values();
	}

	@Override
	public String[] clearTable() {
		return null;
	}

	@Override
	protected Collection<XlsRecord> getSaveObject() {
		List<XlsRecord> list = new ArrayList<XlsRecord>();
		for (XlsRecord r : records.values()) {
			if (r.isExecFlag()) {
				list.add(r);
			}
		}
		return list;
	}

}
