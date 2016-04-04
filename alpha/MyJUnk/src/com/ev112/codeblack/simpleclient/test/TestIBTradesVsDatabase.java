package com.ev112.codeblack.simpleclient.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ev112.codeblack.common.configuration.Configuration;
import com.ev112.codeblack.common.database.AlphaDbSingleton;
import com.ev112.codeblack.common.database.IAlphaDatabase;
import com.ev112.codeblack.common.database.TradeQueryResultItem;
import com.ev112.codeblack.common.utilities.DateTools;
import com.ev112.codeblack.pricecollector.ib.MarketStatusHandler;
import com.ev112.codeblack.pricecollector.ib.TwsMarketController;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.Execution;
import com.ib.client.ExecutionFilter;
import com.ib.client.Types.SecType;
import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController.IConnectionHandler;
import com.ib.controller.ApiController.ITradeReportHandler;

/**
 * 
 * @author Peter Andersson
 *
 */
public class TestIBTradesVsDatabase implements MarketStatusHandler, IConnectionHandler, ILogger {

	private Logger logger = LogManager.getLogger(getClass());
	private TwsMarketController ibMarket;
	private Configuration config;
	private int oktrades = 0;
	private int notfoundtrades = 0;

	public class Item {
		private String tradeKey;
		private Contract contract;
		private Execution execution;

		public Item(String tradeKey, Contract contract, Execution execution) {
			super();
			this.tradeKey = tradeKey;
			this.contract = contract;
			this.execution = execution;
		}

		public String getTradeKey() {
			return tradeKey;
		}

		public void setTradeKey(String tradeKey) {
			this.tradeKey = tradeKey;
		}

		public Contract getContract() {
			return contract;
		}

		public void setContract(Contract contract) {
			this.contract = contract;
		}

		public Execution getExecution() {
			return execution;
		}

		public void setExecution(Execution execution) {
			this.execution = execution;
		}

		@Override
		public String toString() {

			return String.format("%s %s %s %d %1.2f %s", execution.time(), execution.side(), contract.symbol(), execution.shares(), execution.price(), execution.orderRef());
		}
	}

	private Map<String, List<TradeQueryResultItem>> alpha_trades = new HashMap<String, List<TradeQueryResultItem>>();
	private Map<String, List<Item>> ib_trades = new HashMap<String, List<Item>>();

	
	public TestIBTradesVsDatabase() {
		
	}
	
