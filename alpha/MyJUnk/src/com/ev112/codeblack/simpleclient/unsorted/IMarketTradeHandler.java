package com.ev112.codeblack.simpleclient.unsorted;

import com.ev112.codeblack.common.generated.messages.PriceCollectorTradeBdx;

public interface IMarketTradeHandler {
	public void marketTradeUpdated(PriceCollectorTradeBdx bdx);
}
