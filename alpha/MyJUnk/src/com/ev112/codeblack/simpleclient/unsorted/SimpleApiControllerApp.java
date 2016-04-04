package com.ev112.codeblack.simpleclient.unsorted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.Order;
import com.ib.client.OrderType;
import com.ib.client.TickType;
import com.ib.client.Types;
import com.ib.client.Types.Action;
import com.ib.client.Types.MktDataType;
import com.ib.client.Types.TimeInForce;
import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController.IAccountHandler;
import com.ib.controller.ApiController.IConnectionHandler;
import com.ib.controller.ApiController.IContractDetailsHandler;
import com.ib.controller.ApiController.ITopMktDataHandler;
import com.ib.controller.Position;

public class SimpleApiControllerApp implements IConnectionHandler, ILogger {

	SimpleApiController ctrl;
	OrderManager om;
	
	private final Contract m_contract = new Contract();
	private final Order m_order = new Order();
	
	private String paper_account = "DU192885";
	private String ctc_exchange = "SFB";
	private String ctc_symbol = "SAND";
	private String ctc_currency = "SEK";

	
	/**
	 * Contracts
	 * 
	 *
	 */
	
	private class IBContract extends Contract {
		public void updated(Contract contract) {
			System.out.println("--------- CONTRACT ---------");
			System.out.println(contract.toString());
			System.out.println("------- END OF CONTRACT -------");
		}
	}
	private Map<String, IBContract> ibcontracts = new HashMap<String,IBContract>();
	
	
	private void requestContractDetails(String symbol, String curr, String exch, Types.SecType type) {
		
		IBContract c = new IBContract();
		c.symbol(symbol);
		c.currency(curr);
		c.exchange(exch);
		c.secType(type);
		
		ibcontracts.put(symbol,c);
		
		ctrl.reqContractDetails(c, new IContractDetailsHandler() {
			@Override
			public void contractDetails(ArrayList<ContractDetails> list) {
				for (ContractDetails ncd : list) {
					String symbol = ncd.contract().symbol();
					IBContract cc = ibcontracts.get(symbol);
					
					cc.updated(ncd.contract());
				}
			}
		});
	}

	
	/**
	 * Prices
	 * 
	 *
	 */
	
	
	public interface IPriceHandler {
		public void priceUpdate(Contract contract, TickType tickType, double price);
	}
	
	private void requestPrices(final String symbol, final String curr, final String exch, final Types.SecType type, final IPriceHandler handler) {
	
		final Contract contract = new Contract();
		contract.symbol(symbol);
		contract.secType(type);
		contract.exchange(exch);
		contract.currency(curr);
		
		ctrl.reqTopMktData(contract, "", false, new ITopMktDataHandler() {
			@Override
			public void tickString(TickType tickType, String value) {
			}
	
			@Override
			public void tickSnapshotEnd() {
			}
	
			@Override
			public void tickSize(TickType tickType, int size) {
				// System.out.println("tickSize");
			}
	
			
			@Override
			public void tickPrice(TickType tickType, double price, int canAutoExecute) {
				
				switch (tickType) {
					case BID:
						handler.priceUpdate(contract, tickType, price);
					break;
					case ASK:
						handler.priceUpdate(contract, tickType, price);
					break;
					case LAST:
						handler.priceUpdate(contract, tickType, price);
					break;
					
					default:
						System.out.println("UNHANDLED Field, Price update in " + symbol + ":" + tickType.name() + " " + price);
					break;
					
					
/*					BID,
					ASK,
					LAST,
					HIGH,
					LOW,
					VOLUME,
					CLOSE,
					OPEN,
					AVG_VOLUME,
					OPEN_INTEREST,
					BID_EXCH,					// string
					ASK_EXCH,					// string
					AUCTION_VOLUME,
					AUCTION_PRICE,
					AUCTION_IMBALANCE,
					MARK_PRICE,
					LAST_TIMESTAMP,				// string
					SHORTABLE,
					FUNDAMENTAL_RATIOS,			// string
					RT_VOLUME,					// string
					HALTED,
					BID_YIELD,
					ASK_YIELD,
					LAST_YIELD,
					CUST_OPTION_COMPUTATION,
					TRADE_COUNT,
					TRADE_RATE,
					VOLUME_RATE,
					LAST_RTH_TRADE,
					RT_HISTORICAL_VOL; */
					
				}
			}
	
			@Override
			public void marketDataType(MktDataType marketDataType) {
				System.out.println("marketDataType");
			}
		});
	}
	
	/**
	 * Accounts
	 * 
	 *
	 */
	
