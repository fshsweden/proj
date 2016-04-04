package com.ev112.codeblack.simpleclient.test;

import java.util.Locale;

public class testnum {

	public testnum() {
		
		Locale l = Locale.getDefault();
		System.out.println(l.toString());
		Double d = 200.34;
		System.out.println("Double = " + d);
	}
	
	public static void main(String[] args) {
		new testnum();
	}
}
