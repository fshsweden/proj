/* Copyright (C) 2013 Interactive Brokers LLC. All rights reserved.  This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package com.ev112.codeblack.pricecollector.ib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ev112.codeblack.common.instmodel.Market.MARKET_DATA_SUBSCRIPTION_TYPE;
import com.ev112.codeblack.common.ordercontroller.AdapterMarketStatus;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.ExecutionFilter;
import com.ib.client.MarketDataType;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.TagValue;

/**
 * IBMarket gives the possibility to connect to a TWS Trader Station, or an IB
 * Gateway
 * <p>
 * A IInstrumentObserver can subscribe to IInstrument via subscribeToInstrument
 * 
 * @author Peter Andersson
 * 
 */
public class IBMarket implements IMarket, EWrapper {

	private AtomicBoolean nextValidOrderIdRcvd = new AtomicBoolean();
	private AtomicBoolean connectionFailure = new AtomicBoolean();
	private static int depth_ctr=0;
	private EClientSocket market;
	private String host;
	private int port;
	private String account;
	private int client_id;
	private List<MarketStatusHandler> market_status_handlers = new ArrayList<MarketStatusHandler>();
							
	private Map<IInstrument, Contract> m_contract = new HashMap<IInstrument, Contract>();
	private Map<Integer, List<IInstrumentObserver>> m_subscriptions = new HashMap<Integer, List<IInstrumentObserver>>();

	// TODO: Map tickerId to Instrument instead!
	private Map<Integer, IInstrument> m_instruments = new HashMap<Integer, IInstrument>();

	// Maps IB field codes to our string codes
	private Map<Integer, String> ib_field_to_string = new HashMap<Integer, String>();
	
	private Integer nextValidOrderId = 0; // must be set by IB before used!
	private Integer nextReqId = 1000000;
	private AdapterMarketStatus currentAdapterMarketStatus = AdapterMarketStatus.MarketDisconnected;
	
	/**
	 * 
	 * @param host    the hostname or ipaddress of the TWS Workstation/Gateway
	 * @param port    the IP port that TWS id configured to use, normally 7496 for WS
	 */
	public IBMarket(String host, int port, int client_id, String account) {
		connectionFailure.set(false);
		this.host = host;
		this.port = port;
		this.client_id = client_id;
		this.account = account;
		initialize_codes();
	}

	/**
	 * IMarket overrides
	 */
	@Override
	public String getName() {
		return "IB";
	}

