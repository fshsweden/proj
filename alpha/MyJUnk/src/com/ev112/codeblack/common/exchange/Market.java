package com.ev112.codeblack.common.exchange;

import com.ib.controller.ApiController.IConnectionHandler;

public interface Market {
	
	public Boolean connect(KeyValues args, IConnectionHandler handler);
	public void disconnect();
	
	public void addOrder(Order order);
	public void modifyOrder(Order order);
	public void cancelOrder(String orderId);

	public Order getOrder(String orderId);
	

	/*
	 * Somehow, we need a baseline from where to starting order ID's
	 */
	public void setBaseOrderId(Integer base);  // SEED to start
}
