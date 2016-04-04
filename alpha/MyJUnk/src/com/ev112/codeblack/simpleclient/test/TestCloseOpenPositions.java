package com.ev112.codeblack.simpleclient.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ev112.codeblack.common.database.AlphaDatabaseMySql;
import com.ev112.codeblack.common.instmodel.Instrument;
import com.ev112.codeblack.common.instmodel.InstrumentModel;
import com.ev112.codeblack.common.utilities.DateTools;
import com.ev112.codeblack.pricecollector.ib.TwsMarketController;
import com.ev112.codeblack.simpleclient.unsorted.ExecutionsRequest;
import com.ev112.codeblack.simpleclient.unsorted.IBNewContract;
import com.ev112.codeblack.simpleclient.unsorted.MarketRequest;
import com.ev112.codeblack.simpleclient.unsorted.Request;
import com.ev112.codeblack.simpleclient.unsorted.Request.RequestSubscriber;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.OrderStatus;
import com.ib.client.OrderType;
import com.ib.client.Types.Action;
import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController.IConnectionHandler;
import com.ib.controller.ApiController.IOrderHandler;
import com.ib.controller.ApiController.IPositionHandler;

/**
 * 
 * @author peterandersson
 *
 */
public class TestCloseOpenPositions implements IConnectionHandler, ILogger, RequestSubscriber {

	private TwsMarketController ibMarketCtrl;
	private List<IBNewContract> contracts = new ArrayList<IBNewContract>();
	private InstrumentModel tInstModel;
	private Collection<Instrument> collection;
	private List<Instrument> list;
	private AlphaDatabaseMySql adb;
	private Map<Integer, Request> requests = new HashMap<Integer, Request>();
	private boolean first = true;
	private String hostName = "192.168.2.199";

	/**
	 * 
	 * @author peterandersson
	 *
	 */
	public class Position {
		private Contract contract;
		private double position;
		
		public Position(Contract contract, double position) {
			super();
			this.contract = contract;
			this.position = position;
		}

		public Contract getContract() {
			return contract;
		}

		public void setContract(Contract contract) {
			this.contract = contract;
		}

