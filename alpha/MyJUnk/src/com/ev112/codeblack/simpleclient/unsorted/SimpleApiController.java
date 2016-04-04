package com.ev112.codeblack.simpleclient.unsorted;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController;

public class SimpleApiController extends ApiController {

	int nextValidID = -1;
	AtomicBoolean nextOrderIdRcvd = new AtomicBoolean(false);
	
	public SimpleApiController(IConnectionHandler handler, ILogger inLogger, ILogger outLogger) {
		super(handler, inLogger, outLogger);
	}

	@Override
	public void connect(String host, int port, int clientId, String opts) {
		super.connect(host, port, clientId, opts);

		while (!nextOrderIdRcvd.get()) {
			System.out.println("Waiting for next valid order id!");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}

	@Override
	public void nextValidId(int orderId) {
		super.nextValidId(orderId);
		
		nextOrderIdRcvd.set(true);
	}
}
