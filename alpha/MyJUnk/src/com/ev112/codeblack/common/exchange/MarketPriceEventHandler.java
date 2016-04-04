package com.ev112.codeblack.common.exchange;

import com.ev112.codeblack.common.instmodel.Instrument;
import com.ib.client.TickType;

public interface MarketPriceEventHandler {
	public void priceUpdated(Integer tickerId, Instrument i, TickType field, Double price, Integer canAutoExecute);
	public void sizeUpdated(Integer tickerId, Instrument i, TickType field, Integer size);
	
	public void instrumentTraded(Integer tickerId, Instrument ii, Double last_trade_price, Integer last_trade_size, String last_trade_time, Long timestamp, Integer volume);
}
