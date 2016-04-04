package com.ev112.codeblack.simpleclient.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ev112.codeblack.atc.connections.CONNECTION_STATUS;
import com.ev112.codeblack.atc.connections.PriceCollectorConnection;
import com.ev112.codeblack.atc.connections.PriceCollectorConnectionEventHandler;
import com.ev112.codeblack.atc.connections.ServerConnection;
import com.ev112.codeblack.atc.connections.ServerConnectionEventHandler;
import com.ev112.codeblack.atc.connections.StatusEventHandler;
import com.ev112.codeblack.common.configuration.Configuration;
import com.ev112.codeblack.common.dataengine.DataEngine;
import com.ev112.codeblack.common.generated.messages.PriceCollectorClockPulseBdx;
import com.ev112.codeblack.common.generated.messages.PriceCollectorFacilityBdx;
import com.ev112.codeblack.common.generated.messages.PriceCollectorQuoteBdx;
import com.ev112.codeblack.common.generated.messages.PriceCollectorReplayStartedBdx;
import com.ev112.codeblack.common.generated.messages.PriceCollectorReplayStoppedBdx;
import com.ev112.codeblack.common.generated.messages.PriceCollectorTradeBdx;
import com.ev112.codeblack.common.generated.messages.StatusEvent;
import com.ev112.codeblack.common.instmodel.InstrumentModel;
import com.ev112.codeblack.common.ordercontroller.AdapterMarketStatus;
import com.ev112.codeblack.common.ordercontroller.IBOrderAdapter;
import com.ev112.codeblack.common.ordercontroller.OrderAdapterEventHandler;
import com.ev112.codeblack.common.ordercontroller.OrderUpdateEvent;
import com.ev112.codeblack.common.ordercontroller.OwnTrade;
import com.ev112.codeblack.common.strategy.strategies.tech.Bucketeer;
import com.ev112.codeblack.common.strategy.strategies.tech.Candle;
import com.ev112.codeblack.common.strategy.strategies.tech.CandleEventHandler;
import com.ev112.codeblack.common.strategy.strategies.tech.ClassicMACD;
import com.ev112.codeblack.common.utilities.DateTools;