	@Override
	public boolean connect() {
		market = new EClientSocket(this);
		
		System.out.println("IBMarket connecting to host:" + host + " port:" + port + " client_id:" + client_id);
		market.eConnect(host, port, client_id);
		
		System.out.println("IBMarket waiting for Next valid order id");
		while (!nextValidOrderIdRcvd.get() && !connectionFailure.get()) {
			if (connectionFailure.get()) {
				connectionFailure.set(false); // reset flag!
				return false;
			}
			// Have we got the NEXT VALID ORDER ID?
			if (!nextValidOrderIdRcvd.get()) {
				System.out.println("WAITING ONE SECOND...");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
		System.out.println("IBMarket got Next valid order id - setting up connection");
		
		// market.reqIds(1);
		market.reqCurrentTime();
		market.reqAccountSummary(getNextReqId(), /*group*/"All", /*tags*/"NetLiquidation");
		market.reqAccountUpdates(true, account);
		market.reqExecutions(getNextReqId(), new ExecutionFilter());
		market.reqNewsBulletins(true);
		market.reqPositions();
		market.reqGlobalCancel();
		market.reqMarketDataType(MarketDataType.REALTIME);
		
		System.out.println("IBMarket har setup IB Market propertly now");
		currentAdapterMarketStatus = AdapterMarketStatus.MarketConnected;
	
		return true;
	}

	@Override
	public boolean disconnect() {
		System.out.println("Disconnecting from IB.");
		market.eDisconnect();
		currentAdapterMarketStatus = AdapterMarketStatus.MarketDisconnected;
		return true;
	}

	@Override
	public int desubscribeToInstrument(IInstrument ii, IInstrumentObserver observer) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private Integer getNextReqId() {
		return nextReqId++;
	}

	private void initialize_codes() {
		ib_field_to_string.put(0, "BID_SIZE");
		ib_field_to_string.put(1, "BID_PRICE");
		ib_field_to_string.put(2, "ASK_PRICE");
		ib_field_to_string.put(3, "ASK_SIZE");
		ib_field_to_string.put(4, "LAST_PRICE");
		ib_field_to_string.put(5, "LAST_SIZE");
		ib_field_to_string.put(6, "HIGH");
		ib_field_to_string.put(7, "LOW");
		ib_field_to_string.put(8, "VOLUME");
		ib_field_to_string.put(9, "CLOSE_PRICE");
		ib_field_to_string.put(10, "BID_OPTION_COMPUTATION");
		ib_field_to_string.put(11, "ASK_OPTION_COMPUTATION");
		ib_field_to_string.put(12, "LAST_OPTION_COMPUTATION");
		ib_field_to_string.put(13, "MODEL_OPTION_COMPUTATION");
		ib_field_to_string.put(14, "OPEN_TICK");
		ib_field_to_string.put(15, "LOW_13_WEEK");
		ib_field_to_string.put(16, "HIGH_13_WEEK");
		ib_field_to_string.put(17, "LOW_26_WEEK");
		ib_field_to_string.put(18, "HIGH_26_WEEK");
		ib_field_to_string.put(19, "LOW_52_WEEK");
		ib_field_to_string.put(20, "HIGH_52_WEEK");
		ib_field_to_string.put(21, "AVG_VOLUME");
		ib_field_to_string.put(22, "OPEN_INTEREST");
		ib_field_to_string.put(23, "OPTION_HISTORICAL_VOL");
		ib_field_to_string.put(24, "OPTION_IMPLIED_VOL");
		ib_field_to_string.put(25, "OPTION_BID_EXCH");
		ib_field_to_string.put(26, "OPTION_ASK_EXCH");
		ib_field_to_string.put(27, "OPTION_CALL_OPEN_INTEREST");
		ib_field_to_string.put(28, "OPTION_PUT_OPEN_INTEREST");
		ib_field_to_string.put(29, "OPTION_CALL_VOLUME");
		ib_field_to_string.put(30, "OPTION_PUT_VOLUME");
		ib_field_to_string.put(31, "INDEX_FUTURE_PREMIUM");
		ib_field_to_string.put(32, "BID_EXCH");
		ib_field_to_string.put(33, "ASK_EXCH");
		ib_field_to_string.put(34, "AUCTION_VOLUME");
		ib_field_to_string.put(35, "AUCTION_PRICE");
		ib_field_to_string.put(36, "AUCTION_IMBALANCE");
		ib_field_to_string.put(37, "MARK_PRICE");
		ib_field_to_string.put(38, "BID_EFP_COMPUTATION");
		ib_field_to_string.put(39, "ASK_EFP_COMPUTATION");
		ib_field_to_string.put(40, "LAST_EFP_COMPUTATION");
		ib_field_to_string.put(41, "OPEN_EFP_COMPUTATION");
		ib_field_to_string.put(42, "HIGH_EFP_COMPUTATION");
		ib_field_to_string.put(43, "LOW_EFP_COMPUTATION");
		ib_field_to_string.put(44, "CLOSE_EFP_COMPUTATION");
		ib_field_to_string.put(45, "LAST_TIMESTAMP");
		ib_field_to_string.put(46, "SHORTABLE");
		ib_field_to_string.put(47, "FUNDAMENTAL_RATIOS");
		ib_field_to_string.put(48, "RT_VOLUME");
		ib_field_to_string.put(49, "HALTED");
		ib_field_to_string.put(50, "BIDYIELD");
		ib_field_to_string.put(51, "ASKYIELD");
		ib_field_to_string.put(52, "LASTYIELD");
		ib_field_to_string.put(53, "CUST_OPTION_COMPUTATION");
		ib_field_to_string.put(54, "TRADE_COUNT");
		ib_field_to_string.put(55, "TRADE_RATE");
		ib_field_to_string.put(56, "VOLUME_RATE");
		ib_field_to_string.put(233, "RT_VOLUME");
		/* Legal ones for (STK) are */
		ib_field_to_string.put(100, "Option Volume");
		ib_field_to_string.put(101, "Option Open Interest");
		ib_field_to_string.put(104, "HVOLAT30");
		ib_field_to_string.put(105, "Average Opt Volume");
		ib_field_to_string.put(106, "Option Implied Volatility");
		ib_field_to_string.put(107, "Close Implied Volatility");
		ib_field_to_string.put(125, "Bond analytic data");
		ib_field_to_string.put(165, "Misc. Stats");
		ib_field_to_string.put(166, "CScreen");
		ib_field_to_string.put(225, "Auction");
		ib_field_to_string.put(232, "221(Mark Price)");
		ib_field_to_string.put(233, "RTVolume");
		ib_field_to_string.put(236, "inventory");
		ib_field_to_string.put(258, "Fundamentals"); // res: 47
		ib_field_to_string.put(291, "(Close Implied Volatility)");
		ib_field_to_string.put(293, "(TradeCount)");
		ib_field_to_string.put(294, "(TradeRate)");
		ib_field_to_string.put(295, "(VolumeRate)");
		ib_field_to_string.put(318, "(LastRTHTrade)");
		ib_field_to_string.put(370, "(ParticipationMonitor)");
		ib_field_to_string.put(370, "(ParticipationMonitor)");
		ib_field_to_string.put(377, "(CttTickTag)");
		ib_field_to_string.put(377, "(CttTickTag)");
		ib_field_to_string.put(381, "(IB Rate)");
		ib_field_to_string.put(384, "(RfqTickRespTag)");
		ib_field_to_string.put(384, "(RfqTickRespTag)");
		ib_field_to_string.put(387, "(DMM)");
		ib_field_to_string.put(388, "(Issuer Fundamentals)");
		ib_field_to_string.put(391, "(IBWarrantImpVolCompeteTick)");
		ib_field_to_string.put(407, "(FuturesMargins)");
		ib_field_to_string.put(411, "(Real-Time Historical Volatility)");
		ib_field_to_string.put(428, "(Monetary Close Price)");
		ib_field_to_string.put(439, "(MonitorTickTag)");
		ib_field_to_string.put(439, "(MonitorTickTag)");
		ib_field_to_string.put(456, "(IBDividends)"); // res: 59
		ib_field_to_string.put(459, "(RTCLOSE)");
		ib_field_to_string.put(460, "(Bond Factor Multiplier)");
		ib_field_to_string.put(475, "(HVOLAT10)");
		ib_field_to_string.put(476, "(HVOLAT50)");
		ib_field_to_string.put(477, "(HVOLAT75)");
		ib_field_to_string.put(478, "(HVOLAT100)");
		ib_field_to_string.put(479, "(HVOLAT150)");
		ib_field_to_string.put(480, "(HVOLAT200)");
		ib_field_to_string.put(499, "(Fee and Rebate Rate)");
	}

	/**
	 * 
	 */
	@Override
	public int subscribeToInstrument(IInstrument ii, IInstrumentObserver observer) {

		IBInstrument ibi = (IBInstrument) ii;

		Contract con = new Contract();
		con.secType(ii.getStr("SECTYPE"));

		Integer tickerId = ibi.getTickerId();

		switch (con.secType()) {
			
			case IND:
				con.symbol(ii.getStr("SYMBOL"));
				con.currency(ii.getStr("CURRENCY"));
				con.exchange(ii.getStr("EXCHANGE"));
				System.out.println(tickerId + 
						" Subscribing to IND Symbol:" + con.symbol() + 
						" Currency:" + con.currency() + 
						" Exchange:" + con.exchange() + 
						" secType:" + con.secType());
			break;
			
			case STK:
				con.symbol(ii.getStr("SYMBOL"));
				// con.m_localSymbol = ii.getStr("SYMBOL");
				con.currency(ii.getStr("CURRENCY"));
				con.exchange(ii.getStr("EXCHANGE"));
				System.out.println(tickerId + 
						" Subscribing to STK Symbol:" + con.symbol() + 
						" Currency:" + con.currency() + 
						" Exchange:" + con.exchange() + 
						" secType:" + con.secType());
			break;
			
			case FUT:
				// For futures, the symbol is the underlying
				con.localSymbol(ii.getStr("LOCALSYMBOL"));
				if (con.localSymbol().equals("")) {
					con.symbol(ii.getStr("UNDERLYING"));
					con.lastTradeDateOrContractMonth(ii.getStr("EXPIRY"));
				}
				con.currency(ii.getStr("CURRENCY"));
				con.exchange(ii.getStr("EXCHANGE"));
				con.multiplier(ii.getStr("MULTIPLIER"));  // NEW - BLANK IF NOT USED
	
				if (con.localSymbol().equals("")) {
					System.out.println( 
							tickerId +
							" Subscribing to FUT Symbol:" + ii.getStr("SYMBOL") + 
							" Symbol:" + con.symbol() + 
							" Currency:" + con.currency() + 
							" Exchange:" + con.exchange() + 
							" Expiry:" + con.lastTradeDateOrContractMonth() + 
							" secType:" + con.secType());
				}
				else {
					System.out.println( 
						tickerId +
						" Subscribing to FUT Symbol:" + 
						" LocalSymbol :" + con.localSymbol() + 
						" Currency:" + con.currency() + 
						" Exchange:" + con.exchange() + 
						" secType:" + con.secType());
				}
			break;
			
			default:
				System.out.println("Unknown SECTYPE:" + con.secType());
			break;
		}

		
		if (ii.getMarketDataSubscriptionType() == MARKET_DATA_SUBSCRIPTION_TYPE.L2 && depth_ctr < 3) {
			depth_ctr = depth_ctr + 1;
			System.out.println("Requesting DEPTH 5 for tickerId " + tickerId);
			market.reqMktDepth(tickerId, con, 5, new ArrayList<TagValue>());
		}
		else {
			System.out.println("Requesting TOP MARKET for tickerId " + tickerId);
			market.reqMktData(tickerId, con, "233", false /* not just a snapshot! */, new ArrayList<TagValue>());
		}
			

		// NOW KEEP TRACK OF THIS SUBSCRIPTION

		if (m_subscriptions.get(tickerId) == null) {
			m_subscriptions.put(tickerId, new ArrayList<IInstrumentObserver>());
		}
		m_subscriptions.get(tickerId).add(observer);

		m_instruments.put(tickerId, ii);
		/*
		 * remember connection between Contract (IB Specific) and IInstrument
		 * (Generic)
		 */
		m_contract.put(ii, con);
		return tickerId;
	}

	/*
	 * Overridden EWrapper methods
	 */

	@Override
	public void nextValidId(int orderId) {
		System.out.println("nextValidId is *** " + orderId + " ***");
		this.nextValidOrderId = orderId;
		
		nextValidOrderIdRcvd.set(true);
	}

	@Override
	public void error(Exception e) {
		distribute_error(-1, "Exception" + (e != null ? ":" + e.getLocalizedMessage() : ""));
	}

	@Override
	public void error(int tickerId, int errorCode, String errorMsg) {

		
		List<IInstrumentObserver> observers = m_subscriptions.get(tickerId);
		if (observers != null) {
			for (IInstrumentObserver o : observers) {
				// ????
			}
		}
		
		
		
		IInstrument ii = m_instruments.get(tickerId);
		if (ii != null) {
			distribute_error(errorCode, "ticker:" + tickerId + " " + ii.symbol() + " : " + errorMsg);
		} else {
			switch(errorCode) {
				case 1100:
					System.out.println("DISCONNECT: " + errorMsg);
				break;
				case 2104:
				case 2106:
				case 2119: /* Market data farm is connecting: */
					logInfoMsg(errorMsg);
				break;
				case 502:
					System.out.println("Error connecting to TWS. ErrorCode=" + errorCode + " " + errorMsg);
					connectionFailure.set(true);
				break;
				default:
					System.out.println("ErrorCode: " + errorCode + " Msg:" + errorMsg);
				break;
			}
			
			distribute_error(errorCode,errorMsg);
		}
	}
	
	// Hack to avoid mentioning method "error" in logfile - easier to grep... 
	private void logInfoMsg(String str) {
		System.out.println(str);
	}

	@Override
	public void connectionClosed() {
		//mylog("event: connectionClosed");
		distribute_message(0, "event: connectionClosed");
	}

	@Override
	public void error(String str) {
		// mylog("Generic error() :" + str);
		distribute_error(0,"Generic error() :" + str);
	}

	@Override
	public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {

		// tickPrice() handles BID, ASK, LAST etc
		
		IBTickType tt = IBTickType.fromInt(field);
		if (tt == IBTickType.UNKNOWN) {
			
		}
		else {
			IInstrument ii = m_instruments.get(tickerId);
	
			// old value:
			Double dbl = ii.getDbl(ib_field_to_string.get(field));
			if (dbl != price) {
				
				// System.out.println("Changed from - " + dbl + " to " + price);
				ii.setField(IBTickType.TIMESTAMP, System.currentTimeMillis());
				ii.setField(tt, price);
	
				List<IInstrumentObserver> observers = m_subscriptions.get(tickerId);
				for (IInstrumentObserver io : observers) {
					// TODO: Translate IB Tick Types to Alpha Tick Types
					io.instrumentFieldUpdated(ii, tt);
				}
			}
			else {
				// System.out.println("Same value - " + dbl + " " + price + " so no update!");
			}
		}
	}

	@Override
	public void tickSize(int tickerId, int field, int size) {

		// tickSize() handles BIDSIZE, ASKSIZE, LASTSIZE etc
		
		IInstrument ii = m_instruments.get(tickerId);
		
		long cur = System.currentTimeMillis();

		IBTickType tt = IBTickType.fromInt(field);
		
		if (tt == IBTickType.UNKNOWN) {
			
		}
		else {
			Integer sz = ii.getInt(ib_field_to_string.get(field));
			if (sz != size)
			{
				ii.setField(IBTickType.TIMESTAMP, cur);
				ii.setField(ib_field_to_string.get(field), size);
			}
			else {
				// System.out.println("Same value - " + size + " so no update!");
			}
	
			List<IInstrumentObserver> observers = m_subscriptions.get(tickerId);
			for (IInstrumentObserver io : observers) {
				io.instrumentFieldUpdated(ii, tt);
			}
		}
	}

	@Override
	public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta,
			double undPrice) {
	}

	@Override
	public void tickGeneric(int tickerId, int tickType, double value) {
		// mylog("tickGeneric:" + tickerId + " : " + tickType + " : " + value);
		// avoid printing alot of zeroes....
	}

	@Override
	public void tickString(int tickerId, int tickType, String value) {
		IInstrument ii = m_instruments.get(tickerId);

		// mylog("tickString:  id" + tickerId + "(" + ii.symbol() +
		// ") : type:(" + tickType + ") value:(" + value + ")");

		String values[] = value.split(";");

		switch (tickType) {
		case 45:

			break;
		case 48:

			Integer old_volume;
			try {
				old_volume = ii.getInt("TOTAL_VOLUME");
			} catch (Exception ex) {
				old_volume = 0;
			}

			try {
				Double price, vwap;
				Integer size, volume;
				long trade_time;

				price = Double.parseDouble(values[0]);
				size = Integer.parseInt(values[1]);
				trade_time = Long.parseLong(values[2]);
				volume = Integer.parseInt(values[3]);
				vwap = Double.parseDouble(values[4]);

				// ii.setField("TIMESTAMP", trade_time); // Old way, use IB timestamp
				long cur = System.currentTimeMillis();
				ii.setField("TIMESTAMP", cur);

				if ((old_volume + size != volume) && old_volume > 0) {
					System.out.println("Volumes doesnt match for !" + ii.symbol() + " Saved volume was " + old_volume + " trade was " + size + " and new volume is " + volume);
				}

				ii.setField("LAST_TRADE_PRICE", price);
				ii.setField("LAST_TRADE_SIZE", size);
				ii.setField("LAST_TRADE_TIME", values[2]);
				ii.setField("TOTAL_VOLUME", volume);
				ii.setField("VWAP", vwap);
				ii.setField("SINGLE_TRADE", values[5]);

				List<IInstrumentObserver> observers = m_subscriptions.get(tickerId);
				for (IInstrumentObserver io : observers) {
					io.instrumentTraded(ii, values[0], values[1], values[2], trade_time);
				}
			} catch (NumberFormatException e) {
				// CoreLogger.getInstance().log("Price or Size was 0, so this was no trade...");
			} catch (Exception ex) {
				System.out.println("Exception:" + ex.getLocalizedMessage());
			}

			break;
		}

	}

	@Override
	public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureExpiry,
			double dividendImpact, double dividendsToExpiry) {

	}

