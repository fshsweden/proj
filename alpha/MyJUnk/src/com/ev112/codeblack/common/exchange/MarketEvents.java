package com.ev112.codeblack.common.exchange;

public interface MarketEvents {
	public void marketConnected();
	public void marketDisconnected();
	public void marketMessage(String severity, String message);
}