public class TestPriceCollectorConnection implements 
	ServerConnectionEventHandler, 
	PriceCollectorConnectionEventHandler,
	StatusEventHandler,
	OrderAdapterEventHandler
{
	private static int count = 0;
	private DataEngine dataEngine;
	private Configuration conf = new Configuration("PETER");
	private InstrumentModel instrModel = conf.getInstrumentModel();
	private ClassicMACD macd = null;
	private Bucketeer bucketeer = new Bucketeer();  // 10 second buckets!
	private Double old_macd = null;
	
	IBOrderAdapter iboa;
	
	public static void main(String[] args) {
		new TestPriceCollectorConnection();
	}

	private class Trade {
		private int vol;
		private double price;
		private String time;
		
		public Trade(int vol, double price, String time) {
			this.vol = vol;
			this.price = price;
			this.time = time;
		}
		public int getVol() {
			return vol;
		}
		public double getPrice() {
			return price;
		}
		public String getTime() {
			return time;
		}
	}
	
	List<Trade> trades = new ArrayList<Trade>();

	private void addTrade(String time, int v, double p) {
		trades.add(new Trade(v,p, time));
	}
	
	private void printTrades() {
		Double pnl = 0.0;
		int current_pos = 0;
		
		Boolean have_pos = false;
		Integer last_pos = 0;
		Double last_price = 0d;
		String last_date = "";
		
		System.out.println("------ TRADES -------");
		for (Trade t : trades) {
			current_pos += t.getVol();
			System.out.println(t.getTime() + " " + (t.getVol() < 0 ? "  SALE" : "  PURCHASE") + " " + t.getVol() + " @ " + t.getPrice());
			pnl -= (t.getVol() * t.getPrice());
			
			if (have_pos) {
				have_pos = false;
				Double result = 0d;
				if (last_pos > 0) {
					result = t.getPrice() - last_price;
				}
				else {
					result = last_price - t.getPrice();
				}
				
				System.out.println((result > 0 ? "PROFIT! " : "LOSS! ") + result);
				
			}
			else {
				have_pos = true;
				last_pos = t.getVol();
				last_price = t.getPrice();
				last_date = t.getTime();
			}
			
			
			
			if (current_pos == 0) {
				System.out.println("RUNNING P&L:" + pnl);
			}
		}
	}
	
	
	// ServerConnectionEventHandler events
	
	@Override
	public void serverStatisticsUpdate() {
		// System.out.println("serverStatisticsUpdate()");
		count++;
	}

	@Override
	public void connectionStatusChangeCallback(ServerConnection pConnection, CONNECTION_STATUS pStatus) {
		System.out.println("connectionStatusChangeCallback() - value :" + pStatus.name());

		if (pStatus == CONNECTION_STATUS.CONNECTED) {
			PriceCollectorConnection p = (PriceCollectorConnection) pConnection;
//			p.subscribeQuotes("ERIC.B", this);
//			p.subscribeTrades("ERIC.B", this);
//			p.subscribeQuotes("OMXS305K", this);
//			p.subscribeTrades("OMXS305K", this);
//			p.subscribeQuotes("ES5L", this);
//			p.subscribeTrades("ES5L", this);
			p.subscribeQuotes("NQ5L", this);
			p.subscribeTrades("NQ5L", this);
			
			
			// Order Adapter
			iboa = new IBOrderAdapter(conf, this, conf.getRefDb());
		}
	}
	
	// PriceCollectorConnectionEventHandler events 
	
	@Override
	public void priceCollectorTradeEvent(PriceCollectorTradeBdx pBdx) {
		// @formatter:off
		if (pBdx.getReplay()) {
			// System.out.println(DateTools.getTimeAsStr(pBdx.getTime()) + " Replaying TRADE " + pBdx.getSymbol() + " in state " + pBdx.getReplaystate() + " " + pBdx.getPrice() + " " + pBdx.getVolume() );
		}
		else {
			// System.out.println(DateTools.getTimeAsStr(pBdx.getTime()) + " Live      TRADE " + pBdx.getSymbol() + " " + pBdx.getPrice() + " " + pBdx.getVolume());
		}
		
		dataEngine.process(pBdx); //  Can DataEngine process Messages???
		// @formatter:on
	}

	@Override
	public void priceCollectorQuoteEvent(PriceCollectorQuoteBdx pBdx) {
		// TODO Auto-generated method stub
		// System.out.println("QuoteBdx:" + pBdx.getSymbol() + " " + DateTools.getTimeAsStr(pBdx.getTime()) + " : " + pBdx.getBidqty() + " " + pBdx.getBid() + " / " + pBdx.getAsk() + " " + pBdx.getAskqty());
		if (pBdx.getReplay()) {
			// System.out.println(DateTools.getTimeAsStr(pBdx.getTime()) + " Replaying QUOTE " + pBdx.getSymbol() + " in state " + pBdx.getReplaystate() + " " + pBdx.getBid() + " " + pBdx.getAsk() );
		}
		else {
			// System.out.println(DateTools.getTimeAsStr(pBdx.getTime()) + " Live      QUOTE " + pBdx.getSymbol() + " " + pBdx.getBid() + " " + pBdx.getAsk());
		}
		
		dataEngine.process(pBdx); //  Can DataEngine process Messages???
	}

	@Override
	public void priceCollectorClockPulseEvent(PriceCollectorClockPulseBdx pBdx) {
		// TODO Auto-generated method stub
		// System.out.println("ClockPulseBdx:" + pBdx.getSymbol() + " " + DateTools.getTimeAsStr(pBdx.getTime()));
	}

	@Override
	public void facilityUpdate(PriceCollectorFacilityBdx pBdx) {
		// TODO Auto-generated method stub
		System.out.println("FacilityBdx:" + pBdx.getSymbol() + " " + DateTools.getTimeAsStr(pBdx.getTime()) + " " + pBdx.getFacilityTypes());
	}

	@Override
	public void priceCollectorStatusEvent(List<StatusEvent> pEvents) {
		// TODO Auto-generated method stub
		for (StatusEvent s : pEvents) {
			System.out.println("addEvent:" + s.getSeverity() + " " + s.getMessage());
		}
	}

	@Override
	public void priceCollectorReplayStarted(PriceCollectorReplayStartedBdx pBdx) {
		System.out.println("---- REPLAY STARTED FOR " + pBdx.getFlowtype() + " " + pBdx.getSymbol() + " ---");
	}

	@Override
	public void priceCollectorReplayStopped(PriceCollectorReplayStoppedBdx pBdx) {
		System.out.println("---- REPLAY STOPPED FOR " + pBdx.getFlowtype() + " " + pBdx.getSymbol() + " ---");
	}
	
	
	// SystemStatusEventHandler
	
	@Override
	public void addEvent(List<StatusEvent> pEvents) {
		for (StatusEvent e : pEvents) {
			System.out.println(e.getMessage());
		}
	}
	
	
	private int dir = 0, olddir = 0;
	
	public TestPriceCollectorConnection() {

		// L O A D C O N F I G U R A T I O N
		conf = new Configuration("PETER");
		instrModel = conf.getInstrumentModel();

		bucketeer.addPeriodSize(10);
		
		dataEngine = new DataEngine(
				false,		// pInLiveMode - ignore
				new Date(),	// pCurrentDate, 
				false,		// pUseSwingThread, 
				instrModel,	// pInstModel, 
				true);		// pCheckDataQuality);
		
		dataEngine.addListener(bucketeer);
		
		macd = new ClassicMACD(12,26, 9);
		
		bucketeer.addCandleEventHandler(new CandleEventHandler() {

			@Override
			public void preAddCandle() {
				// TODO implement this
				
			}

			@Override
			public void postAddCandle() {
				// TODO implement this
				
			}

			@Override
			public void addCandle(Candle candle) {
				
				if (macd == null)
					return;
				
				macd.addPrice(candle.getClose());
				
				if (old_macd != null) {
					
					Double k = macd.getMacd() - old_macd;
					Boolean  enough = (k > 0.11 || k < -0.11);
					Boolean level_ok = macd.getMacd() > 0.25 || macd.getMacd() < -0.25;
					
					System.out.println(DateTools.getCurTimeAsStr() + " CANDLE CLOSE: " + candle.getClose() + " Current K = " + f(k) + " ENOUGH " + enough + " LEVEL " + level_ok);
					System.out.println("#trades:" + trades.size());
					
					enough = true;  // !!
					
					/*
					 * TEST CROSSOVER
					 */
					
					if (CLOSE_ENOUGH(macd.getMacd() - macd.getSignal())) {
						dir = 0;
						//System.out.println("0");
					}
					else 
						if (macd.getMacd() > macd.getSignal()) {
							olddir = dir;
							dir = 1;
						}
						else 
							if (macd.getMacd() < macd.getSignal()) {
								olddir = dir;
								dir = -1;
							}
							else 
								if (CLOSE_ENOUGH(macd.getMacd())) {
									System.out.println(DateTools.getCurTimeAsStr() + " crossing ZERO line!");
								}
					
					
					switch (dir) {
						case 0:
						break;
						case -1:
							if (olddir == 1 || olddir == 0) {
								if (enough) {
									System.out.println(DateTools.getCurTimeAsStr() + "=== TRADE === : MACD crossing SIGNAL from ABOVE - SELL @ " + candle.getClose());
									addTrade(DateTools.TimeFromTimestamp(candle.getTime()), -1, candle.getClose());
									printTrades();
								}
								else
									System.out.println(DateTools.getCurTimeAsStr() + "MACD crossing SIGNAL from ABOVE - BUT NOT ENOUGH K " + k);
								// sell(1, candle);
	//							OwnOrder order;
	//							iboa.addOrder(new OrderInsertRequest(order, pBusinessTime));
							}
							else
								System.out.println(DateTools.getCurTimeAsStr() + "    MACD " + f(macd.getMacd()) + " STILL under SIGNAL " + f(macd.getSignal()) + " with " + (macd.getMacd() - macd.getSignal()));
							break;
						case 1:
							if (olddir == -1 || olddir == 0) {
								
								if (enough) {
									System.out.println(DateTools.getCurTimeAsStr() + "=== TRADE === : MACD crossing SIGNAL from UNDER - BUY @ " + candle.getClose());
									addTrade(DateTools.TimeFromTimestamp(candle.getTime()), 1, candle.getClose());
									printTrades();
								}
								else
									System.out.println(DateTools.getCurTimeAsStr() + "MACD crossing SIGNAL from UNDER BUT NOT ENOUGH K " + k);
								
							}
							else
								System.out.println(DateTools.getCurTimeAsStr() + "    MACD " + f(macd.getMacd()) + " STILL over SIGNAL " + f(macd.getSignal()) + " with " + (macd.getMacd() - macd.getSignal()));
							break;
						default:
							System.out.println("ERROR!!!");
					}
				}
				
				if (macd != null)
					old_macd = macd.getMacd();

				printTrades();
			}
			
			
		});

		
				
		/**
		 * 
		 * 
		 */
		System.out.println("Connecting to PriceCollector");
		// You could argue that the second arg to this call is not necessary...

		// @formatter:off
		PriceCollectorConnection p = new PriceCollectorConnection(
			this, 
			this, 
			conf, 
			"PriceCollector",
			/* inSwing */false);
		// @formatter:on

		p.start(); /* inkonsekvent, denna klass är tyligen en Thread.... */

		try {
			Thread.sleep(180000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Boolean CLOSE_ENOUGH(Double diff) {
		// System.out.println("diff=" + diff);
		return diff > -0.001 && diff < 0.001;
	}
	
	private String f(Double d) {
		return String.format("%1.4f", d);
	}

	
	// OrderAdapterEventHandler
	
	@Override
	public void ownOrderUpdate(OrderUpdateEvent pOrderUpdateEvent) {
		System.out.println("xxxxx ORDER xxxxx " + pOrderUpdateEvent.getOwnOrder().toString());
	}

	@Override
	public void ownTradeUpdate(OwnTrade pTrade) {
		System.out.println("xxxxx TRADE xxxxx");
	}

	@Override
	public void broadcastStatusEvent(String pMessage, String pSource, int pSeverity) {
		System.out.println("    >>> " + pSource + " " + pMessage);
	}

	@Override
	public void adapterMarketStatusEvent(AdapterMarketStatus status) {
		System.out.println("Adapter Event: " + status.name());
	}
}
