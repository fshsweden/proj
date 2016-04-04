package com.ev112.codeblack.simpleclient.test;

import java.sql.Timestamp;
import java.util.Date;

import com.ev112.codeblack.common.utilities.DateTools;

public class TestDateTools {

	public TestDateTools() {
		Long ts = new Date().getTime();
		System.out.println(new Timestamp(ts));
		
		Timestamp ts2 = new Timestamp(System.currentTimeMillis());
		System.out.println(ts2);
		
		System.out.println(DateTools.getDateAsStr(ts2));
		System.out.println(DateTools.getDateTimeAsStr(new Date()));
		System.out.println(DateTools.getDateTimeAsStr(ts2));
	}
	
	
	public static void main(String[] args) {
		new TestDateTools();
	}
}
