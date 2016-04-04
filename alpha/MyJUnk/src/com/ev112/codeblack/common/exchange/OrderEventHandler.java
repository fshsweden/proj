package com.ev112.codeblack.common.exchange;

import com.ib.controller.OrderStatus;

public interface OrderEventHandler {
	public void orderStatusChanged(Order order, OrderStatus status, int filled, int remaining, double avgFillPrice, long permId, int parentId, double lastFillPrice, int clientId, String whyHeld);
	public void handleError(Order order, Integer errorCode, String errorMsg);
	
	public void orderAdded(Order order);
	public void orderModified(Order order);
	public void orderCancelled(Order order);
	public void orderTraded(Order order); // implies cancel of remainingQty = 0
}
