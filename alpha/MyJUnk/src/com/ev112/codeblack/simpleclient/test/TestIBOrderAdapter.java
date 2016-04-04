/**
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.ev112.codeblack.simpleclient.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ev112.codeblack.common.configuration.Configuration;
import com.ev112.codeblack.common.database.IAlphaDatabase;
import com.ev112.codeblack.common.generated.messages.NameValueList;
import com.ev112.codeblack.common.instmodel.InstrumentModel;
import com.ev112.codeblack.common.ordercontroller.AdapterMarketStatus;
import com.ev112.codeblack.common.ordercontroller.IBOrderAdapter;
import com.ev112.codeblack.common.ordercontroller.OrderAdapterEventHandler;
import com.ev112.codeblack.common.ordercontroller.OrderException;
import com.ev112.codeblack.common.ordercontroller.OrderInsertRequest;
import com.ev112.codeblack.common.ordercontroller.OrderTimeCondition;
import com.ev112.codeblack.common.ordercontroller.OrderTradeAction;
import com.ev112.codeblack.common.ordercontroller.OrderTradeNumberFactory;
import com.ev112.codeblack.common.ordercontroller.OrderTypeX;
import com.ev112.codeblack.common.ordercontroller.OrderUpdateEvent;
import com.ev112.codeblack.common.ordercontroller.OrderUpdateRequest;
import com.ev112.codeblack.common.ordercontroller.OwnOrder;
import com.ev112.codeblack.common.ordercontroller.OwnTrade;
import com.ev112.codeblack.common.utilities.DateTools;
import com.ev112.codeblack.common.utilities.LogTools;

/**
 * 
 * @author Peter Andersson
 *
 */
public class TestIBOrderAdapter implements OrderAdapterEventHandler {

	private Logger logger = LogManager.getLogger(getClass());
	
	private IBOrderAdapter oa;
	private Configuration config;
	private IAlphaDatabase db;
	
	
	/*	---------------------------------------------------------------------------------------------------------------
	 * 
	 * 
	 * 
	 *	--------------------------------------------------------------------------------------------------------------- 
	 */
	public TestIBOrderAdapter() {
		
		LogTools.initLog4J2();
		
		InstrumentModel instModel;
		
		System.out.println("Loading configuration.....");
		config = new Configuration("PETER");
		
		db = config.getRefDb();
		
		instModel = config.getInstrumentModel();
		oa = new IBOrderAdapter(config, /*orders cb*/this, db);  // OrderAdapterEventInterface
		
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Done sleeping 30 secs!");
		
		// updatePriceOnAllOrders();
	}
	
	/*	---------------------------------------------------------------------------------------------------------------
	 * 
	 * 
	 * 
	 *	--------------------------------------------------------------------------------------------------------------- 
	 */
	public static void main(String[] args) {
		new TestIBOrderAdapter();
	}

	
	/*	---------------------------------------------------------------------------------------------------------------
	 * 
	 * 
	 * 
	 *	--------------------------------------------------------------------------------------------------------------- 
	 */
	Map<String,OwnOrder> ownorders_by_reference = new HashMap<String, OwnOrder>();
	
	/*
	 * OrderControllerCallbackInterface overrides
	 */
	@Override
	public void ownOrderUpdate(OrderUpdateEvent pOrderUpdateEvent) {
		System.out.println("ownOrderUpdate:" + pOrderUpdateEvent.getOwnOrder().toString());
		
		OwnOrder ownOrder = pOrderUpdateEvent.getOwnOrder();
		
		OwnOrder x = ownorders_by_reference.get(ownOrder.getOwnReference()); 
		if (x != null) {
			System.out.println("UPDATE : We have this order : " + x.getOwnReference());
		}
		else {
			System.out.println("UPDATE : We didn't have this order : " + ownOrder.getOwnReference());
			ownorders_by_reference.put(ownOrder.getOwnReference(), ownOrder);
		}
	}

	/*	---------------------------------------------------------------------------------------------------------------
	 * 
	 * 
	 * 
	 *	--------------------------------------------------------------------------------------------------------------- 
	 */
	@Override
	public void ownTradeUpdate(OwnTrade pTrade) {
		System.out.println("ownTradeUpdate:" + pTrade.toString());
	}

	public static class OrderIdGenerator {
		private static Integer orderid = 60000;
		public static Integer getNewOrderId() {
			return orderid++;
		}
		public static String getNewOrderIdStr() {
			String s =  orderid.toString();
			orderid++;
			return s;
		}
	}
	
