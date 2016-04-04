package com.ev112.codeblack.simpleclient.ibexamples;

import java.util.HashMap;
import java.util.Map;

import com.ev112.codeblack.pricecollector.ib.TwsMarketController;
import com.ev112.codeblack.simpleclient.unsorted.IBNewContract;
import com.ev112.codeblack.simpleclient.unsorted.Request;
import com.ib.controller.ApiController.IHistoricalDataHandler;
import com.ib.controller.Bar;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.DurationUnit;
import com.ib.controller.Types.WhatToShow;

public class HistoricalDataRequest extends Request {

	private IBNewContract nc;
	private TwsMarketController ibMarketCtrl;
	
	public class data {
		private Double open,high,low,close;
		
		public data(Double open, Double high, Double low, Double close) {
			this.open = open;
			this.high = high;
			this.low = low;
			this.close = close;
		}

		public Double getOpen() {
			return open;
		}

		public Double getHigh() {
			return high;
		}

		public Double getLow() {
			return low;
		}

		public Double getClose() {
			return close;
		}
	}
	
	private Map<String, data> histdata = new HashMap<String,data>();
	
	public HistoricalDataRequest(TwsMarketController ctrl, IBNewContract newcontract) {
		super(ctrl.getNextValidReqId());
		ibMarketCtrl = ctrl;
		this.nc = newcontract;
	}
	
	public void reqHistoricalData() {
		System.out.println("Requesting historical data for:" + nc.toString() + " NEXT VALID REQID IS:" + ibMarketCtrl.getNextValidReqId());

		ibMarketCtrl.reqHistoricalData(nc, "20140922 22:15:00 GMT", 3, DurationUnit.DAY, BarSize._1_day, WhatToShow.TRADES, true, new IHistoricalDataHandler() {
			
			@Override
			public void historicalDataEnd() {
				// System.out.println("DATA END");
				// SIGNAL
			}
			
			@Override
			public void historicalData(Bar bar, boolean hasGaps) {
				// System.out.println("DATA: symbol:" + nc.symbol() + " " + " date:" + bar.formattedTime() + " Closing Price:" + bar.close() + " has gaps:" + hasGaps);
				
				String symbol = nc.symbol();
				String date = bar.formattedTime().substring(0, 7);
				
				// adb.addClosingPrice(c, date, bar.open(), bar.high(), bar.low(), bar.close());
				data d = new data(bar.open(), bar.high(), bar.low(), bar.close());
				histdata.put(date, d);
			}
		});
		
	}

}
