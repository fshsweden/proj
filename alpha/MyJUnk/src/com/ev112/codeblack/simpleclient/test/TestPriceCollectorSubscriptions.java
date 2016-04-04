package com.ev112.codeblack.simpleclient.test;

import java.io.IOException;

import com.ev112.codeblack.common.adapters.AdapterInterface;
import com.ev112.codeblack.common.adapters.AdapterEventHandler;
import com.ev112.codeblack.common.adapters.TcpIpClientAdapter;
import com.ev112.codeblack.common.generated.messages.PriceCollectorClockPulseBdx;
import com.ev112.codeblack.common.generated.messages.PriceCollectorQuerySymbolsTrackedReq;
import com.ev112.codeblack.common.generated.messages.PriceCollectorQuerySymbolsTrackedRsp;
import com.ev112.codeblack.common.generated.messages.PriceCollectorSubscribeReq;
import com.ev112.codeblack.common.generated.messages.PriceCollectorTradeBdx;
import com.ev112.codeblack.common.messaging.MessageInterface;
import com.ev112.codeblack.common.tcpip.TcpIpConnection;

public class TestPriceCollectorSubscriptions implements AdapterEventHandler {

	private TcpIpClientAdapter mPriceCollector;

	public TestPriceCollectorSubscriptions() {

		mPriceCollector = new TcpIpClientAdapter("name", "localhost", 9595, this);
		try {
			mPriceCollector.connect();
	
			PriceCollectorQuerySymbolsTrackedReq tRq1 = new PriceCollectorQuerySymbolsTrackedReq();
			try {
				mPriceCollector.sendMessage(tRq1);
			} catch (IOException e) {
				System.out.println("Exception:" + e.getLocalizedMessage());
			}
		
			
			PriceCollectorSubscribeReq tReq = new PriceCollectorSubscribeReq();
			tReq.setSymbol("LLOY");
			tReq.setFlows(PriceCollectorSubscribeReq.FLOW_TRADES);
			tReq.setIgnoreReplay(true);
			
			try {
				mPriceCollector.sendMessage(tReq);
			} catch (IOException e) {
				System.out.println("Exception:" + e.getLocalizedMessage());
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TestPriceCollectorSubscriptions();
	}

	@Override /* AdapterMessageCallbackInterface */
	public void adapterMessage(AdapterInterface pAdapterInterface, MessageInterface pMessage, TcpIpConnection pConnection) {
		
		System.out.println("Update!");
		
		if (pMessage instanceof PriceCollectorQuerySymbolsTrackedRsp)
		{
			PriceCollectorQuerySymbolsTrackedRsp tRsp = (PriceCollectorQuerySymbolsTrackedRsp)pMessage;
			String symbols[] = tRsp.getSymbols();
			for (String s : symbols) {
				System.out.println("TRACKED SYMBOL:" + s);
			}
		}
		
		if (pMessage instanceof PriceCollectorTradeBdx) {
			PriceCollectorTradeBdx rsp = (PriceCollectorTradeBdx)pMessage;
			System.out.println("UPDATE:" + rsp.toString());
		}
		
		if (pMessage instanceof PriceCollectorClockPulseBdx) {
			System.out.println("PULSE!!");
		}
		
		
	}

	@Override /* AdapterMessageCallbackInterface */
	public void adapterError(AdapterInterface pAdapterInterface, Throwable pException) {
		System.out.println("Error!");
	}

}
