package com.ev112.codeblack.simpleclient.test;

import java.util.List;

import com.ev112.codeblack.common.database.AlphaDbSingleton;
import com.ev112.codeblack.common.database.IAlphaDatabase;
import com.ev112.codeblack.common.database.StatusEventQueryResultItem;
import com.ev112.codeblack.common.utilities.DateTools;

public class TestAlphaDatabase {
	
	private IAlphaDatabase db;
	private volatile int counter = 0;

	/**
	 * 
	 * @author peterandersson
	 *
	 */
	private class Job implements Runnable {
		
		private final int number;
		private final String host;
		private volatile boolean done = false;
		
		public Job(final String host, final int number) {
			this.host = host;
			this.number = number;
		}
		
		@Override
		public void run() {
			System.out.println("Aquiring DB Connection #" + number);
			db = AlphaDbSingleton.getDbInstance(host, "3306", "alpha", "alpha", "alpha");
			if (db == null) {
				System.out.println("Failed to open database!");
				System.exit(1);
			}
			System.out.println("Got DB Connection #" + number + " executing job!");
		
			long startTime = System.nanoTime();
			List<String> result = db.queryDatesFromZdatTrades();
			long endTime = System.nanoTime();
			long duration = (endTime - startTime) / 1000000;  // milliseconds.
		
			System.out.println("Job #" + number + " executed OK in " + duration + " ms ");
			
			done = true;
		}
		
		public boolean isDone() {
			return done;
		}
		
		public Integer getNumber() {
			return number;
		}
	}
	
	
	public TestAlphaDatabase() {

		//ConfigurationLoader conf = new ConfigurationLoader("192.168.0.198", "8888");

		db = AlphaDbSingleton.getDbInstance("192.168.0.198", "3306", "alpha", "alpha", "alpha");
		
		if (db == null) {
			System.out.println("Failed to open DB!");
			System.exit(1);
		}
		
		/*
		 * 	JOB TEST WITH MULTIPLE THREADS
		 * 		
		String host = "192.168.0.199";
		final int no_of_connections = 40;
		final List<Job> jobs = new ArrayList<Job>();
		
		for (int i=0; i<no_of_connections; i++) {
			Job j = new Job("192.168.0.199", i);
			jobs.add(j);
			Thread t = new Thread(j);
			t.start();
		}
		
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("Checking jobs...");
		for (Job job : jobs) {
			if (!job.isDone()) {
				System.out.println("Hmmm Job " + job.getNumber() + " isnt done....");
			}
		}
		System.out.println("DONE Checking jobs...");
*/		
		
//		db.addStrategyEvent("TEST-STRATEGY", "ABBN", "WAITING", "CHANNEL", "GOD", new Double(100.0), "BREAK-DOWN", "??", "??", "Context");
//		db.addStrategyEvent("TEST-STRATEGY", "BOL", "WAITING", "CHANNEL", "GOD", new Double(200.0), "BREAK-OUT", "??", "??", "Context");
//		db.addStrategyEvent("TEST-STRATEGY", "SAND", "WAITING", "CHANNEL", "GOD", new Double(300.0), "BREAK-DOWN", "??", "??", "Context");
//
//		List<StrategyQueryResultItem> strategy_events = db.queryStrategyEventsToday("TEST-STRATEGY");
//		for (StrategyQueryResultItem o : strategy_events) {
//			System.out.println("Strategy event:" + o.getReason());
//		}
//
//		List<OrderEventQueryResultItem> order_events = db.queryOrderEventsForDate(DateTools.getTodayAsStr());
//		for (OrderEventQueryResultItem o : order_events) {
//			System.out.println("Order event:" + o.toString());
//		}
//
//		db.addTrade("perm_id", "12345", "APPL", "3.45", "2000", "trade_time", "BUY", "some_strategy", "ownref", "order_status", "reason", "tif", "order_type", "substate","tradeKey");
		
				
//		db.addOrderEvent(1, "symbol", 1.0, 10, 0, 0, 0.0, 12345L, 0, 77.88,"order_time", "action", "strategy_id", "ownref", "order_status", "reason", "tif", "order_type", "substate");
//		db.addRiskEvent("strategy_id", "symbol", "event_time", "reason", "source", "parameter_id", "order_id", "ownref");
//		db.addSystemEvent(33, "subsystem", "module", "message", "short_message", "symbol", 1, 2, "Datetime");
//		db.replaceIntoHiLowData("symbol", 100, "date", 12345L, 12346L, 23, 45.34, 56.67, 11.12, 88.99, 340000, 23);

		db.addStatusEvent("TESTSERVER", 1,System.currentTimeMillis(), "A test status message");
		
		List<StatusEventQueryResultItem> list = db.queryStatusEventsForDate(DateTools.getTodayAsStr());
		for (StatusEventQueryResultItem item : list) {
			System.out.println("" + item.getMessage());
		}
		
		// db.addCommission("tradeKey", 33.34, "currency");
		


		System.exit(1); // Exit process
	}

	public static void main(String args[]) {
		new TestAlphaDatabase();
	}
}
