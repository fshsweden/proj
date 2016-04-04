package com.ev112.codeblack.common.exchange;

public interface Order {
	
	public OrderState2  		getOrderState();
	public Integer 			getInitialQty();
	public Integer 			getCurrentQty();// initially = initialQty
	public Double 			getPrice();
	public Double 			getAuxPrice();	// only used for STP and STP_LMT
	public OrderAction 		getAction();	// ACTION_BUY or ACTION_SELL
	public TimeInForce 		getOrderTime();	// EOD, GTC, GTD
	public OrderType		getOrderType();	// LMT, MKT, STOP

	// Only used/updated by API, never by Alpha
	public String 			getExchangeOrderId();
	public String			getOrderId();
	
	public void	setOrderState(OrderState2 os);
	public void	setInitialQty(Integer iq);
	public void	setCurrentQty(Integer cq);
	public void setPrice(Double p);
	public void	setAuxPrice(Double p);
	public void setAction(OrderAction a);
	public void	setOrderTime(TimeInForce tf);	// EOD, GTC, GTD
	public void	setOrderType(OrderType ot);	// LMT, MKT, STOP

	// Only used/updated by API, never by Alpha
	public void setExchangeOrderId(String e);
	public void	setOrderId(String s);	
	
	@Override
	public String toString();
	
}
