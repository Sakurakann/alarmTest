package com.sakura;

public class StringSplit {
	public static void main(String[] args) {
		//将to_date和后面template拼接起来
		String[] strings = {"to_data('2017-08-08 10:33:01'", "'yyyy-mm-dd hh24:mi:ss')", "", "1", "to_data('2017-08-08 10:33:01'", "'yyyy-mm-dd hh24:mi:ss')"};
		System.out.println(strings.length);
		int count = 0;
		for ( int i = 0;i < strings.length;i++){
			if (strings[i].startsWith("'yyyy-mm-dd")){
				strings[i-1] = strings[i-1]+","+strings[i];
				strings[i] = null;
				count++;
			}
		}
		System.out.println(count);
		String newString[] = new String[strings.length - count];
		int j = 0;
		for ( String s : strings ) {
			if (s != null){
				newString[j++] = s;
			}
		}
		System.out.println(newString.length);
		for ( String string : newString ) {
			System.out.println(string);
		}
	}
}
