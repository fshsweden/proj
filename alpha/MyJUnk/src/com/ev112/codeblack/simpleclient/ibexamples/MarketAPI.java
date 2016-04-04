package com.ev112.codeblack.simpleclient.ibexamples;

import java.util.HashMap;
import java.util.Set;

import com.ev112.codeblack.pricecollector.ib.IInstrument;
import com.ev112.codeblack.pricecollector.ib.IInstrumentObserver;
import com.ev112.codeblack.pricecollector.ib.MarketStatusHandler;
import com.ib.controller.NewContract;
import com.ib.controller.OrderType;
import com.ib.controller.Types.Action;
import com.ib.controller.Types.SecType;
import com.ib.controller.Types.TimeInForce;

/*
 * Generic Market API 
 * 
 * 
 */
public interface MarketAPI {

	public enum InstrumentType	{Index, Stock, Future, Forward, ETF};
//	public enum TimeInForce		{EOD};
//	public enum OrderType		{MKT, LMT};
	
	/*
	 * Data making up an Order
	 */
	public interface OrderData {
		public TradableInstrument	getInstrument();
		public Double				getPrice();
		public Integer				getQty();
		public TimeInForce			getTimeInForce();
		public OrderType				getOrderType();
		public String				getInternalOrderId();
		public String				getExchangeOrderId();
		
		public void setInstrument(TradableInstrument instr);
		public void setPrice(Double price);
		public void setQty(Integer qty);
		public void setTimeInForce(TimeInForce tif);
		public void setOrderType(OrderType ot);
		public void	setInternalOrderId(String oid);
		public void	setExchangeOrderId(String oid);
	}
	
	
	public interface OrderAPI {
		public RetCodes enterOrder(OrderData order);
		public RetCodes modifyOrder(OrderData order);
		public RetCodes cancelOrder(OrderData order);
	}

	public interface ExchangeInfo {
		public String getExchangeSymbol();	// The exchange local symbol, like NQ6U
		public String getExchangeName();		// NASDAQ, NYSE etc (keyword) 
		public String getCurrency();
	}
	
	public interface Instrument {
		public InstrumentType getInstrumentType(); 
		public String getLongName();
		public String getSymbol();
	}
	
	public interface TradableInstrument extends Instrument {
		public ExchangeInfo[] getExchangeInfo();  // One for every exchange where it is traded
	}
	
	public interface MarketPriceAPI {
		public int subscribeToInstrument(TradableInstrument i);
	}
	
	public static enum PriceType {Bid, Ask, Last};
	public static enum QtyType {BidQty,AskQty,LastQty,Volume};
	
	public interface MarketPriceDataItem {
		public PriceType getPriceType();
		public QtyType getQtyType();
	}
	
	public interface MarketPriceData {
		public MarketPriceDataItem[] getPriceDataItems();
	}
	
	public interface MarketPriceEventHandler {
		public void marketPriceUpdated(TradableInstrument i, MarketPriceData data);
	}
	
	public interface ProductsAPI {
		
	}
	
	public class KeyValues extends HashMap<String,String> {
		
	}
	
	public class RetCodes extends KeyValues {
		
	}
	
	public static enum InformationType {Error, Information} ;
	
	public interface ConnectionEventHandler {
		public void connected(KeyValues info);
		public void disconnected(KeyValues info);
		public void information(InformationType it, KeyValues info);
	}
	
	// Do whatever you can to connect the API completely
	public void configure(KeyValues config);
	public RetCodes connect(ConnectionEventHandler handler);
	
	
	

	
	/* OLD */
	
	public String getName();
	
	// public boolean disconnect();
	
	public int subscribeToInstrument(IInstrument ii, IInstrumentObserver observer);
	// public int desubscribeToInstrument(IInstrument ii, IInstrumentObserver observer);
	
	public void addMarketStatusHandler(MarketStatusHandler handler);
	public void loadActiveOrders();
	
	public void loadSymbol(SecType secType, String symbolName);
	public int getNextValidOrderIdAndIncrement();
	
	public void enterOrder(String alphaSymbol, double price, int qty, Action action, OrderType otype, TimeInForce tif, double auxprice, int clientOrderId);
	// public void requestSymbols(SecType st, String spec, ContractInfoHandler handler);  replaced by loadSymbol()
	
	public String makeSymbol(NewContract contract);
	public Set<String> getAllSymbols();
	public Set<IInstrument> getAllInstruments();
}

