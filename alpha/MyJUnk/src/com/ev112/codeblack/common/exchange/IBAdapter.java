package com.ev112.codeblack.common.exchange;

import java.util.List;

import com.ev112.codeblack.common.generated.messages.PLPosition;
import com.ev112.codeblack.common.generated.messages.StrategyServer_OwnOrder;
import com.ev112.codeblack.common.generated.messages.StrategyServer_OwnTrade;
import com.ev112.codeblack.common.ordercontroller.AdapterMarketStatus;
import com.ev112.codeblack.common.ordercontroller.OrderAdapter;
import com.ev112.codeblack.common.ordercontroller.OrderCancelRequest;
import com.ev112.codeblack.common.ordercontroller.OrderException;
import com.ev112.codeblack.common.ordercontroller.OrderInsertRequest;
import com.ev112.codeblack.common.ordercontroller.OrderUpdateRequest;

public class IBAdapter implements OrderAdapter {

	@Override
	public void addOrder(OrderInsertRequest pEvent) throws OrderException {
		
	}

	@Override
	public void cancelOrder(OrderCancelRequest pEvent) throws OrderException {
		
	}

	@Override
	public void updateOrder(OrderUpdateRequest pEvent) throws OrderException {
		
	}

	@Override
	public List<StrategyServer_OwnOrder> getOrders() {
		
		return null;
	}

	@Override
	public List<StrategyServer_OwnTrade> getTrades() {
		return null;
	}

	@Override
	public List<PLPosition> getPositions() {
		return null;
	}

	@Override
	public AdapterMarketStatus getCurrentAdapterMarketStatus() {
		return null;
	}

}
