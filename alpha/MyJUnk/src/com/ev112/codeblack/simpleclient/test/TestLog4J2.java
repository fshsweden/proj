package com.ev112.codeblack.simpleclient.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestLog4J2 {
	Logger logger = LogManager.getLogger();
	
	public TestLog4J2() {
		
		// remember: Add folder where log4j2.xml to classpath!
		
		System.out.println("pwd=" + System.getProperty("user.dir"));
		// System.out.println("CP=" + System.getProperty("java.class.path"));
		
		for (int i=0; i<10000; i++) {
			logger.error("An error!");
			logger.info("An info!");
		}
	}
	
	public static void main(String[] args) {
		new TestLog4J2();
	}
}
