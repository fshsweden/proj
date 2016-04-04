package com.ev112.codeblack.simpleclient.ibexamples;

import mytrade.ib.IBMarketAPI;

public class TestTrader {
	
	private IBMarketAPI market;
	private Trader trader;
	
	public TestTrader() {
		
		market = new IBMarketAPI();

		System.out.println("TESTTRADER.connected()");
		trader = new Trader(market); // pass on a connected market....				
		
		MarketAPI.KeyValues kv = new MarketAPI.KeyValues();
		
		kv.put("HOST", "192.168.0.22");
		kv.put("PORT", "6661");
		kv.put("CLIENT_ID", "1");
		kv.put("ACCOUNT", "");
		
		market.configure(kv);
		
		trader.start();
	}
	
	public static void main(String[] args) {
		new TestTrader();
	}
}