	private void requestAccountUpdates(String account) {
		/**
		 * 
		 */
		ctrl.reqAccountUpdates(true, account, new IAccountHandler() {
			@Override
			public void updatePortfolio(Position position) {
				System.out.println("ACCOUNT: Position in " + position.contract().symbol() + " is now " + position.position());
			}
			@Override
			public void accountValue(String account, String key, String value, String currency) {
				if (key.equals("ACCOUNT: NetLiquidation")) {
					System.out.println("Account:" + account + " key;" + key + " value:" + value + " curr:" + currency);
				}
			}
			@Override
			public void accountTime(String timeStamp) {
				// System.out.println("ACCOUNT: Time:" + timeStamp);
			}
			@Override
			public void accountDownloadEnd(String account) {
				// System.out.println("accountDownloadEnd(String account)");
			}
		});
	}
	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	public SimpleApiControllerApp() {
		
		ctrl = new SimpleApiController(this, this, this);
		ctrl.connect("192.168.2.30", 6667, /*clientid:*/0, "");
		
//		AccountSummaryTag tags[] = {AccountSummaryTag.NetLiquidation};
		
		/**
		 * 
		 */
//		app.reqAccountSummary("All", tags, new IAccountSummaryHandler() {
//			@Override
//			public void accountSummaryEnd() {
//				System.out.println("Account summary END");
//			}
//			
//			@Override
//			public void accountSummary(String account, AccountSummaryTag tag, String value, String currency) {
//				System.out.println("Account summary for account:" + account);
//			}
//		});
//		
//		
//		requestContractDetails("ES","USD","GLOBEX",Types.SecType.IND);
//		requestContractDetails("OMXS30","SEK","OMS",Types.SecType.IND);
//		
//		requestAccountUpdates(paper_account);
//		
//		
//		IPriceHandler handler = new IPriceHandler() {
//			@Override
//			public void priceUpdate(NewContract contract, NewTickType tickType, double price) {
//				
//				switch (tickType) {
//					case BID:
//						System.out.println("BID update in " + contract.symbol() + " " + price);
//					break;
//					case ASK:
//						System.out.println("ASK update in " + contract.symbol() + " " + price);
//					break;
//					case LAST:
//						System.out.println("LAST update in " + contract.symbol() + " " + price);
//					break;
//				}
//				
//			}
//		};
//		requestPrices(ctc_symbol, ctc_currency, ctc_exchange, Types.SecType.STK, handler);
//		requestPrices("OMXS30", "SEK", "OMS", Types.SecType.IND, handler);

		
		// Order Manager
		om = new OrderManager(ctrl);
		om.start();
		

//		System.out.println("Sleeping 10s....");
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// System.out.println("Send bracket order disabled - change source code to enable!");

/*
		Scanner scan= new Scanner(System.in);
		String text = "";
		while (!text.equals("Quit")) {
			
			if (text.equals(" ")) {
				om.listOrders();
			}
			else if (text.equals("B")) { 
				sendBracketOrder(89.65, "SFB", "SEK", "SAND");
			}
			else if (text.equals("X")) { 
				// delete all orders
			}
			else {
				om.listOrders();
			}
			
			System.out.println("Command:");
			
		    //For string
		    text= scan.nextLine();
//		    System.out.println(text);
//		    //for int
//		    int num= scan.nextInt();
//		    System.out.println(num);
		};
		
*/		
	}


	private void sendBracketOrder(Double hit_price, String exch, String curr, String symbol) {
		
		final Contract contract = new Contract();
		final Order base_order = new Order();
		
		contract.exchange(exch);
		contract.currency(curr);
		contract.symbol(symbol);
		contract.secType(Types.SecType.STK);
		
		base_order.account(paper_account);
		base_order.action(Action.BUY);
		base_order.totalQuantity(100);
		// m_order.displaySize(1);
		base_order.orderType(OrderType.LMT);
		base_order.lmtPrice(hit_price);
		base_order.tif(TimeInForce.DAY);
		base_order.transmit(false);

		System.out.println("PLACE-OR-MODIFY-ORDER (MAIN ORDER)");
		ctrl.placeOrModifyOrder(contract, base_order, null);
		
		Order profit_order = new Order();
		profit_order.account(paper_account);
		profit_order.action(Action.SELL);
		profit_order.totalQuantity(100);
		// m_order.displaySize(1);
		profit_order.orderType(OrderType.LMT);
		profit_order.lmtPrice(hit_price+0.05);
		profit_order.tif(TimeInForce.DAY);
		profit_order.parentId(base_order.orderId());
		profit_order.transmit(false);

		System.out.println("PLACE-OR-MODIFY-ORDER (PROFIT ORDER)");
		ctrl.placeOrModifyOrder(contract, profit_order, null);
		
		Order stop_order = new Order();
		stop_order.account(paper_account);
		stop_order.action(Action.SELL);
		stop_order.totalQuantity(100);
		// m_order.displaySize(1);
		stop_order.orderType(OrderType.STP);
		stop_order.auxPrice(hit_price-0.20);
		stop_order.tif(TimeInForce.DAY);
		stop_order.parentId(base_order.orderId());
		stop_order.transmit(true);
		
		System.out.println("PLACE-OR-MODIFY-ORDER (STOP ORDER)");
		ctrl.placeOrModifyOrder(contract, stop_order, null);
	}
	
	
	@Override
	public void log(String valueOf) {
		// System.out.print(valueOf);
	}

	/*
	 * IConnectionHandler overrides
	 * (non-Javadoc)
	 * @see com.ib.controller.ApiController.IConnectionHandler#connected()
	 */
	
	@Override
	public void connected() {
		System.out.println("Connected!");
	}

	@Override
	public void disconnected() {
		System.out.println("Disconnected!");
	}

	@Override
	public void accountList(ArrayList<String> list) {
		System.out.println("AccountList");
	}

	@Override
	public void message(int id, int errorCode, String errorMsg) {
		if (id != -1)
			System.out.println("Message id:" + id + " code:" + errorCode + " msg:" + errorMsg);
		else
			System.out.println("Message code:" + errorCode + " msg:" + errorMsg);
	}

	@Override
	public void error(Exception e) {
		System.out.println("Exception:" + e.getLocalizedMessage());
	}

	@Override
	public void show(String string) {
		System.out.println("Show:" + string);
	}

	
	
	public static void main(String[] args) {
		new SimpleApiControllerApp();
	}
}
