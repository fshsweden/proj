package com.ev112.codeblack.simpleclient.unsorted;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ev112.codeblack.pricecollector.ib.TwsMarketController;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.Execution;
import com.ib.client.ExecutionFilter;
import com.ib.controller.ApiController.ITradeReportHandler;

public class ExecutionsRequest extends Request {

	private TwsMarketController ibMarketCtrl;
	
	public ExecutionsRequest(TwsMarketController ctrl) {
		super(ctrl.getNextValidReqId());
		ibMarketCtrl = ctrl;
	}

	private Map<String,Execution> executions = new HashMap<String,Execution>();
	private Map<String,CommissionReport> commissions = new HashMap<String,CommissionReport>();
	private AtomicBoolean end_recvd = new AtomicBoolean();
	
	public void reqExecutions() {
		
		executions.clear();
		commissions.clear();
		end_recvd.set(false);
		
		ibMarketCtrl.reqExecutions(new ExecutionFilter(), new ITradeReportHandler() {
			
			@Override
			public void tradeReportEnd() {
				end_recvd.set(true);
			}
			
			@Override
			public void tradeReport(String tradeKey, Contract contract, Execution execution) {
				executions.put(tradeKey, execution);
			}
			
			@Override
			public void commissionReport(String tradeKey, CommissionReport commissionReport) {
				commissions.put(tradeKey, commissionReport);
			}
		});
	}

	public Map<String, Execution> getExecutions() {
		return executions;
	}

	public Map<String, CommissionReport> getCommissions() {
		return commissions;
	}
	
	public boolean isEndRecvd() {
		return end_recvd.get();
	}
}