	@Override
	public void orderStatus(int orderId, String status, double filled, double remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
		System.out.println("orderstatus: id:" + orderId + " status:" + status + " filled:" + filled + " rem:" + remaining + 
				" avgpx:" + avgFillPrice + " permId:" + permId + " parent:" + parentId + 
				" lastFill:" + lastFillPrice + " client:" + clientId + " why:" + whyHeld);
	}

	@Override
	public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
		System.out.println("Open Order - orderid:" + orderId + " Contract:" + contract.toString() + " order:" + order.toString() + " state:" + orderState.toString());
		// orders << order
	}

	@Override
	public void openOrderEnd() {
		// mylog("END OF OPEN ORDERS");
		// order_subscribers.cb(orders);
	}

	@Override
	public void updateAccountValue(String key, String value, String currency, String accountName) {
		// mylog("DEBUG updateAccountValue: " + key + " : " + value + " curr:" + currency + " acct:" + accountName);
	}

	@Override
	public void updateAccountTime(String timeStamp) {
		System.out.println("Acct Time:" + timeStamp);
	}

	@Override
	public void accountDownloadEnd(String accountName) {
	}

	@Override
	public void contractDetails(int reqId, ContractDetails contractDetails) {
	}

	@Override
	public void bondContractDetails(int reqId, ContractDetails contractDetails) {
	}

	@Override
	public void contractDetailsEnd(int reqId) {
	}

	@Override
	public void execDetails(int reqId, Contract contract, Execution execution) {
		// logger.debug("Trade in " + contract.m_symbol + " Price:" + execution.m_avgPrice + " Volume:"+ execution.m_cumQty);
	}

	@Override
	public void execDetailsEnd(int reqId) {
		// mylog("execDetailsEnd");
	}

	@Override
	public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) {
		System.out.println("--- updateMktDepth " + tickerId + " ---");
	}

	@Override
	public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size) {
		System.out.println("--- updateMktDepthL2 ---");
	}

	@Override
	public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
		System.out.println("--- updateNewsBulletin ---");
		System.out.println("MSGID:" + msgId + " Type:" + msgType + " MSG:" + message + " FROM:" + origExchange);
	}

	@Override
	public void managedAccounts(String accountsList) {
	}

	@Override
	public void receiveFA(int faDataType, String xml) {
	}

	@Override
	public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
	}

	@Override
	public void scannerParameters(String xml) {
	}

	@Override
	public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
	}

	@Override
	public void scannerDataEnd(int reqId) {
	}

	@Override
	public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {
	}

	@Override
	public void currentTime(long time) {
//		mylog("************** current time: " + time);
//		long tNow = System.currentTimeMillis() / 1000;
//		mylog("************** now is: " + tNow);
//		long offset = tNow - time;
//		mylog("************** offset is: " + offset);
	}

	@Override
	public void fundamentalData(int reqId, String data) {
	}

	@Override
	public void tickSnapshotEnd(int reqId) {
	}

	@Override
	public void marketDataType(int reqId, int marketDataType) {
		System.out.println("--- marketDataType ---");
	}

	@Override
	public void commissionReport(CommissionReport commissionReport) {
	}

	@Override
	public void position(String account, Contract contract, double pos, double avgCost) {
		// logger.debug("Position: Account " + account + " Symbol:" + contract.m_symbol + " Position:" + pos + " Avg Px:" + avgCost);
	}

	@Override
	public void positionEnd() {
		System.out.println("--- positionEnd ---");
	}

	@Override
	public void accountSummary(int reqId, String account, String tag, String value, String currency) {
		System.out.println("--- accountSummary ---");
	}

	@Override
	public void accountSummaryEnd(int reqId) {
		System.out.println("--- accountSummaryEnd ---");
	}

	@Override
	public void addMarketStatusHandler(MarketStatusHandler handler) {
		market_status_handlers.add(handler);
	}
	
	private void distribute_error(int code, String message) {
		for (MarketStatusHandler h : market_status_handlers) {
			h.information(code, message);
		}
	}
	
	private void distribute_message(int code, String message) {
		for (MarketStatusHandler h : market_status_handlers) {
			h.message(code, message);
		}
	}

	@Override
	public void updatePortfolio(Contract contract, double position,
			double marketPrice, double marketValue, double averageCost,
			double unrealizedPNL, double realizedPNL, String accountName) {
		// TODO implement this
		
	}

	@Override
	public void deltaNeutralValidation(int reqId,
			DeltaNeutralContract underComp) {
		// TODO implement this
		
	}

	@Override
	public void verifyMessageAPI(String apiData) {
		// TODO implement this
		
	}

	@Override
	public void verifyCompleted(boolean isSuccessful, String errorText) {
		// TODO implement this
		
	}

	@Override
	public void verifyAndAuthMessageAPI(String apiData, String xyzChallange) {
		// TODO implement this
		
	}

	@Override
	public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {
		// TODO implement this
		
	}

	@Override
	public void displayGroupList(int reqId, String groups) {
		// TODO implement this
		
	}

	@Override
	public void displayGroupUpdated(int reqId, String contractInfo) {
		// TODO implement this
		
	}

	@Override
	public void connectAck() {
		// TODO implement this
		
	}

	@Override
	public void positionMulti(int reqId, String account, String modelCode,
			Contract contract, double pos, double avgCost) {
		// TODO implement this
		
	}

	@Override
	public void positionMultiEnd(int reqId) {
		// TODO implement this
		
	}

	@Override
	public void accountUpdateMulti(int reqId, String account, String modelCode,
			String key, String value, String currency) {
		// TODO implement this
		
	}

	@Override
	public void accountUpdateMultiEnd(int reqId) {
		// TODO implement this
		
	}
}
