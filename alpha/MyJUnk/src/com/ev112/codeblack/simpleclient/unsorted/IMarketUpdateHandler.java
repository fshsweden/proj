package com.ev112.codeblack.simpleclient.unsorted;


public interface IMarketUpdateHandler {
	public void marketPriceUpdated(String cause, MarketPrice mkt);
}
