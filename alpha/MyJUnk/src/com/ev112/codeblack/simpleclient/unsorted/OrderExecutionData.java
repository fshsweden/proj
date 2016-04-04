package com.ev112.codeblack.simpleclient.unsorted;

import com.ib.client.OrderStatus;

public class OrderExecutionData {
	private OrderStatus status;
	private int filled;
	private int remaining;
	private double avgFillPrice;
	private long permId;
	private int parentId;
	private double lastFillPrice;
	private int clientId;
	private String whyHeld;
	
	public OrderExecutionData(
		OrderStatus status,
		int filled,
		int remaining,
		double avgFillPrice,
		long permId,
		int parentId,
		double lastFillPrice,
		int clientId,
		String whyHeld
	) 
	{
		this.status = status;
		this.filled = filled;
		this.remaining = remaining;
		this.avgFillPrice = avgFillPrice;
		this.permId = permId;
		this.parentId = parentId;
		this.lastFillPrice = lastFillPrice;
		this.clientId = clientId;
		this.whyHeld = whyHeld;
	}
	
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	public int getFilled() {
		return filled;
	}
	public void setFilled(int filled) {
		this.filled = filled;
	}
	public int getRemaining() {
		return remaining;
	}
	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}
	public double getAvgFillPrice() {
		return avgFillPrice;
	}
	public void setAvgFillPrice(double avgFillPrice) {
		this.avgFillPrice = avgFillPrice;
	}
	public long getPermId() {
		return permId;
	}
	public void setPermId(long permId) {
		this.permId = permId;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public double getLastFillPrice() {
		return lastFillPrice;
	}
	public void setLastFillPrice(double lastFillPrice) {
		this.lastFillPrice = lastFillPrice;
	}
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public String getWhyHeld() {
		return whyHeld;
	}
	public void setWhyHeld(String whyHeld) {
		this.whyHeld = whyHeld;
	} 
}
