package com.ev112.codeblack.simpleclient.test;

import com.ev112.codeblack.common.generated.messages.StatusEvent;
import com.ev112.codeblack.common.tools.SendEmail;
import com.ev112.codeblack.simpleclient.alphasystem.AlphaSystem;
import com.ev112.codeblack.simpleclient.alphasystem.AlphaSystemModule;
import com.ev112.codeblack.simpleclient.alphasystem.AlphaSystemStatus;
import com.ev112.codeblack.simpleclient.alphasystem.IAlphaSystemConnectionStatus;
import com.ev112.codeblack.simpleclient.alphasystem.IAlphaSystemEventHandler;

public class TestErrorAlarm implements IAlphaSystemConnectionStatus {
	private AlphaSystem alpha_system;

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java -jar <jarname> <hostname> <portnumber>");
			System.exit(1);
		}
		new TestErrorAlarm(args[0], args[1]);
	}
	
	public TestErrorAlarm(String hostname, String port) {
		System.out.println("TestAlphaSystem version 1.00");
		System.out.println("Connecting to Alpha Configuration Server at " + hostname + ":" + port);

		alpha_system = new AlphaSystem("TEST");
		alpha_system.connect(this);
	}

	@Override
	public void alphaConnectionStatus(AlphaSystemModule module, AlphaSystemStatus status) {
		if (module == AlphaSystemModule.System && status == AlphaSystemStatus.Connected) {
			
			alpha_system.subscribeSystemEvents(new IAlphaSystemEventHandler() {
				@Override
				public void handleEvent(StatusEvent pEvent) {
					switch (pEvent.getSeverity()) {
						case 2:
							System.out.println("ERROR! " + pEvent.getMessage());
							SendEmail.mailTo("peter.andersson@fsh.se", "fshsweden@gmail.com", "ALARM!", pEvent.getMessage());
						break;
						
						default:
							System.out.println("mumble, mumble...");
							SendEmail.mailTo("peter.andersson@fsh.se", "fshsweden@gmail.com", "Just some information", pEvent.getMessage());
						break;
					}
				}
			});
		}
	}

}
