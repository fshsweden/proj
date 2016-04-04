package com.ev112.codeblack.common.exchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ev112.codeblack.common.generated.messages.StrategyServer_OwnTrade;
import com.ev112.codeblack.common.instmodel.Derivative;
import com.ev112.codeblack.common.instmodel.Instrument;
import com.ib.client.Contract;
import com.ib.client.OrderState;
import com.ib.client.TickType;
import com.ib.client.Types.Action;
import com.ib.client.Types.MktDataType;
import com.ib.client.Types.SecType;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.IConnectionHandler;
import com.ib.controller.ApiController.IOrderHandler;
import com.ib.controller.ApiController.ITopMktDataHandler;

public class IB implements Market /* , OrderAdapter */, IConnectionHandler {

	private Integer			nextTickerId			= 3000;
	private static int		depth_ctr				= 0;
	private AtomicBoolean	nextValidOrderIdRcvd	= new AtomicBoolean();
	private AtomicBoolean	connectionFailure		= new AtomicBoolean();
	private String			host;
	private int				port;
	private String			account;
	private int				clientId;
	private Integer			nextReqId				= 1000000;

	// private EClientSocket eclientSocket;
	private ApiController apiController;

	private Map<Integer, Instrument>	subscribed_instruments	= new TreeMap<Integer, Instrument>();
	private Map<Integer, MarketPriceEventHandler> market_price_event_handlers = new HashMap<Integer, MarketPriceEventHandler>();
	private List<OrderEventHandler>		order_handlers			= new ArrayList<OrderEventHandler>();
	private List<ExecutionEventHandler>	execution_handlers		= new ArrayList<ExecutionEventHandler>();

	// private IBAdapterOrderDatabase order_db = new IBAdapterOrderDatabase();
	private Map<Integer, Order> orders = new TreeMap<Integer, Order>();

	private List<IConnectionHandler>		connection_handlers		= new ArrayList<IConnectionHandler>();
	private List<StrategyServer_OwnTrade>	strategyserver_trades	= new ArrayList<StrategyServer_OwnTrade>();

	public void addOrderEventHandler(OrderEventHandler meh) {
		order_handlers.add(meh);
	}

