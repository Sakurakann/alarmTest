package com.sakura;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Demo {
	public static void main(String[] args) {
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		System.out.println(format1.format(date));
		System.out.println(format2.format(date));
		System.out.println(16>>4);
	}
}
