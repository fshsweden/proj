package com.ev112.codeblack.simpleclient.ibexamples;

import java.util.ArrayList;

import com.ev112.codeblack.pricecollector.ib.ITwsMarketControllerHandler;
import com.ev112.codeblack.pricecollector.ib.TwsMarketController;
import com.ib.controller.ApiController.IContractDetailsHandler;
import com.ib.controller.ApiController.IOrderHandler;
import com.ib.controller.ApiController.ITopMktDataHandler;
import com.ib.controller.NewContract;
import com.ib.controller.NewContractDetails;
import com.ib.controller.NewOrder;
import com.ib.controller.NewOrderState;
import com.ib.controller.NewTickType;
import com.ib.controller.OrderType;
import com.ib.controller.Types.Action;
import com.ib.controller.Types.MktDataType;
import com.ib.controller.Types.SecType;

public class TestIB implements ITwsMarketControllerHandler {
	
	private TwsMarketController ibMarket;
//	private String sTWSHost = "alphatrading.dnsalias.com";
//	private Integer sTWSPort = 36661, sTWSClientId = 98;
	private String sTWSHost = "192.168.0.23";
	private Integer sTWSPort = 6661, sTWSClientId = 98;

	public TestIB() {
		/*
		 * Connect to IB/TWS
		 */
		System.out.println("Connecting to IB market at Host:" + sTWSHost + " port:" + sTWSPort);
		System.out.println("Wait for message that connection succeeded...");
		
		ibMarket = new TwsMarketController(this);
		ibMarket.connect(sTWSHost, sTWSPort, sTWSClientId);
		
//		testNewContract("SWEDA", "SFB", "SEK", SecType.STK);
//		testSubscribePrice("SWEDA", "", "SFB", "SEK", SecType.STK);
		
		testReqContractDetailsFuture("CL","","","",SecType.FUT);
//		testSubscribeFuturePriceLocalSymbol("ZF","20151231","", "ECBOT","USD",SecType.FUT);
		
		// test placing orders!
//		testPlaceOrderFuture("ZF","20151231","", "ECBOT","USD",SecType.FUT, 1, 1.0);
//		testPlaceOrderFuture("ZF","20151231","", "SMART","USD",SecType.FUT, 1, 1.0);
		
		sleep(20);
	}
	
	private void sleep(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void testSubscribePrice(String symbol, String localSymbol, String exch, String curr, SecType st) {
	
		System.out.println("testSubscribePrice()");
		
		NewContract c = new NewContract();
		c.symbol(symbol);
		c.localSymbol(localSymbol);
		c.exchange(exch);
		c.currency(curr);
		c.secType(st);
		
		ibMarket.reqTopMktData(c, "", false, new ITopMktDataHandler() {
			
			@Override
			public void tickString(NewTickType tickType, String value) {
				//System.out.println("tickString      : " + tickType.name() + " " + value);
			}
			
			@Override
			public void tickSnapshotEnd() {
				System.out.println("tickSnapshotEnd : ");
			}
			
			@Override
			public void tickSize(NewTickType tickType, int size) {
				System.out.println("tickSize        : " + tickType.name() + " " + size);
			}
			
			@Override
			public void tickPrice(NewTickType tickType, double price, int canAutoExecute) {
				System.out.println("tickPrice       : " + c.symbol() + " " + price);
			}
			
			@Override
			public void marketDataType(MktDataType marketDataType) {
				//System.out.println("marketDataType  : " + marketDataType.name());
			}
		});
	}

	
	private void testSubscribeFuturePriceLocalSymbol(String symbol, String expdate, String localSymbol, String exch, String curr, SecType st) {
		
		System.out.println("testSubscribePrice()");
		
		NewContract c = new NewContract();
		c.symbol(symbol);
		c.expiry(expdate);
		c.localSymbol(localSymbol);
		c.exchange(exch);
		c.currency(curr);
		c.secType(st);
		
		ibMarket.reqTopMktData(c, "", false, new ITopMktDataHandler() {
			
			@Override
			public void tickString(NewTickType tickType, String value) {
				//System.out.println("tickString      : " + tickType.name() + " " + value);
			}
			
			@Override
			public void tickSnapshotEnd() {
				System.out.println("tickSnapshotEnd : ");
			}
			
			@Override
			public void tickSize(NewTickType tickType, int size) {
				System.out.println("tickSize        : " + tickType.name() + " " + size);
			}
			
			@Override
			public void tickPrice(NewTickType tickType, double price, int canAutoExecute) {
				System.out.println("tickPrice       : " + c.symbol() + " " + price);
			}
			
			@Override
			public void marketDataType(MktDataType marketDataType) {
				//System.out.println("marketDataType  : " + marketDataType.name());
			}
		});
	}

	
	private void testPlaceOrderFuture(String symbol, String expdate, String localSymbol, String exch, String curr, SecType st, Integer qty, Double price) {
		
		System.out.println("testPlaceOrderFuture()");
		
		NewContract c = new NewContract();
		c.symbol(symbol);
		c.expiry(expdate);
		c.localSymbol(localSymbol);
		c.exchange(exch);
		c.currency(curr);
		c.secType(st);
		
		NewOrder order = new NewOrder();
		order.account("DU192885");
		order.action(Action.BUY);
		Integer orderId = ibMarket.getNextValidOrderIdAndIncrement(); 
		order.orderRef(orderId.toString());
		order.orderId(orderId);
		order.totalQuantity(qty);
		order.orderType(OrderType.MKT);
		// order.orderType(OrderType.LMT);
		// order.lmtPrice(price);
		
		ibMarket.placeOrModifyOrder(c, order, new IOrderHandler() {
			@Override
			public void orderState(NewOrderState orderState) {
				System.out.println("orderState=" + orderState.toString());
			}
			@Override
			public void handle(int errorCode, String errorMsg) {
				System.out.println("handle=" + errorCode + " " + errorMsg);
			}
		});
	}

	
	private void testNewContract(String symbol, String exch, String curr, SecType sec) {

		NewContract c = new NewContract();
//		c.conid(917845);
		c.symbol(symbol);
		c.secType(sec);
		c.exchange(exch);
		c.currency(curr);

		
		ibMarket.reqContractDetails(c, new IContractDetailsHandler() {
			
			@Override
			public void contractDetails(ArrayList<NewContractDetails> list) {
				
				for (NewContractDetails cd : list) {
					System.out.println("Details:" + cd.toString());
				}
			}
		});
	}
	
	private void testReqContractDetailsFuture(String underlying, String expdate, String exch, String curr, SecType sec) {

		NewContract c = new NewContract();
//		c.conid(917845);
		c.symbol(underlying);
		c.expiry(expdate);
		c.exchange(exch);
		c.currency(curr);
		c.secType(sec);

		ibMarket.reqContractDetails(c, new IContractDetailsHandler() {
			@Override
			public void contractDetails(ArrayList<NewContractDetails> list) {
				for (NewContractDetails cd : list) {
					System.out.println("Details:" + cd.toString());
				}
			}
		});
	}
	
	public static void main(String[] args) {
		new TestIB();
	}


	@Override
	public void connectedToTws() {
		System.out.println("connectedToTws");
	}


	@Override
	public void disconnectedFromTws() {
		System.out.println("disconnectedFromTws");
	}

	@Override
	public void infoFromTws(int id, int code, String msg) {
		System.out.println("infoFromTws:" + msg);
	}
}