	public void addExecutionEventHandler(ExecutionEventHandler meh) {
		execution_handlers.add(meh);
	}

/*****************	
	public void downloadOrders(final OrderEventHandler oeh) {

		apiController.takeTwsOrders(new ILiveOrderHandler() {
			
			@Override
			public void orderStatus(int orderId, OrderStatus status, int filled, int remaining, double avgFillPrice, long permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
				Order order = orders.get(orderId);
				if (order == null) {
					System.out.println("******* ORDERID " + orderId + " NOT FOUND! ERROR!!!! *********");
				}
				else {
					oeh.orderStatusChanged(order, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
				}
			}
			
			@Override
			public void openOrderEnd() {
				// TODO implement this
			}
			
			@Override
			public void openOrder(Contract contract, NewOrder newOrder, OrderState orderState) {
				
				System.out.println("OpenOrder." + newOrder.toString());
				
				Order order = orders.get(newOrder.orderId());
				if (order == null) {
					
					order = new IBOrder();
					
					order.setOrderState(OrderState2.None);
					order.setPrice(newOrder.lmtPrice());
					order.setAuxPrice(newOrder.auxPrice());
					order.setInitialQty(newOrder.totalQuantity());
					order.setAction(newOrder.action() == Action.BUY ? OrderAction.BUY : OrderAction.SELL);
					
					switch (newOrder.tif()) {
						case AUC:
							break;
						case DAY:
							order.setOrderTime(TimeInForce.EOD);
						break;
						case DTC:
							break;
						case FOK:
							order.setOrderTime(TimeInForce.FOK);
						break;
						case GTC:
							break;
						case GTD:
							break;
						case GTT:
							break;
						case GTX:
							break;
						case IOC:
							break;
						case OPG:
							break;
						default:
							break;
						
					}
					
					switch (newOrder.orderType()) {
						case BOX_TOP:
							break;
						case FIX_PEGGED:
							break;
						case LIT:
							break;
						case LMT:
							order.setOrderType(OrderType.LMT);
							break;
						case LMT_PLUS_MKT:
							break;
						case LOC:
							break;
						case MIT:
							break;
						case MKT:
							order.setOrderType(OrderType.MKT);
							break;
						case MKT_PRT:
							break;
						case MOC:
							break;
						case MTL:
							break;
						case None:
							break;
						case PASSV_REL:
							break;
						case PEG_BENCH:
							break;
						case PEG_MID:
							break;
						case PEG_MKT:
							break;
						case PEG_PRIM:
							break;
						case PEG_STK:
							break;
						case REL:
							break;
						case REL_PLUS_LMT:
							break;
						case REL_PLUS_MKT:
							break;
						case STP:
							order.setOrderType(OrderType.STP);
							break;
						case STP_LMT:
							order.setOrderType(OrderType.STP_LMT);
							break;
						case STP_PRT:
							break;
						case TRAIL:
							break;
						case TRAIL_LIMIT:
							break;
						case TRAIL_LIT:
							break;
						case TRAIL_LMT_PLUS_MKT:
							break;
						case TRAIL_MIT:
							break;
						case TRAIL_REL_PLUS_MKT:
							break;
						case VOL:
							break;
						case VWAP:
							break;
						default:
							break;
						
					}
					order.setExchangeOrderId(makeString(newOrder.orderId()));
					order.setOrderId(newOrder.orderRef());
					
					order.setCurrentQty(newOrder.totalQuantity());  // CHECK!!
					
					orders.put(newOrder.orderId(), order);
					
					oeh.orderAdded(order);
				}
				else {
					oeh.orderModified(order);
				}
			}
			
			@Override
			public void handle(int orderId, int errorCode, String errorMsg) {
				// TODO implement this
				System.out.println("handle() " + orderId + " " + errorMsg);
			}
		});
	}
	
	private String makeString(int i) {
		return String.format("%d", i);
	}

	public void StartListeningToLiveOrderchanges() {

		apiController.reqLiveOrders(new ILiveOrderHandler() {
			@Override
			public void orderStatus(int orderId, OrderStatus status, int filled, int remaining, double avgFillPrice, long permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {

				Order order = orders.get(orderId);

				if (order == null) {
					System.out.println("******* ORDERID " + orderId + " NOT FOUND! ERROR!!!! *********");
				} else {
					for (OrderEventHandler o : order_handlers) {
						o.orderStatusChanged(order, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
					}
				}
			}

			@Override
			public void openOrderEnd() {
			}

			@Override
			public void openOrder(Contract contract, NewOrder order, OrderState orderState) {
			}

			@Override
			public void handle(int orderId, int errorCode, String errorMsg) {

				if (orderId == -1) {
					// an error/message that doesnt refer to an order 
				} 
				else {
					Order order = orders.get(orderId);

					if (order == null) {
						System.out.println("******* ORDERID " + orderId + " NOT FOUND! ERROR!!!! *********");
					} 
					else {
						for (OrderEventHandler o : order_handlers) {
							o.handleError(order, errorCode, errorMsg);
						}
					}
				}
			}
		});
	}

	private Boolean initialTradeLoadingDone = false;

	public void StartListeningToExecutions() {

		apiController.reqExecutions(new ExecutionFilter(), new ITradeReportHandler() {

			@Override
			public void tradeReportEnd() {
				initialTradeLoadingDone = true;
				// checkOrdersTradesPositionsLoaded();
			}

			@Override
			public void tradeReport(String tradeKey, Contract contract, Execution execution) {

				StrategyServer_OwnTrade trade = new StrategyServer_OwnTrade();
				trade.setAction(execution.m_side.equals("BOT") ? OrderTradeAction.Buy.name() : OrderTradeAction.Sell.name());
				trade.setSymbol(contract.symbol()); // Database needed!!! Contract -> Internal symbol!
				trade.setPrice(execution.m_price);
				trade.setVolume(execution.m_shares);
				trade.setStrategyId("Unknown"); // TODO: where can we find this? Using the order ref?????
				trade.setOrderId(new Integer(execution.m_orderId).toString());
				trade.setTradeId(tradeKey);
				trade.setOwnReference(execution.m_orderRef);
				trade.setCreateTime(0); // TODO: where?? from table?
				// trade.setUserExt(pUserExt); TODO: not saved... needed???

				strategyserver_trades.add(trade);
				
				if (!initialTradeLoadingDone) {
					
				} 
				else {
					
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//					Date resultdate = new Date(System.currentTimeMillis());
//					String date_s = sdf.format(resultdate);
//
//					AdapterOrder_IB my = order_db.getOrderByOrderId(execution.m_orderId);
//
//					logger.info("Execution " + execution.m_orderId + " " + execution.m_side + " " + contract.symbol() + " Price:" + execution.m_price + " Qty:" + execution.m_shares + " CumQty:" + execution.m_cumQty + " " + (my != null ? ownRef(my.getOwnOrder()) : "OwnRef N/A"));
//
//					if (my != null) {
//						long timestamp = System.currentTimeMillis();
//
//						// @formatting:off
//						alpha_database.addTrade(new Integer(execution.m_permId).toString(), // String perm_id
//								new Integer(execution.m_orderId).toString(), // String order_id
//								my.getOwnOrder().getSymbol(), // String symbol
//								new Double(execution.m_price).toString(), // String price
//								new Integer(execution.m_shares).toString(), // String qty
//								date_s, // String trade_time
//								my.getOwnOrder().getActionAsString(), // String action
//								my.getOwnOrder().getStrategyId(), // String strategy_id
//								my.getOwnOrder().getOwnReference(), // String ownref
//								tradeKey, // String order_status
//								my.getOwnOrder().getReason(), // String reason
//								my.getOwnOrder().getTimeCondition().name(), // String tif
//								my.getOwnOrder().getOrderType().name(), // String order_type
//								"", // String substate
//								tradeKey);
//						// @formatting:on
//
//						OwnTrade t = new OwnTrade(timestamp, execution.m_side.equals("BOT") ? OrderTradeAction.Buy : OrderTradeAction.Sell, my.getOwnOrder().getSymbol(), execution.m_shares, execution.m_price, new Integer(execution.m_orderId).toString(), tradeKey,
//								my.getOwnOrder().getStrategyId(), my.getOwnOrder().getOwnReference(), my.getOwnOrder().getReason(), my.getOwnOrder().getUserExt());
//
//						mOrderAdapterEventInterface.ownTradeUpdate(t);
//
//						
//						// Subtract traded volume (execution.m_shares) from my.getOwnOrder()
//						
//						OwnOrder ownOrder = my.getOwnOrder();
//						assert(ownOrder.getCurrentVolume() - execution.m_shares >= 0);
//						ownOrder.setCurrentVolume(ownOrder.getCurrentVolume() - execution.m_shares);
//						if (ownOrder.getCurrentVolume() == 0) {
//							logger.info("Order " + execution.m_orderId + " " + ownRef(ownOrder) + " fully traded - setting status to DELETED and informing system!");
//							my.getOwnOrder().setSubState("DELETED");
//							mOrderAdapterEventInterface.ownOrderUpdate(new OrderUpdateEvent(ownOrder, EventType.CANCELED, 0, "OK", System.currentTimeMillis()));
//						} else {
//							logger.info("Order " + execution.m_orderId + " " + ownRef(ownOrder) + " partially traded!");
//							mOrderAdapterEventInterface.ownOrderUpdate(new OrderUpdateEvent(ownOrder, EventType.UPDATED, 0, "OK", System.currentTimeMillis()));
//						}
//					} else {
//						logger.error("Did not find the corresponding order:" + execution.m_orderId);
//						mOrderAdapterEventInterface.broadcastStatusEvent("IBOrderAdapter did not find the corresponding order:" + execution.m_orderId, "IBOrderAdapter", StatusEvent.Error);
//
//						
//						// We save an entry into the database anyway!
//						
//
//						// @formatting:off
//
//						alpha_database.addTrade(new Integer(execution.m_permId).toString(), // String perm_id
//								new Integer(execution.m_orderId).toString(), // String order_id
//								contract.symbol(), // String symbol
//								new Double(execution.m_price).toString(), // String price
//								new Integer(execution.m_shares).toString(), // String qty
//								date_s, // String trade_time
//								execution.m_side, // String action
//								"", // String strategy_id = unknown
//								"", // String ownref = unknown
//								tradeKey, // String order_status
//								"TWS", // String reason
//								execution.m_time, // String tif
//								"", // String order_type
//								"", // String substate
//								tradeKey
//
//						);
//						// @formatting:on
//
//					}
			
				}
			}

			@Override
			public void commissionReport(String tradeKey, CommissionReport commissionReport) {
				if (initialTradeLoadingDone) {
					// alpha_database.addCommission(tradeKey, commissionReport.m_commission, commissionReport.m_currency);
				}
			}

		});
	}
**********************************/