	/*	---------------------------------------------------------------------------------------------------------------
	 * 
	 * 
	 * 
	 *	--------------------------------------------------------------------------------------------------------------- 
	 */
	@Override
	public void broadcastStatusEvent(String pMessage, String pSource, int pSeverity) {
		System.out.println("broadcastStatusEvent:" + pMessage);
	}

	/*	---------------------------------------------------------------------------------------------------------------
	 * 
	 * 
	 * 
	 *	--------------------------------------------------------------------------------------------------------------- 
	 */
	@Override
	public void adapterMarketStatusEvent(AdapterMarketStatus status) {
		
		if (status == AdapterMarketStatus.MarketConnected) {
			System.out.println("Market is connected - adding some test orders!!");
			
			// addSomeTestOrders();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			addOneOrder("AAPL", 1, 1.0, OrderTradeAction.Buy);
//			addOneOrder("NQ6C", 1, 1.0, OrderTradeAction.Buy);
			addOneOrder("ES6C", 1, 1.0, OrderTradeAction.Buy);
//			addOneOrder("GBL6C", 1, 1.0, OrderTradeAction.Buy);
//			addOneOrder("ERIC.B", 1, 1.0, OrderTradeAction.Buy);
//			addOneOrder("ZC", 1, 1.0, OrderTradeAction.Buy);
//			addOneOrder("ZB6C", 1, 1.0, OrderTradeAction.Buy);
		}
		else {
			System.out.println("Market is NOT connected!");
		}
	}
	
	private void addSomeTestOrders() {
		System.out.println("Adding some orders....");
		NameValueList nvl = new NameValueList();
		for (int i=1; i<2; i++) {
			String orderRef = OrderIdGenerator.getNewOrderIdStr();
			
			OwnOrder ownOrder = new OwnOrder(
				OrderTypeX.LMT,
				new Date().getTime(),	// order time
				"ZF5L",					// 
				70.0,					// price
				i,		    			// volume
				OrderTradeAction.Sell,	//  
				"MyStrategy",			// pStrategyId,
				orderRef,				// String pOwnReference,
				"MyReason",				// String pReason,
				OrderTimeCondition.EOD	// pTimeCondition,
			);
			
			String tOrderId = OrderTradeNumberFactory.getNumberAsHexString();
			ownOrder.setInternalOrderId(tOrderId);

			try {
				System.out.println("Adding one order....");
				oa.addOrder(new OrderInsertRequest(ownOrder, DateTools.getCurrentTime()));
				System.out.println("Saving order under ref: " + orderRef);
				ownorders_by_reference.put(orderRef, ownOrder);
			}
			catch(OrderException ex) {
				System.out.println("Exception:" + ex.getLocalizedMessage());
			}
		}
		System.out.println("DONE Adding some orders....");
	}

	private void addOneOrder(String symbol, int qty, double price, OrderTradeAction ota) {
		System.out.println("Adding an order....");
		
		NameValueList nvl = new NameValueList();
		
		String orderRef = OrderIdGenerator.getNewOrderIdStr();
			
		OwnOrder ownOrder = new OwnOrder(
			OrderTypeX.LMT,
			new Date().getTime(),	// order time
			symbol,					// 
			price,						// price
			qty,		    				// volume
			ota,	//  
			"MyStrategy",			// pStrategyId,
			orderRef,				// String pOwnReference,
			"MyReason",				// String pReason,
			OrderTimeCondition.EOD	// pTimeCondition,
		);
			
		String tOrderId = OrderTradeNumberFactory.getNumberAsHexString();
		ownOrder.setInternalOrderId(tOrderId);

		try {
			System.out.println("Adding one order....");
			oa.addOrder(new OrderInsertRequest(ownOrder, DateTools.getCurrentTime()));
			System.out.println("Saving order under ref: " + orderRef);
			ownorders_by_reference.put(orderRef, ownOrder);
		}
		catch(OrderException ex) {
			System.out.println("Exception:" + ex.getLocalizedMessage());
		}
	
		System.out.println("DONE Adding one orders....");
	}
	
	
	private void updatePriceOnAllOrders() {
		System.out.println("Updating orders....");
		for(String s : ownorders_by_reference.keySet()) {
			OwnOrder o = ownorders_by_reference.get(s);
			try {
				System.out.println("Updating order # " + o.getOrderId() + " ref:" + o.getOwnReference());
				oa.updateOrder(new OrderUpdateRequest(o, 0, o.getPrice()-0.05, DateTools.getCurrentTime()));
			} catch (OrderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("DONE Updating orders....");
	}
	
}
