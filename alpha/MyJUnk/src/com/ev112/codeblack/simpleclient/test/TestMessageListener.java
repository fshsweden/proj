package com.ev112.codeblack.simpleclient.test;

import com.ev112.codeblack.simpleclient.alphasystem.AlphaSystem;
import com.ev112.codeblack.simpleclient.alphasystem.AlphaSystemModule;
import com.ev112.codeblack.simpleclient.alphasystem.AlphaSystemStatus;
import com.ev112.codeblack.simpleclient.alphasystem.IAlphaSystemConnectionStatus;

public class TestMessageListener implements IAlphaSystemConnectionStatus {
	AlphaSystem alpha;
	
	public TestMessageListener() {
		alpha = new AlphaSystem("TEST");
		alpha.connect(this);
		
	}

	@Override
	public void alphaConnectionStatus(AlphaSystemModule module, AlphaSystemStatus status) {
		
		
	}

	public static void main(String[] args) {
		new TestMessageListener();
	}
}