	/*
	 * ------------------------------------------------------------------------------------------------
	 * ------------------------------------------------------------------------------------------------
	 */
	public int subscribeToInstrument(Instrument i, MarketPriceEventHandler meh) {

		Contract con = new Contract();
		Integer tickerId = nextTickerId++;

		market_price_event_handlers.put(tickerId, meh);
		subscribed_instruments.put(tickerId, i);

		switch (i.getType()) {

			case Index:
				con.secType(SecType.IND);
				con.symbol(i.getSymbol());
				con.currency(i.getMarket().getPriceCurrency());
				con.exchange(i.getExchange());
				System.out.println(tickerId + " Subscribing to IND Symbol:" + con.symbol() + " Currency:" + con.currency() + " Exchange:" + con.exchange() + " secType:" + con.secType());
			break;

			case Stock:
				con.secType(SecType.STK);
				con.symbol(i.getSymbol());
				// con.m_localSymbol = ii.getStr("SYMBOL");
				con.currency(i.getMarket().getPriceCurrency());
				con.exchange(i.getPriceRoute());
				System.out.println(tickerId + " Subscribing to STK Symbol:" + con.symbol() + " Currency:" + con.currency() + " Exchange:" + con.exchange() + " secType:" + con.secType());
			break;

			case Future:
				con.secType(SecType.FUT);
				// For futures, the symbol is the underlying
				con.localSymbol(i.getLocalSymbol());
				if (con.localSymbol().equals("")) {
					con.symbol(((Derivative) i).getUnderlyingSymbol());
					String s = ((Derivative) i).getLastTradingDateString();
					s = s.replace("-", "");
					con.lastTradeDateOrContractMonth(s);
				}
				con.currency(i.getMarket().getPriceCurrency());
				con.exchange(i.getExchange());

				if (con.localSymbol().equals("")) {
					System.out.println(tickerId + " Subscribing to FUT Symbol:" + i.getSymbol() + " Symbol:" + con.symbol() + " Currency:" + con.currency() + " Exchange:" + con.exchange() + " Expiry:" + con.expiry() + " secType:" + con.secType());
				} else {
					System.out.println(tickerId + " Subscribing to FUT Symbol:" + " LocalSymbol :" + con.localSymbol() + " Currency:" + con.currency() + " Exchange:" + con.exchange() + " secType:" + con.secType());
				}
			break;

			default:
				System.out.println("Unknown SECTYPE:" + con.secType());
			break;
		}

		apiController.reqTopMktData(con, "233", false, new ITopMktDataHandler() {
			
			@Override
			public void tickString(TickType tickType, String value) {
				MarketPriceEventHandler mpeh = market_price_event_handlers.get(tickerId); 
				System.out.println("tickString:" + tickerId + " ticktype:" + tickType + " value:" + value);

				Instrument instr = subscribed_instruments.get(tickerId);
				if (instr == null) {
					return;
				}

				String values[] = value.split(";");
				for (int i=0; i<values.length; i++) {
					System.out.println("    " + values[i]);
				}

				switch (tickType) {
					case RT_VOLUME:

						Integer old_volume;
						try {
							old_volume = 0; // instr.getInt("TOTAL_VOLUME");
						} catch (Exception ex) {
							old_volume = 0;
						}

						try {
							Double price, vwap;
							Integer size, volume;
							Long last_trade_time;

							price = Double.parseDouble(values[0]);
							size = Integer.parseInt(values[1]);
							last_trade_time = Long.parseLong(values[2]);
							volume = Integer.parseInt(values[3]);
							vwap = Double.parseDouble(values[4]);

							long cur = System.currentTimeMillis();
							instr.setLong("TIMESTAMP", cur);

							if ((old_volume + size != volume) && old_volume > 0) {
								System.out.println("Volumes doesnt match for !" + instr.getSymbol() + " Saved volume was " + old_volume + " trade was " + size + " and new volume is " + volume);
							}

							instr.setDbl("LAST_TRADE_PRICE", price);
							instr.setInt("LAST_TRADE_SIZE", size);
							instr.setLong("LAST_TRADE_TIME", last_trade_time);
							instr.setInt("TOTAL_VOLUME", volume);
							instr.setDbl("VWAP", vwap);
							instr.setStr("SINGLE_TRADE", values[5]);

							/* MarketPriceEventHandler meh = handlers.get(tickerId);
							if (meh != null) {
								meh.instrumentTraded(tickerId,subscribed_instruments.get(tickerId), price, size, values[2], volume);
							}
							*/

						} catch (NumberFormatException e) {
							// CoreLogger.getInstance().log("Price or Size was 0, so this was no trade...");
						} catch (Exception ex) {
							System.out.println("Exception:" + ex.getLocalizedMessage());
						}
					break;
					
					default:
						System.out.println("Unhandled ticktype: " + tickType);
				}
			}
			
			@Override
			public void tickSnapshotEnd() {
				// TODO implement this
			}
			
			@Override
			public void tickSize(TickType tickType, int size) {
				MarketPriceEventHandler mpeh = market_price_event_handlers.get(tickerId);
				mpeh.sizeUpdated(tickerId, i, tickType, size);
			}
			
			@Override
			public void tickPrice(TickType tickType, double price, int canAutoExecute) {
				MarketPriceEventHandler mpeh = market_price_event_handlers.get(tickerId);
				mpeh.priceUpdated(tickerId, i, tickType, price, canAutoExecute);
			}
			
			@Override
			public void marketDataType(MktDataType marketDataType) {
				// TODO implement this
				
			}
		});
		/*
		 * if (i.getMarketDataSubscriptionType() == MARKET_DATA_SUBSCRIPTION_TYPE.L2 && depth_ctr < 3) {
		 * depth_ctr = depth_ctr + 1;
		 * System.out.println("Requesting DEPTH 5 for tickerId " + tickerId);
		 * eclientSocket.reqMktDepth(tickerId, con, 5);
		 * }
		 * else {
		 * System.out.println("Requesting TOP MARKET for tickerId " + tickerId);
		 * // eclientSocket.reqMktData(tickerId, con, "233", false);
		 * }
		 */

		// NOW KEEP TRACK OF THIS SUBSCRIPTION

		// if (m_subscriptions.get(tickerId) == null) {
		// m_subscriptions.put(tickerId, new ArrayList<IInstrumentObserver>());
		// }
		// m_subscriptions.get(tickerId).add(observer);
		//
		// m_instruments.put(tickerId, ii);
		/*
		 * remember connection between Contract (IB Specific) and IInstrument
		 * (Generic)
		 */
		// m_contract.put(ii, con);
		return tickerId;
	}

