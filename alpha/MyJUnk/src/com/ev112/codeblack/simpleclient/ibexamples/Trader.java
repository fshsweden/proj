package com.ev112.codeblack.simpleclient.ibexamples;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.ev112.codeblack.pricecollector.ib.IBTickType;
import com.ev112.codeblack.pricecollector.ib.IInstrument;
import com.ev112.codeblack.pricecollector.ib.IInstrumentObserver;
import com.ev112.codeblack.pricecollector.ib.MarketStatusHandler;
import com.ev112.codeblack.simpleclient.ibexamples.MarketAPI.ConnectionEventHandler;
import com.ev112.codeblack.simpleclient.ibexamples.MarketAPI.InformationType;
import com.ev112.codeblack.simpleclient.ibexamples.MarketAPI.KeyValues;
import com.ib.controller.Types.SecType;

import mytrade.ib.IBMarketAPI;

/*
 * Trader known ONLY about marketAPI, nothing about IB here!
 * 
 * 
 * 
 * 
 */
public class Trader {

	enum SymbolType {
		Stock, Future, Option, ETF
	};
	
	private AtomicInteger connectedToMarket = new AtomicInteger(0);

	private MarketAPI marketAPI;

	/*
	 * 	Trader App that is given a market API (could be IB, NetTrade or whatever)
	 * 
	 * 
	 * 
	 */
	public Trader(MarketAPI marketAPI) {
		
		this.marketAPI = marketAPI;
		
		
		
	}
	
	public void start() {
		
		/* check error codes */
		marketAPI.connect(new ConnectionEventHandler() {
			
			/*
			 * Note callback in IB context!
			 * 
			 * 
			 */
			@Override
			public void information(InformationType it, KeyValues info) {
				System.out.println("---------------------------------------------------------------------------------------");
				System.out.println("- " + it.name() + " " + info.get("MSG"));
				System.out.println("---------------------------------------------------------------------------------------");
			}
			
			@Override
			public void disconnected(KeyValues info) {
				System.out.println("---------------------------------------------------------------------------------------");
				System.out.println("- DISCONNECTED -");
				System.out.println("---------------------------------------------------------------------------------------");
				
				// Do something smart here! Reconnect? Deal with orders? ETC ETC
				connectedToMarket.set(0);
			}
			
			@Override
			public void connected(KeyValues info) {
				System.out.println("---------------------------------------------------------------------------------------");
				System.out.println("- CONNECTED -");
				System.out.println("---------------------------------------------------------------------------------------");
				connectedToMarket.set(1);
			}
		});
		
		
		
		System.out.println("----> main loop waiting for connection....");
		while (connectedToMarket.get() != 1) {
			try {
				Thread.sleep(3000);
				System.out.println("----> and waiting....");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("----> main loop DONE waiting for connection....");
		
		
		marketAPI.addMarketStatusHandler(new MarketStatusHandler() {
			@Override
			public void message(int code, String msg) {
				System.out.println("MESSAGE:[" + code + "] " + msg);
			}
			
			@Override
			public void information(int code, String msg) {
				System.out.println("INFORMATION:[" + code + "] " + msg);
			}
		});
		
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * Loop and load all symbols from InstrumetnModel!
		 */
		System.out.println("Asking for all NQ Futures");
		marketAPI.loadSymbol(SecType.FUT, "NQ");
		
//		System.out.println("Asking for Stock ERIC.B");
//		marketAPI.loadSymbol(SecType.STK, "ERIC.B");
//		
//		System.out.println("Asking for Stock AAPL");
//		marketAPI.loadSymbol(SecType.STK, "AAPL");
		
		
		
		
		Set<String> sym = marketAPI.getAllSymbols();
		System.out.println(sym.size() + " symbols found");
		
		Set<IInstrument> instr = marketAPI.getAllInstruments();
		System.out.println(instr.size() + " instruments found");
		
		IBMarketAPI ib = (IBMarketAPI)marketAPI;
		
		
		for (IInstrument i : instr) {
			
			System.out.println("Subscribing to:" + i.symbol());
			marketAPI.subscribeToInstrument(i, new IInstrumentObserver() {
				@Override
				public void instrumentTraded(IInstrument ii, String last_trade_price, String last_trade_size, String last_trade_time, long timestamp) {
					System.out.println("instrumentTraded " + i.symbol() + last_trade_price);
				}
				
				@Override
				public void instrumentFieldUpdated(IInstrument ii, IBTickType tt) {
					// TODO implement this
					System.out.println("instrumentFieldUpdated " + tt.name());
				}
			});
		}
	
		/*
		marketAPI.enterOrder(
		   "ERIC.B",
		   1.0,
		   1,
		   Action.BUY,
		   OrderType.LMT,
		   TimeInForce.DAY,
		   0d,
		   1);
		*/
		
		// ib.subscribeToInstrument(ii, observer)
	}
}