		public double getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}
	}
	
	public List<Position> positions = new ArrayList<Position>();
	
	public static void main(String[] args) {
		new TestCloseOpenPositions();
	}
	
	public TestCloseOpenPositions() {
		
		
//		/**
//		 * Load Configuration and InstrumentModel
//		 */
//		ConfigurationLoader mConfigurationLoader = new ConfigurationLoader("192.168.2.20", "8888");
//		Element tSymbolsNode = (Element) mConfigurationLoader.getConfigNode("/AlphaTradingConfiguration/Common");
//		
//		/**
//		 * Connect to database
//		 */
//		adb = new AlphaDatabaseMySql();
//		adb.openDb(mConfigurationLoader);
//		
//		/**
//		 * Get a sorted list of Instruments
//		 */
//		String str = mConfigurationLoader.getInstrumentModelAsString();
//		try {
//			tInstModel = InstXmlDomHandler.loadInstrumentFromString(str);
//			
//			collection = tInstModel.getInstruments();
//			list = new ArrayList<Instrument>(collection);
//				
//			/*
//			 * Add a getSortedInstruments();
//			 */
//			Collections.sort(list);
//			
//			Collections.sort(list, new Comparator<Instrument>(){
//				@Override
//				public int compare(Instrument o1, Instrument o2) {
//					return o1.compareTo(o2);
//				}
//			});
//			
//			for (Instrument i : list) {
//				String currency = i.getMarket() != null ? " Currency: " + i.getMarket().getPriceCurrency() : "";
//				
//				if (i instanceof Derivative) {
//					Derivative d = (Derivative)i;
//					String ps = d.getExpirationDateString(); 
//					ps = ps.replace("-", "");
//					System.out.println("Instrument:" + i.getSymbol() + " Price Route:" + i.getPriceRoute() + " SecType:" + i.getType() + " " + ps + currency);
//				}
//				else
//				{
//					System.out.println("Instrument:" + i.getSymbol() + " Price Route:" + i.getPriceRoute() + " SecType:" + i.getType() + currency);
//				}
//			}
//			
//		}
//		catch (Exception ex) {
//			System.out.println("Exception: " + ex.getLocalizedMessage());
//			ex.printStackTrace();
//			System.exit(1);
//		}		
//
//		
//		/**
//		 * Change Instrument list into a list of IBContract
//		 */
//		for (Instrument i : list) {
//			if (i.getType() == Type.Future) {
//				
//				IBContract nc = new IBContract();
//				nc.symbol(i.getSymbol());
//				nc.exchange(i.getPriceRoute());
//				nc.currency(i.getMarket().getPriceCurrency());
//				nc.secType(SecType.FUT);
//				
//				nc.localSymbol(i.getLocalSymbol());
//				if (i instanceof Derivative) {
//					Derivative d = (Derivative)i;
//					String ps = d.getExpirationDateString(); 
//					ps = ps.replace("-", "");
//					nc.expiry(ps);
//					nc.symbol(d.getUnderlyingSymbol());
//				}
//				
//				if (is_expired(nc)) {
//					// System.out.println("Won't add " + nc.getID() + " since its expired...");
//				}
//				else {
//					contracts.add(nc);
//				}
//			}
//			if (i.getType() == Type.Stock) {
//				IBContract nc = new IBContract();
//				nc.symbol(i.getSymbol());
//				nc.exchange(i.getPriceRoute());
//				nc.currency(i.getMarket().getPriceCurrency());
//				nc.secType(SecType.STK);
//				contracts.add(nc);
//			}
//		}
		
		
		/**
		 * Connect to IB
		 */
		System.out.println("Connecting to TWS.....");
		ibMarketCtrl = new TwsMarketController(this, this, this);
		ibMarketCtrl.connect(hostName, 7496, 5,"");

		
		
		
		/*
		 * CLUNKY WAY OF WAITING UNTIL REPLIES HAVE ARRIVED.....
		 */
//		System.out.println("main thread Sleeping 30s....");
//		try {
//			Thread.sleep(30000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("main thread Done sleeping....!!!");
//		
//		
//		int count = 0;
//		for (final Request r : requests.values()) {
//		
//			if (r instanceof MarketRequest) {
//				MarketRequest mr = (MarketRequest)r;
//				//if (mr.got_market()) {
//					System.out.println(mr.getContract().getID() + " " + mr.getBidQty() + " @ " + mr.getBid() + " -- " + mr.getAskQty() + " @ " + mr.getAsk() + " : " + mr.getLast() + " " + mr.getLastQty());
//				//}
//				//if (mr.got_stats()) {
//					System.out.println("    " + mr.getOpen() + " -- " + mr.getClose());
//				//}
//			}
//			
//			if (r instanceof ExecutionsRequest) {
//				ExecutionsRequest er = (ExecutionsRequest)r;
//				
//				Map<String,Execution> ex = er.getExecutions();
//				for (String s : ex.keySet()) {
//					Execution e = ex.get(s);
//					System.out.println("Trade: " + s + " Acct:" + e.m_acctNumber + " Price:" + e.m_price + " Qty:" + e.m_shares);
//				}
//				Map<String, CommissionReport> cr = er.getCommissions();
//				for (String s : cr.keySet()) {
//					CommissionReport c = cr.get(s);
//					System.out.println("Trade: " + s + " Comm:" + c.m_commission + " Pnl:" + c.m_realizedPNL);
//				}
//			}
//		}
		
		
		ibMarketCtrl.reqPositions(new IPositionHandler() {
			
			@Override
			public void position(String account, Contract contract, double position, double avgCost) {
				positions.add(new Position(contract,position));
			}
			
			@Override
			public void positionEnd() {

				System.out.println("Closing positions....");
				for (final Position p : positions) {
					
					if (p.position != 0) {
						if (p.position < 0) {
							
							if (p.contract.exchange().equals("NYSE") || p.contract.exchange().equals("NASDAQ")) {
								
								System.out.println(
										"Buying " + p.position + 
										" of " + p.contract.symbol() + 
										"    (" + p.contract.currency() + ")" +
										"    (" + p.contract.secType().name()  + ")" +
										" at (" + p.contract.exchange()  + ")"
									);
	
								Order order = new Order();
								
								order.orderType(OrderType.MKT);
								order.action(Action.BUY);
								order.totalQuantity(Math.abs(p.position));
								
								p.contract.exchange("SMART");
								
								ibMarketCtrl.placeOrModifyOrder(p.contract, order, new IOrderHandler() {
									
									@Override
									public void orderState(OrderState orderState) {
										System.out.println(p.contract.symbol() + " orderState.status=" + orderState.status().name());
									}
									
									@Override
									public void handle(int errorCode, String errorMsg) {
										System.out.println(
												"Error:" + p.contract.symbol() + 
												" " + errorCode + " " + errorMsg + " " +
												"Buying " + p.position + 
												" of " + p.contract.symbol() + 
												"    (" + p.contract.currency() + ")" +
												"    (" + p.contract.secType().name()  + ")" +
												" at (" + p.contract.exchange()  + ")"
											);
									}

									@Override
									public void orderStatus(OrderStatus status,
											double filled, double remaining,
											double avgFillPrice, long permId,
											int parentId, double lastFillPrice,
											int clientId, String whyHeld) {
										// TODO implement this
										
									}
								});
							}
							else {
								System.out.println("Skipping orders on " + p.contract.exchange());
							}
						}
						else {
							System.out.println("Selling " + p.position + " of " + p.contract.symbol() + " at " + p.contract.exchange());
						}
					}
				}
				System.out.println("Done closing positions....");
			}
			
		});
		
		
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(1);
		
	}
	
	
	private boolean is_expired(IBNewContract c) {
		// System.out.println("comparing " + DateTools.getTodayAsStrNoDashes() + " with " + c.expiry());
		return DateTools.getTodayAsStrNoDashes().compareTo(c.lastTradeDateOrContractMonth()) > 0;
	}

	
	// IConnectionHandler 
	@Override
	public void connected() {
		
		System.out.println("Connected!");

		System.out.println("Sleeping 5s....zzzz");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done sleeping....!!!");
		
	}

	@Override
	public void disconnected() {
		System.out.println("Disconnected");
	}

	
	private boolean eurofarm = false;
	private boolean usfarm_us = false;
	private boolean usfarm = false;
	
	private boolean requested = false;
	
	public void market_data_farm_connection(final boolean connected, final String farm_name) {
		
		switch (farm_name) {
			case "eurofarm":
				eurofarm = connected;
			break;
			case "usfarm.us":
				usfarm_us = connected;
			break;
			case "usfarm":
				usfarm = connected;
			break;
		}
		System.out.println(farm_name + (connected ? " IS UP AND RUNNING" : " IS DISCONNECTED"));
		
		if (!requested /* eurofarm && usfarm_us && usfarm */) {
			
			requested = true;
			
			/**
			 * 
			 */
//			int count = 0;
//			for (final IBContract c : contracts) {
//
//				if (c.currency().equals("SEK")) {
//					if (count++ < 80) {
//						System.out.println("Requesting market for " + c.getID() + " REQID IS:" + ibMarketCtrl.getNextValidReqId());
//						
//						MarketRequest mr = new MarketRequest(ibMarketCtrl, c /*, done_handler */);
//						mr.addSubscriber(this);
//						requests.put(mr.getReqId(), mr);
//						mr.reqTopMktData(false);
//			
//			//			HistoricalDataRequest hr = new HistoricalDataRequest(ibMarketCtrl, c);
//			//			requests.put(mr.getReqId(), hr);
//			//			hr.reqHistoricalData(); // TODO: Add arguments
//					}
//				}
//			}
			
			ExecutionsRequest er = new ExecutionsRequest(ibMarketCtrl);
			er.addSubscriber(this);
			requests.put(er.getReqId(), er);
			er.reqExecutions();
			
		}
	}

	private boolean hdms_eurofarm = false;
	private boolean hdms_usfarm_us = false;
	private boolean hdms_usfarm = false;
	
	public void HDMS_data_farm_connection(final boolean connected, final String farm_name) {
		switch (farm_name) {
			case "eurofarm":
				hdms_eurofarm = connected;
			break;
			case "usfarm.us":
				hdms_usfarm_us = connected;
			break;
			case "usfarm":
				hdms_usfarm = connected;
			break;
		}
		System.out.println(farm_name + (connected ? " IS UP AND RUNNING" : " IS DISCONNECTED"));
	}

	
	
	@Override
	public void accountList(ArrayList<String> list) {
		System.out.println("AccountList:" + list.toString());
	}

	@Override
	public void error(Exception e) {
		System.out.println("Exception:" + e.getLocalizedMessage());
	}

	@Override
	public void message(int id, int errorCode, String errorMsg) {
		if (id == -1) {
			System.out.println(errorCode + ":" + errorMsg);
			switch (errorCode) {
				case 2104:
					String message1_part[] = errorMsg.split(":");
					if (message1_part[0].equals("Market data farm connection is OK")) {
						market_data_farm_connection(true, message1_part[1]);
					}
					else {
						market_data_farm_connection(false, message1_part[1]);
					}
				break;
				case 2106:
					String message2_part[] = errorMsg.split(":");
					if (message2_part[0].equals("HMDS data farm connection is OK")) {
						HDMS_data_farm_connection(true, message2_part[1]);
					}
					else {
						HDMS_data_farm_connection(false, message2_part[1]);
					}
				break;
			}
		}
		else {
			// Lookup Request Object
			
			Request r = requests.get(id);
			if (r != null) {
				r.error(errorCode, errorMsg);
			}
		
			// System.out.println("XXXXXXXXXX Error: " + id + " " + errorCode + " " + errorMsg);
		}
	}

	@Override
	public void show(String string) {
		System.out.println("show:" + string);
	}

	// ILogger
	
	static String logmsg = ""; // System.out.println(valueOf);
	@Override
	public void log(String valueOf) {
//		if (valueOf.equals("\n")) {
//			System.out.println("log:<" + logmsg + ">");
//			logmsg = "";
//		}
//		else {
//			logmsg += valueOf;
//		}
	}

	@Override
	public void updated(Request r) {
		if (r instanceof MarketRequest) {
			MarketRequest mr = (MarketRequest) r;
			adb.updateMarketTable(mr.getContract(), mr.getBidQty(), mr.getBid(), mr.getAsk(), mr.getAskQty(), mr.getLast(), mr.getLastQty());
		}
	}
}
