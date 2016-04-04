package com.ev112.codeblack.simpleclient.unsorted;

import java.util.ArrayList;
import java.util.List;

public abstract class Request {
	private int reqid;
	private int errorCode=0;
	private String errorMsg="";
	
	public Request(int reqid) {
		this.reqid = reqid;
	}
	
	public void error(int errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
	
	public int getReqId() {
		return reqid;
	}
	
	private List<RequestSubscriber> subscribers = new ArrayList<RequestSubscriber>();
	
	public interface RequestSubscriber {
		public void updated(Request r);
	}
	
	public void addSubscriber(RequestSubscriber s) {
		subscribers.add(s);
	}
	
	public void updated() {
		for (RequestSubscriber s : subscribers) {
			s.updated(this);
		}
	}
	
	public boolean got_error() {
		return errorCode != 0;
	}
	
	public int get_error() {
		return errorCode;
	}
	
	public String get_errormsg() {
		return errorMsg;
	}
}
