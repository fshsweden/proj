package com.ev112.codeblack.simpleclient.test;

import com.ev112.codeblack.simpleclient.alphasystem.TestAlphaSystem;

public class TestServlet {
	public static void main(String[] args) {
		new TestServlet();
	}
	
	TestAlphaSystem ta;
	
	public TestServlet() {
		
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				
				
//				ta.getPosition("SAND");
				
				try {
					Thread.sleep(5000);
				}
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			};
		
		};
		
		new Thread(r).start();
	}
}