	public void analyze(String sTWSHost, Integer sTWSPort, Integer sTWSClientId, String date_to_query, String dbHost, String dbUser, String dbPass, String dbSchema) {

		/**
		 * THIS APP CAN ONLY CHECK TRADES MADE IN THE LAST 24 HOURS! RECOMMENDED
		 * TO RUN JUST BEFORE, OR IMMEDIATELY AFTER CLOSE!
		 */

		/*
		 * Connect to ALPHA DB
		 */
		IAlphaDatabase sql = AlphaDbSingleton.getDbInstance(dbHost, "3306", dbSchema, dbUser, dbPass);
		if (sql == null) {
			System.err.println("Failed to open DB");
			System.exit(1);
		}

		/*
		 * Load trades from ALPHA DB
		 */

		List<TradeQueryResultItem> list = sql.queryTradesForDate(date_to_query);
		for (TradeQueryResultItem t : list) {

			String key = t.getOwnref();

			if (alpha_trades.get(key) != null) {
				// System.out.println("    adding to existing list (" + key + ")");
				alpha_trades.get(key).add(t);
			} else {
				//System.out.println("    creating new list (" + key + ")");
				List<TradeQueryResultItem> l = new ArrayList<TradeQueryResultItem>();
				l.add(t);
				alpha_trades.put(key, l);
			}

			System.out.println("Adding Alpha trade " + key + " sym:" + t.getSymbol() + " price:" + t.getPrice() + " Qty:" + t.getQty() + " from database");
		}

		System.out.println("Loaded " + list.size() + " items from Alpha trades table at " + dbHost);

		/*
		 * Connect to IB/TWS
		 */
		System.out.println("Connecting to IB market at Host:" + sTWSHost + " port:" + sTWSPort);
		System.out.println("Wait for message that connection succeeded...");
		ibMarket = new TwsMarketController(this, this, this);
		ibMarket.connect(sTWSHost, sTWSPort, sTWSClientId,"");

		final Map<String, List<TradeQueryResultItem>> alpha_copy = new HashMap<String, List<TradeQueryResultItem>>(alpha_trades);

		/*
		 * BY NOW, WE HAVE THE ALPHA DB TRADES IN alpha_trades, and a copy in
		 * alpha_copy
		 */

		System.out.println("Loading IB Trades and testing against alpha trades");
		/*
		 * Query IB/TWS about our own executed orders
		 */
		ibMarket.reqExecutions(new ExecutionFilter(), new ITradeReportHandler() {

			boolean doneLoading = false;

			@Override
			public void tradeReport(String tradeKey, Contract contract, Execution execution) {

				String symbol = contract.symbol();
				if (contract.secType() == SecType.FUT) {
					symbol = contract.localSymbol();
				}
				
				if (execution.orderRef() == null) {
					System.out.println("Tradekey:" + tradeKey + " symbol=" + symbol + " price:" + execution.price() + " qty:" + execution.shares()
							+ " placed using client id:" + execution.clientId() + " (Probably TWS)");
				} 
				else {
					if (!doneLoading) {

						System.out.println(
								"Looking for match with IB Tradekey:" + tradeKey + 
								" symbol=" + symbol + 
								" orderref:" + execution.orderRef() +
								" price:" + execution.price() + 
								" qty:" + execution.shares()	+ 
								" placed using client id:" + execution.clientId());
						
						// We use our own orderRef as key, could
						String key = execution.orderRef();

						/*
						 * Save the IB Trade
						 */
						if (ib_trades.get(key) != null) {
							ib_trades.get(key).add(new Item(tradeKey, contract, execution));
						} else {
							List<Item> l = new ArrayList<Item>();
							l.add(new Item(tradeKey, contract, execution));
							ib_trades.put(key, l);
						}

						/*
						 * Match the IB Trade with our list of Alpha trades...
						 */
						List<TradeQueryResultItem> list = alpha_copy.get(key);
						if (list == null) {
							System.out.println(execution.time() + " IB trade " + key + " not found at all in Alpha Trades List!!!!");
						} 
						else {

							System.out.println(execution.time() + "Looking for IB trade " + key + " amongst " + list.size() + " Alpha trades");
							boolean found = false;
							for (TradeQueryResultItem tr : list) {

								if (!found) {

									if (tr.getSymbol().substring(0, 1).equals(symbol.substring(0, 1)) && 
										tr.getPrice() == execution.price() && 
										tr.getQty() == execution.shares()) 
									{
										System.out.println(execution.time() +
												" IB trade " + key + " " +
												symbol + " price:" +
												execution.price() + " qty:" +
												execution.shares() +
												" matched with Alpha DB!");
										
										found = true;
										oktrades++;
									}
									else {
										System.out.println("     .... not found");
									}
								}

							}

							if (!found) {
								
								System.out.println(
									">>> ERROR:" + execution.time() + 
									" IB trade " + key + 
									" " + symbol + 
									" price:" + execution.price() + 
									" qty:" + execution.shares()	+ 
									" not found in Alpha Trades List!");
								
								notfoundtrades++;
							}
						}

					}
				}
			}

			@Override
			public void tradeReportEnd() {
				doneLoading = true;

				// System.out.println("----------------- TESTING FOR MISSING TRADES -----------------------");
				//
				// for (String t : alpha_copy.keySet()) {
				// System.out.println("Unmatched Alpha trade:" +
				// alpha_copy.get(t).toString());
				// }
				//
				// for (String t : ib_trades.keySet()) {
				// System.out.println("Unmatched IB trade:" +
				// ib_trades.get(t).toString());
				// }

				System.out.println("Finished loading IB trades. Matched trades: " + oktrades + " Unmatched trades:" + notfoundtrades);
				if (notfoundtrades > 0) {
					System.out.println("*** ERRORS FOUND ***");
				}
				else {
					System.out.println("+++ A PERFECT DAY +++");
				}
			}

			@Override
			public void commissionReport(String tradeKey, CommissionReport commissionReport) {
				// logger.info("Commission on " + tradeKey + " comm:" +
				// commissionReport.m_commission);
			}
		});

		System.out.println("Sleeping 15s while waiting for IB query to finish....");
		sleepSec(15);
		System.out.println("Exiting app normally....");
		System.exit(1);
	}