	public void placeOrder(Instrument ib, Double price, Integer size, Action action) {

		// Contract from Instrument !
		
		Contract contract = new Contract();
		contract.symbol("ATCO.B");
		contract.secType(SecType.STK);
		contract.currency("SEK");
		contract.exchange("SFB");

		IBOrder ibOrder = new IBOrder();
		ibOrder.setPrice(price);
		ibOrder.setInitialQty(size);
		ibOrder.setAction(OrderAction.BUY);
		ibOrder.setOrderTime(TimeInForce.EOD);
		ibOrder.setOrderType(OrderType.LMT);

		Integer i = apiController.getNextValidOrderIdAndIncrement();
		ibOrder.setExchangeOrderId(i.toString());
		ibOrder.setOrderId(i.toString());

		/* Store order */
		orders.put(i, ibOrder);

		Order newOrder = ibOrder.getNewOrder();

		apiController.placeOrModifyOrder(contract, newOrder, new IOrderHandler() {
			@Override
			public void orderState(OrderState orderState) {
				System.out.println("orderState:" + orderState.toString());
			}

			@Override
			public void handle(int errorCode, String errorMsg) {
				System.out.println("HANDLE:" + errorCode + " " + errorMsg);
			}
		});
	}

	/*
	 * ------------------------------------------------------------------------------------------------
	 * INTERFACE: MARKET
	 * ------------------------------------------------------------------------------------------------
	 */
	@Override
	public Boolean connect(KeyValues args, IConnectionHandler handler) {

		connection_handlers.add(handler);

		host = args.get("HOST");
		port = args.getInt("PORT");
		account = args.get("ACCOUNT");
		clientId = args.getInt("CLIENTID");

		System.out.println("Connecting to host:" + host + " port:" + port + " client_id:" + clientId);

		apiController = new ApiController();
		apiController.setConnectionHandler(this);
		apiController.connect(host, port, clientId,"");

		// market.reqIds(1);
		// eclientSocket.reqCurrentTime();
		// eclientSocket.reqGlobalCancel();

		// eclientSocket.reqAccountSummary(getNextReqId(), /*group*/"All", /*tags*/"NetLiquidation");
		/*
		 * eclientSocket.reqAccountUpdates(true, account);
		 * eclientSocket.reqExecutions(getNextReqId(), new ExecutionFilter());
		 * eclientSocket.reqNewsBulletins(true);
		 * eclientSocket.reqPositions();
		 * eclientSocket.reqMarketDataType(MarketDataType.REALTIME);
		 * loadActiveOrders();
		 * System.out.println("IBMarket har setup IB Market propertly now");
		 * currentAdapterMarketStatus = AdapterMarketStatus.MarketConnected;
		 */

		return true;
	}

