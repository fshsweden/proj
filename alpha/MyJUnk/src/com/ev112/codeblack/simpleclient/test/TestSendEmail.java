package com.ev112.codeblack.simpleclient.test;

import com.ev112.codeblack.common.tools.SendEmail;

public class TestSendEmail {
	public TestSendEmail() {
		SendEmail.mailTo("fshsweden@hotmail.com", "fshsweden@gmail.com", "TEST EMAIL", "This is a simple test!");
	}
	
	public static void main(String[] args) {
		new TestSendEmail();
	}
}