	private void sleepSec(int secs) {
		try {
			Thread.sleep(secs * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void log(String valueOf) {
	}

	@Override
	public void connected() {
		System.out.println("Connected...");
	}

	@Override
	public void disconnected() {
		System.out.println("Disconnected...");
	}

	@Override
	public void accountList(ArrayList<String> list) {
	}

	@Override
	public void error(Exception e) {
		System.out.println("Exception:" + e.getLocalizedMessage());
	}

	@Override
	public void message(int id, int errorCode, String errorMsg) {
		// System.out.println(errorMsg);
	}

	@Override
	public void show(String string) {
	}

	@Override
	public void information(int code, String msg) {
		System.out.println("Error:" + code + " : " + msg);
	}

	@Override
	public void message(int code, String msg) {
		System.out.println(code + " : " + msg);
	}

	public static void main(String[] args) {
		System.out.println("Version 1.1");
		TestIBTradesVsDatabase tit = new TestIBTradesVsDatabase();
		tit.parseCommandLine(args);
	}
	
	private void parseCommandLine(String[] args) {

//		String sTWSHost = "192.168.2.199";
//		Integer sTWSPort = 6661;
//		Integer sTWSClientId = 22;
//
//		String date_to_query = DateTools.getTodayAsStr(); // "2014-09-22";
//
//		String dbHost = "192.168.2.199";
//		String dbUser = "alpha";
//		String dbPass = "alpha";
//		String dbSchema = "alpha";

		Options options = new Options();
		
		options.addOption(createOption("help", false, "Show help", false));
		options.addOption(createOption("twshost"	, true, "hostname/ipaddress of TWS host", true));
		options.addOption(createOption("twsport"	, true, "portname of TWS API (check so that it is enabled too)", true));
		options.addOption(createOption("twsclientid", true, "TWS Client Id. Choose a unique number that wont clash with other TWS clients.", true));
		options.addOption(createOption("dbhost"	, true, "database hostname/ipaddress", true));
		options.addOption(createOption("dbuser"	, true, "database user, normally alpha", true));
		options.addOption(createOption("dbpass"	, true, "database password", true));
		options.addOption(createOption("dbschema", true, "database schema, normally alpha", true));

		CommandLineParser parser = new BasicParser();
		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("testibtrades", options);
				System.exit(2);
			}
			
			String sTWSHost = cmd.getOptionValue("twshost");
			Integer sTWSPort = Integer.parseInt(cmd.getOptionValue("twsport"));
			Integer sTWSClientId = Integer.parseInt(cmd.getOptionValue("twsclientid"));;

			String date_to_query = DateTools.getTodayAsStr();

			String dbHost	= cmd.getOptionValue("dbhost");
			String dbUser	= cmd.getOptionValue("dbuser");
			String dbPass	= cmd.getOptionValue("dbpass");
			String dbSchema	= cmd.getOptionValue("dbschema");
			
			analyze(sTWSHost, sTWSPort, sTWSClientId, date_to_query, dbHost, dbUser, dbPass, dbSchema);
			// System.out.println("Would analyze:" + sTWSHost + ":" + sTWSPort + ":" + sTWSClientId + ":" + date_to_query + ":" + dbHost + ":" + dbUser + ":" + dbPass + ":" + dbSchema);
		} 
		catch (ParseException e) {
			System.out.println("Arguments to TestIBTradesVsDatabase ");
			for (int i = 0; i < args.length; i++) {
				System.out.print(args[i]);
				System.out.print(" ");
			}
			System.out.println("");
			System.err.println("Error:" + e.getLocalizedMessage());
			
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("testibtrades", options);
			System.exit(3);
		}
		
	}
	
	
	private Option createOption(String opt, boolean hasArg, String description, boolean required) throws IllegalArgumentException {
		
		Option option = new Option(opt, hasArg, description);
		option.setRequired(required);
		
		// add more options if necessary
		
		//helpOption.setDescription(description);
		//helpOption.setLongOpt(longOpt);
		//helpOption.setOptionalArg(optionalArg);
		//helpOption.setType(type);
		//helpOption.setValueSeparator(sep);
		
		return option;
	}

}
