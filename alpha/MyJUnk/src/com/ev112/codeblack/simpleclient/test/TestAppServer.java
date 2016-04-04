package com.ev112.codeblack.simpleclient.test;

import com.ev112.codeblack.common.tcpip.TcpIpConnection;
import com.ev112.codeblack.simpleclient.appserver.AppServer;
import com.ev112.codeblack.simpleclient.appserver.AppServerInterface;

public class TestAppServer {

	
	AppServer server;
	
	public TestAppServer() {
		server = new AppServer("Test", 7878, new AppServerInterface() {
			
			@Override
			public void setVariable(TcpIpConnection conn, String name, String value) {
			}
			
			@Override
			public void linkError(String errMsg) {
			}
			
			@Override
			public String getVariable(TcpIpConnection conn, String name) {
				return "";
			}
		});
	}
	
	
	public static void main(String[] args) {
		new TestAppServer();
	}

}
