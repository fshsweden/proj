package com.ev112.codeblack.simpleclient.ibexamples;

import java.util.ArrayList;

import com.ev112.codeblack.pricecollector.ib.ITwsMarketControllerHandler;
import com.ib.controller.ApiController;

public class IBController extends ApiController {
	
	private final IConnectionHandler connHandler = new IConnectionHandler() {
		
		@Override
		public void show(String msg) {
		}
		
		@Override
		public void message(int id, int errorCode, String errorMsg) {
		}
		
		@Override
		public void error(Exception e) {
		}
		
		@Override
		public void disconnected() {
		}
		
		@Override
		public void connected() {
		}
		
		@Override
		public void accountList(ArrayList<String> list) {
			/* GULP */
		}
	};

	public IBController(ITwsMarketControllerHandler handler) {
		super();
		setConnectionHandler(connHandler);
		// ibMarketControllerHandler = handler;
	}
}
