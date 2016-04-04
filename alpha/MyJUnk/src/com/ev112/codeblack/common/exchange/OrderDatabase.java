package com.ev112.codeblack.common.exchange;

import java.util.HashMap;
import java.util.Map;


/*
 * Orders indexed by EXCHANGE-ORDERNO and INTERNAL-ORDERNO
 * 
 * 
 */
public class OrderDatabase {
	
	// IB Order-id -> AdapterOrder
	private Map<String, Order>	orders_by_exchange_orderno = new HashMap<String, Order>();
	private Map<String, Order>	orders_by_internal_orderno = new HashMap<String, Order>();
	
	public Order getOrderByOrderId(String orderId) {
		return orders_by_internal_orderno.get(orderId);
	}
	
	public Order getOrderByExchangeOrderId(String orderId) {
		return orders_by_exchange_orderno.get(orderId);
	}
	
	public void addOrder(Order order) {
		/* ASSEERT here that both of these are UNIQUE! */
		orders_by_exchange_orderno.put(order.getExchangeOrderId(), order);
		orders_by_internal_orderno.put(order.getOrderId(), order);
	}
		
}
