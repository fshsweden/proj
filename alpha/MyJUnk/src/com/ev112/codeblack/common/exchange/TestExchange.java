package com.ev112.codeblack.common.exchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.ev112.codeblack.common.configuration.Configuration;
import com.ev112.codeblack.common.instmodel.Instrument;
import com.ev112.codeblack.common.instmodel.InstrumentModel;
import com.ib.controller.ApiController.IConnectionHandler;
import com.ib.controller.NewTickType;
import com.ib.controller.OrderStatus;
import com.ib.controller.Types.Action;

public class TestExchange implements IConnectionHandler {
	
	private Configuration config;
	private InstrumentModel im;
	private IB ib;
	private Map<String, Order> my_orders = new TreeMap<String, Order>();
	private Map<String, TopOfMarket> topOfMarket = new HashMap<String, TopOfMarket>();
	
	
	public TestExchange() {
		
		config = new Configuration("PETER");
		im = config.getInstrumentModel();
		
		ib = new IB();
		
		KeyValues kv = new KeyValues();
		kv.put("HOST", "192.168.0.198");
		kv.put("PORT", "6661");
		kv.put("ACCOUNT", "DU192885");
		kv.put("CLIENTID", "131");
		
		if (!ib.connect(kv, this)) {
			System.out.println("Failed to connect!");
			System.exit(1);
		}
		
//		MarketStatusHandler msh = new MarketStatusHandler() {
//			@Override
//			public void message(int code, String msg) {
//				
//			}
//			@Override
//			public void information(int code, String msg) {
//				
//			}
//		};
	}
	
