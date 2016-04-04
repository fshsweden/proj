package com.ev112.codeblack.simpleclient.test;

import java.util.Date;
import java.util.List;

import com.ev112.codeblack.common.database.AlphaDbSingleton;
import com.ev112.codeblack.common.database.IAlphaDatabase;
import com.ev112.codeblack.common.database.MarketTradeAndQuotesResultItem;
import com.ev112.codeblack.common.database.MarketTradeResultItem;
import com.ev112.codeblack.common.database.OrderEventQueryResultItem;
import com.ev112.codeblack.common.database.StrategyQueryResultItem;
import com.ev112.codeblack.common.utilities.DateTools;

public class TestAlphaDatabaseOnly {
	
	private IAlphaDatabase	db;

	public TestAlphaDatabaseOnly() {

		String host = "192.168.0.198";
		
		System.out.println("10 secs sleep : start profiler now!");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Connecting to db server");
		db = AlphaDbSingleton.getDbInstance(host, "3306", "alpha", "alpha", "alpha");
		if (db == null) {
			System.out.println("Failed to open database");
			System.exit(1);
		}
		System.out.println("opening DB");
		
		System.out.println("OPENING DATABASE ON " + host);
		
		// System.out.println("OPENING FSHSWEDEN DATABASE!");
		// db.openDb("192.168.0.199", "alpha", "alpha", "alpha");

		System.out.println("Running test");
		
		// testAddStrategyEvents();
		// testListStrategyEvents();
		// testAddOrderEvents();
		// testListOrderEvents();
		testAddTrade();
		
		// testQueryMarketTradesAndQuotes();
		// testQueryMarketTrades();
		// testMinMax();
		// testQueryMarketTradesAndQuotes2();
		// testAddRiskEvent();
		
		// testAddSystemEvents();
		
		System.out.println("closing DB");
//		db.closeDb();

		System.out.println("30 secs sleep before exit");
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.exit(1); // Exit process
	}

	public static void main(String args[]) {
		new TestAlphaDatabaseOnly();
	}
	
	private void testAddRiskEvent() {
		db.addRiskEvent("Strategy_id", "symbol1", "event_time", "reason", "source", "param_id", "order_id", "ownref");
		db.addRiskEvent("Strategy_id", "symbol2", "event_time", "reason", "source", "param_id", "order_id", "ownref");
	}
	
	private void testAddStrategyEvents() {
		db.addStrategyEvent("TEST-STRATEGY", "ABBN", "WAITING", "CHANNEL", "GOD", new Double(100.0), "BREAK-DOWN", "??", "??", "Context");
		db.addStrategyEvent("TEST-STRATEGY", "BOL", "WAITING", "CHANNEL", "GOD", new Double(200.0), "BREAK-OUT", "??", "??", "Context");
		db.addStrategyEvent("TEST-STRATEGY", "SAND", "WAITING", "CHANNEL", "GOD", new Double(300.0), "BREAK-DOWN", "??", "??", "Context");
	}

//	private void testAddSystemEvents() {
//		db.addSystemEvent(3, "WB", "STRATEGY", "Error loading strategy S2_BB_SE3", "Error loading strategy", "SAS", 4, 1, "2014-10-11");
//		db.addSystemEvent(13, "IBSERVER", "MARKET", "Error subscribing to symbol ZSD: Contract Not found", "Contract not found", "ZSD", 4, 1, "2014-10-11");
//		db.addSystemEvent(-6, "RISK", "RISK", "Error loading risk definition for ROOT", "Error loading risk", "", 7, 21, "2014-10-11");
//	}
	
	private void testListStrategyEvents() {
		List<StrategyQueryResultItem> strategy_events = db.queryStrategyEventsToday("TEST-STRATEGY");
		for (StrategyQueryResultItem o : strategy_events) {
			System.out.println("Strategy event:" + o.getReason());
		}
	}
	
	private void testAddOrderEvents() {
		db.addOrderEvent(123, "ABBN", 99.88, 200, 200, 
				0, // filled, dont know
				0.0, // avgFillPrice,
				0L, // Long permId,
				0, // Integer parentId,
				0.0, // Double lastFillPrice,
				"2014-07-25 10:45:00.345", "Buy", "TEST-STRATEGY", "jkhgasdkjgkfdjgkj", "ACTIVE", "No reason...", "EOD", "LMT", "INMARKET");
		
		db.addOrderEvent(123, "ABBN", 99.88, 200, 100, 
				0, // filled, dont know
				0.0, // avgFillPrice,
				0L, // Long permId,
				0, // Integer parentId,
				0.0, // Double lastFillPrice,
				"2014-07-25 10:45:00.345", "Buy", "TEST-STRATEGY", "jkhgasdkjgkfdjgkj", "PARTIALLY_FILLED", "No reason...", "EOD", "LMT",
				"INMARKET");
		db.addOrderEvent(123, "ABBN", 99.88, 100, 0, 
				0, // filled, dont know
				0.0, // avgFillPrice,
				0L, // Long permId,
				0, // Integer parentId,
				0.0, // Double lastFillPrice,
				"2014-07-25 10:45:00.345", "Buy", "TEST-STRATEGY", "jkhgasdkjgkfdjgkj", "FILLED", "No reason...", "EOD", "LMT", "INMARKET");
	}
	
