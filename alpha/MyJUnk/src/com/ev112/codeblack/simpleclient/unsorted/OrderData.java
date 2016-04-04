package com.ev112.codeblack.simpleclient.unsorted;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

class OrderData {
	private Contract contract; 
	private Order order; 
	private OrderState orderState;
	
	public OrderData(Contract contract, 
				Order order, 
				OrderState orderState) {
		this.contract = contract;
		this.order = order;
		this.orderState = orderState;
	}

	public Contract getContract() {
		return contract;
	}

	public Order getOrder() {
		return order;
	}

	public OrderState getOrderState() {
		return orderState;
	}
	
}