	@Override
	public void connected() {
		
		Instrument instrument = im.getInstrument("AAPL");
		
		MarketPriceEventHandler handler = new MarketPriceEventHandler() {
			@Override
			public void sizeUpdated(Integer tickerId, Instrument instr, NewTickType field, Integer size) {
				// System.out.println("    "  + instr.getSymbol() + " sizeUpdated " + tickerId);
				
				TopOfMarket t = topOfMarket.get(instr.getSymbol());
				if (t == null) {
					t = new IBTopOfMarket();
					topOfMarket.put(instr.getSymbol(), t);
				}
				
				switch (field) {
					case BID_SIZE:
						t.setBidQty(size);
					break;
					case ASK_SIZE:
						t.setAskQty(size);
					break;
					case LAST_SIZE:
						t.setLastQty(size);
					break;
				}
				printTopOfMarket(instr, t);
			}
			@Override
			public void priceUpdated(Integer tickerId, Instrument instr, NewTickType field, Double price, Integer canAutoExecute) {
				// System.out.println("    "  + instr.getSymbol() + " priceUpdated " + tickerId);
				TopOfMarket t = topOfMarket.get(instr.getSymbol());
				if (t == null) {
					t = new IBTopOfMarket();
					topOfMarket.put(instr.getSymbol(), t);
				}
				
				switch (field) {
					case BID:
						t.setBid(price);
					break;
					case ASK:
						t.setAsk(price);
					break;
					case LAST:
						t.setLast(price);
					break;
					
					// TODO: what about HIGH,LOW,OPEN,CLOSE??
				}
				printTopOfMarket(instr, t);
			}
			@Override
			public void instrumentTraded(Integer tickerId, Instrument instr, Double last_trade_price, Integer last_trade_size, String last_trade_time, Long timestamp, Integer volume) {
				System.out.println("    " + instr.getSymbol() + " traded! " + tickerId);
			}
		};
		
		ib.subscribeToInstrument(instrument, handler);
		
		/*
		 * 
		 * 
		 * 
		 */
		OrderEventHandler oeh = new OrderEventHandler() {
			
			@Override
			public void orderTraded(Order order) {
				System.out.println("orderTraded()");
				listOrders();
			}
			
			@Override
			public void orderModified(Order order) {
				System.out.println("orderModified()");
				my_orders.put(order.getOrderId(), order);
				listOrders();
			}
			
			@Override
			public void orderCancelled(Order order) {
				System.out.println("orderCancelled()");
				listOrders();
			}
			
			@Override
			public void orderAdded(Order order) {
				System.out.println("orderAdded() : " + order.getOrderId() + " " + order.getExchangeOrderId());
				my_orders.put(order.getOrderId(), order);
				listOrders();
			}
			
			@Override
			public void orderStatusChanged(Order order, OrderStatus status, int filled, int remaining, double avgFillPrice, long permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
				
				System.out.println("Order status changed! New status is:" + status.name());
				Order o = my_orders.get(order.getOrderId());
				
				switch (status) {
					case Cancelled:
						o.setOrderState(OrderState2.Cancelled);
					break;
					case ApiCancelled:
						break;
					case ApiPending:
						break;
					case Filled:
						if (remaining == 0)
							o.setOrderState(OrderState2.FullyTraded);
						else
							o.setOrderState(OrderState2.PartiallyTraded);
						break;
					case Inactive:
						o.setOrderState(OrderState2.Inactive);
						break;
					case PendingCancel:
						break;
					case PendingSubmit:
						break;
					case PreSubmitted:
						o.setOrderState(OrderState2.InMarket);
						break;
					case Submitted:
						o.setOrderState(OrderState2.InMarket);
						break;
					case Unknown:
						break;
					default:
						break;
				}
				
				
				// o.setExchangeOrderId(Long.toHexString(permId));

				// TODO: Set these too
				// Filled
				// Remaining
				// AvgFillPrice
				// ParentId ?
				// lastFillPrice
				// ClientId ???
				// WhyHeld
				
				listOrders();
			}
			
			@Override
			public void handleError(Order order, Integer errorCode, String errorMsg) {
				if (errorCode == 202) {
					System.out.println("Order " + order.getOrderId() + " was cancelled " + errorCode + " " + errorMsg);
					Order o = my_orders.get(order.getOrderId());
					o.setOrderState(OrderState2.Cancelled);
				}
				else {
					System.out.println("Order error " + errorCode + " " + errorMsg);
				}
				listOrders();
			}
		};
		
		ExecutionEventHandler eeh = new ExecutionEventHandler() {
			
		};
		
		ib.downloadOrders(oeh);
		
		ib.addOrderEventHandler(oeh);
		ib.addExecutionEventHandler(eeh);
		
		ib.StartListeningToLiveOrderchanges();
		ib.StartListeningToExecutions();
		
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ib.placeOrder(instrument, 1.0, 1, Action.BUY);
	}

	private void listOrders() {
		System.out.println("------------- ORDERS ---------------- ");
		for (String oid: my_orders.keySet()) {
			Order o = my_orders.get(oid);
			if (o.getOrderState() != OrderState2.Cancelled) {
				System.out.println("ID:" + o.getOrderId() + "\nOrderState:" + o.getOrderState() + "\n" + o.toString() + "\n" + "---------------------------");
			}
			else {
				System.out.println("ID:" + o.getOrderId() + " is cancelled");
			}
		}
		System.out.println("------------------------------------- ");
	}

	private void printTopOfMarket(Instrument instr, TopOfMarket t) {
		System.out.println(instr.getSymbol() + " " + t.getBidQty() + " " + t.getBid() + " - " + t.getAsk() + " " + t.getAskQty());
	}
	
	
	@Override
	public void disconnected() {
		// TODO implement this
		
	}

	@Override
	public void accountList(ArrayList<String> list) {
		// TODO implement this
		
	}

	@Override
	public void error(Exception e) {
		// TODO implement this
		
	}

	@Override
	public void message(int id, int errorCode, String errorMsg) {
		// TODO implement this
		
	}

	@Override
	public void show(String string) {
		// TODO implement this
		
	}
	
	
	public static void main(String[] args) {
		new TestExchange();
	}

}