	@Override
	public void disconnect() {

	}

	@Override
	public void addOrder(Order order) {
		String orderId = generateOrderId();

	}

	@Override
	public void modifyOrder(Order order) {

	}

	@Override
	public void cancelOrder(String orderId) {

	}

	@Override
	public Order getOrder(String orderId) {
		Order o = orders.get(orderId);
		return o;
	}

	@Override
	public void setBaseOrderId(Integer base) {
		nextValidOrderId = base;
	}

	private Integer nextValidOrderId = 1;

	private String generateOrderId() {
		Integer orderId = nextValidOrderId++;
		return orderId.toString();
	}


	
	/*
	 * IConnectionHandler
	 * 
	 * 
	 * 
	 */

	@Override
	public void connected() {
		for (IConnectionHandler h : connection_handlers) {
			h.connected();
		}
	}

	@Override
	public void disconnected() {
		System.out.println("Disconnected!!!!!!!!!!");
	}

	@Override
	public void accountList(ArrayList<String> list) {
		// TODO implement this

	}

	@Override
	public void message(int id, int errorCode, String errorMsg) {
		// TODO implement this
		System.out.println(errorMsg);
	}

	@Override
	public void show(String string) {
		System.out.println(string);
	}

	@Override
	public void error(Exception e) {
		// TODO implement this
		
	}
	
	
}