	private void testListOrderEvents() {
		List<OrderEventQueryResultItem> order_events = db.queryOrderEventsForDate(DateTools.getTodayAsStr());
		for (OrderEventQueryResultItem o : order_events) {
			System.out.println("Order event:" + o.toString());
		}
	}
	
	private void testAddTrade() {
		Long l = new Long(new Date().getTime());
		db.addTrade("perm_id", "12345", "APPL", "3.45", "2000", l.toString(), "BUY", "some_strategy", "ownref", "order_status", "reason", "tif", "order_type", "substate","tradeKey");
	}
	
	private void testQueryMarketTradesAndQuotes() {
		
		Long start = System.currentTimeMillis();
		List<MarketTradeAndQuotesResultItem> res = db.queryMarketTradeAndQuotesForDateRange("AAPL", "2014-10-01", "2014-10-02");
		String opmsg = "Operation took " + (System.currentTimeMillis() - start) + " milliseconds";
		
		start = System.currentTimeMillis();
		for (MarketTradeAndQuotesResultItem t : res) {
			Long ts = t.getTs();
			Date date = new Date(ts);
			
			System.out.print("Time:" + date + " ");
			if (t.getType().equals("trade")) {
				System.out.println("TRADE: " + t.getPrice() + " / " + t.getSize());
			}
			else {
				System.out.println("QUOTE: " + t.getBid() + " / " + t.getAsk());
			}
		}
		String printmsg = "Printout took " + (System.currentTimeMillis() - start) + " milliseconds";

		System.out.println(opmsg);
		System.out.println(printmsg);
	}


	private void testQueryMarketTrades() {
		
		Long start = System.currentTimeMillis();
		List<MarketTradeResultItem> res = db.queryMarketTradesForDateRange("AAPL", "2014-10-01", "2014-10-02");
		String opmsg = "Operation took " + (System.currentTimeMillis() - start) + " milliseconds";
		
		start = System.currentTimeMillis();
		for (MarketTradeResultItem t : res) {
			Long ts = t.getTs();
			Date date = new Date(ts);
			System.out.print("Time:" + date + " ");
			System.out.println("TRADE: " + t.getPrice() + " / " + t.getSize());
		}
		String printmsg = "Printout took " + (System.currentTimeMillis() - start) + " milliseconds";

		System.out.println(opmsg);
		System.out.println(printmsg);
	}
	
	
	private void testQueryMarketTradesAndQuotes2() {
		
		Long start = System.currentTimeMillis();
		String[] arr = {"AAPL","FB"};
		String[] dates = {"2014-09-01","2014-09-02", "2014-09-03"};
		
		for (int i=0; i<3; i++) {
			
			List<MarketTradeAndQuotesResultItem> res = db.queryMarketTradeAndQuotesForDateRange2(arr, dates[i], dates[i]);
			
			String opmsg = "Operation took " + (System.currentTimeMillis() - start) + " milliseconds";
			
			start = System.currentTimeMillis();
			for (MarketTradeAndQuotesResultItem t : res) {
				Long ts = t.getTs();
				Date date = new Date(ts);
	//			System.out.print("Time:" + date + " ");
	//			System.out.println("TRADE: " + t.getPrice() + " / " + t.getSize());
			}
	//		String printmsg = "Printout took " + (System.currentTimeMillis() - start) + " milliseconds";
	
			System.out.println(opmsg);
	//		System.out.println(printmsg);
			
		}
		
		// res = null;
		System.gc();
	}
	
	private void testMinMax() {
		Long start = System.currentTimeMillis();
		List<String> q = db.queryDatesFromZdatQuotes();
		String opmsg = "Operation took " + (System.currentTimeMillis() - start) + " milliseconds";
		System.out.println("Dates from:" + q.get(0) + " to:" + q.get(q.size()-1));
		System.out.println(opmsg);
		
		start = System.currentTimeMillis();
		List<String> t = db.queryDatesFromZdatQuotes();
		System.out.println("Dates from:" + t.get(0) + " to:" + t.get(t.size()-1));
		opmsg = "Operation took " + (System.currentTimeMillis() - start) + " milliseconds";
		System.out.println(opmsg);
	}

}
