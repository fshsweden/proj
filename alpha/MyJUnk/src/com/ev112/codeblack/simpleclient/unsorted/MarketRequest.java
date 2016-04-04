package com.ev112.codeblack.simpleclient.unsorted;

import java.util.HashMap;
import java.util.Map;

import com.ev112.codeblack.pricecollector.ib.TwsMarketController;
import com.ib.client.TickType;
import com.ib.client.Types.MktDataType;
import com.ib.controller.ApiController.ITopMktDataHandler;

public class MarketRequest extends Request {

	private IBNewContract nc;
	private TwsMarketController ibMarketCtrl;
	
	private Map<String,Double> market_data = new HashMap<String,Double>();
	
	// flags
	private boolean got_bid;
	private boolean got_bidqty;
	private boolean got_ask;
	private boolean got_askqty;
	private boolean got_last;
	private boolean got_lastqty;
	private boolean got_open;
	private boolean got_high;
	private boolean got_low;
	private boolean got_volume;
	private boolean got_close;

	private Double	bid = new Double(0);
	private Integer	bidqty = new Integer(0);
	private Double	ask = new Double(0);
	private Integer	askqty = new Integer(0);
	private Double	last = new Double(0);
	private Integer	lastqty = new Integer(0);
	private Double	open = new Double(0);
	private Double	high = new Double(0);
	private Double	low = new Double(0);
	private Integer	volume = new Integer(0);
	private Double	close = new Double(0);
	
	
	public MarketRequest(TwsMarketController ctrl, IBNewContract newcontract) {
		super(ctrl.getNextValidReqId());
		ibMarketCtrl = ctrl;
		this.nc = newcontract;
	}

	@Override
	public void error(int errorCode, String errorMsg) {
		
		switch (errorCode) {
			case 200:	// No security definition has been found for the request
				System.out.println("Contract:" + nc.getID() + " " + errorMsg);
			break;
			
			case 321:	// Error validating request:-'ud' : cause - Please enter a local symbol or an expiry
				System.out.println("Contract:" + nc.getID() + " " + errorMsg);
			break;
			
			case 2117:	// Requested top market data is not subscribed. Subscription-independent ticks are still active.459; Error&IBIS/STK/Top&IBIS/STK/Top&IBIS/STK/Top&IBIS/STK/Top
				System.out.println("Contract:" + nc.getID() + " " + errorMsg);
			break;
		}
		
		super.error(errorCode, errorMsg);
	}
	
	public void reqTopMktData(final boolean snapshot) {
		
// Legal ones for (STK) are: 
//		100(Option Volume),
//		101(Option Open Interest),
//		105(Average Opt Volume),
//		106(Option Implied Volatility),
//		107(Close Implied Volatility),
//		125(Bond analytic data),
//		165(Misc. Stats),
//		166(CScreen),
//		225(Auction),
//		232/221(Mark Price),
//		233(RTVolume),
//		236(inventory),
//		258/47(Fundamentals),
//		291(Close Implied Volatility),
//		293(TradeCount),
//		294(TradeRate),
//		295(VolumeRate),
//		318(LastRTHTrade),
//		370(ParticipationMonitor),
//		370(ParticipationMonitor),
//		377(CttTickTag),
//		377(CttTickTag),
//		381(IB Rate),
//		384(RfqTickRespTag),
//		384(RfqTickRespTag),
//		387(DMM),
//		388(Issuer Fundamentals),
//		391(IBWarrantImpVolCompeteTick),
//		407(FuturesMargins),
//		411(Real-Time Historical Volatility),
//		428(Monetary Close Price),
//		439(MonitorTickTag),
//		439(MonitorTickTag),
//		456/59(IBDividends),
//		459(RTCLOSE),
//		460(Bond Factor Multiplier),
//		499(Fee and Rebate Rate),
//		506(MidPtImpVolTickTag),
//		511(HVOLAT10_PPD),
//		512/104(HVOLAT30_PPD),
//		513(HVOLAT50_PPD),
//		514(HVOLAT75_PPD),
//		515(HVOLAT100_PPD),
//		516(HVOLAT150_PPD),
//		517(HVOLAT200_PPD)
		
		
// Legal ones for (FUT) are: 
//		100(Option Volume),
//		101(Option Open Interest),
//		105(Average Opt Volume),
//		106(Option Implied Volatility),
//		107(Close Implied Volatility),
//		125(Bond analytic data),
//		165(Misc. Stats),
//		166(CScreen),
//		225(Auction),
//		232/221(Mark Price),
//		233(RTVolume),
//		236(inventory),
//		258/47(Fundamentals),
//		291(Close Implied Volatility),
//		293(TradeCount),294(TradeRate),
//		295(VolumeRate),
//		318(LastRTHTrade),
//		370(ParticipationMonitor),
//		370(ParticipationMonitor),
//		377(CttTickTag),
//		377(CttTickTag),
//		381(IB Rate),
//		384(RfqTickRespTag),
//		384(RfqTickRespTag),
//		387(DMM),
//		388(Issuer Fundamentals),
//		391(IBWarrantImpVolCompeteTick),
//		407(FuturesMargins),
//		411(Real-Time Historical Volatility),
//		428(Monetary Close Price),
//		439(MonitorTickTag),
//		439(MonitorTickTag),
//		456/59(IBDividends),
//		459(RTCLOSE),
//		460(Bond Factor Multiplier),
//		499(Fee and Rebate Rate),
//		506(MidPtImpVolTickTag),
//		511(HVOLAT10_PPD),
//		512/104(HVOLAT30_PPD),
//		513(HVOLAT50_PPD),
//		514(HVOLAT75_PPD),
//		515(HVOLAT100_PPD),
//		516(HVOLAT150_PPD),
//		517(HVOLAT200_PPD)		
		
		String fields="";
		if (!snapshot){
			fields="459";
		}
		ibMarketCtrl.reqTopMktData(nc, fields, snapshot, new ITopMktDataHandler() {
			
			@Override
			public void tickString(TickType tickType, String value) {
				System.out.println(nc.getID() + " " + tickType.toString() + " : --- TICKSTRING: " + value);
			}
			
			@Override
			public void tickSnapshotEnd() {
				System.out.println(nc.getID() + " : --- TICKSNAPSHOT END ----");
			}
			
			@Override
			public void tickSize(TickType tickType, int size) {
				// System.out.println("    " + nc.getID() + " : " + tickType.toString() + " size:" + size);
				
				market_data.put(tickType.toString(), new Double(size));
				
				switch (tickType) {
					case BID_SIZE:
						got_bidqty=true;
						bidqty = size;
					break;
					case ASK_SIZE:
						got_askqty=true;
						askqty = size;
					break;
					case LAST_SIZE:
						got_lastqty=true;
						lastqty = size;
					break;
					case VOLUME:
						got_volume=true;
						volume = size;
					break;
						
					case AVG_VOLUME:
					case OPEN_INTEREST:
					case OPTION_CALL_OPEN_INTEREST:
					case OPTION_PUT_OPEN_INTEREST:
					case OPTION_CALL_VOLUME:
					case OPTION_PUT_VOLUME:
					case AUCTION_VOLUME:
					case AUCTION_IMBALANCE:
						System.err.println("WARNING: Unusual field update : " + nc.getID() + tickType.toString() + " Value:" + size);
					break;
					
					case HALTED:
						System.err.println("WARNING: Trading is halted in : " + nc.getID() + " Value: " + size);
					break;
					
					default:
						System.err.println("ERROR: Unknown field update!  : " + nc.getID() + " Value: " + size);
					break;
				}
				
				updated();
			}
			
			/*
			 * NOTE: Handles BOTH tickPrice and tickGeneric!
			 * 
			 */
			@Override
			public void tickPrice(TickType tickType, double price, int canAutoExecute) {
				// System.out.println("    " + nc.getID() + " : " + tickType.toString() + " price:" + price);

				market_data.put(tickType.toString(), price);

				switch (tickType) {
					case BID:
						System.out.println(nc.getID() + " got BID:" + price);
						got_bid=true;
						bid = price;
					break;
					case ASK:
						System.out.println(nc.getID() + " got ASK:" + price);
						got_ask=true;
						ask = price;
					break;
					case LAST:
						System.out.println(nc.getID() + " got LAST:" + price);
						got_last=true;
						last = price;
					break;
					case HIGH:
						System.out.println(nc.getID() + " got HIGH:" + price);
						got_high=true;
						high = price;
					break;
					case LOW:
						System.out.println(nc.getID() + " got LOW:" + price);
						got_low=true;
						low = price;						
					break;
					
					// Both triggered by generic tick 459 ? 
					case CLOSE:
						System.out.println(nc.getID() + " got CLOSE:" + price);
						got_close=true;
						close = price;						
					break;	
					case OPEN:
						System.out.println(nc.getID() + " got OPEN:" + price);
						got_open=true;
						open = price;
					break;
					
					case LOW_13_WEEK:
					case HIGH_13_WEEK:
					case LOW_26_WEEK:
					case HIGH_26_WEEK:
					case LOW_52_WEEK:
					case HIGH_52_WEEK:
					case OPTION_HISTORICAL_VOL:
					case OPTION_IMPLIED_VOL:
					case OPTION_BID_EXCH:
					case OPTION_ASK_EXCH:
					case INDEX_FUTURE_PREMIUM:
					case BID_EXCH:
					case ASK_EXCH:
					case AUCTION_PRICE:
					case MARK_PRICE:
					case BID_EFP_COMPUTATION:
					case ASK_EFP_COMPUTATION:
					case LAST_EFP_COMPUTATION:
					case OPEN_EFP_COMPUTATION:
					case HIGH_EFP_COMPUTATION:
					case LOW_EFP_COMPUTATION:
					case CLOSE_EFP_COMPUTATION:
					case RT_VOLUME:
					case BID_YIELD:
					case ASK_YIELD:
					case LAST_YIELD:
					case TRADE_COUNT:
					case TRADE_RATE:
					case VOLUME_RATE:
					case LAST_RTH_TRADE:
					case RT_HISTORICAL_VOL:
						System.err.println("WARNING: Unusual field update : " + nc.getID() + tickType.toString() + " Value:" + price);
					break;
					
					case HALTED:
						System.err.println("WARNING: Trading is halted in : " + nc.getID() + " Value:" + price);
					break;
					
					default:
						System.err.println("WARNING: Unknown field update : " + nc.getID() + tickType.toString());
					break;
				}
			
				updated();
			}
			
			@Override
			public void marketDataType(MktDataType marketDataType) {
				System.err.println("    marketDataType:" + marketDataType.toString());
			}

			// Somehow signal that we're completed..........
		});
	}

	
	public boolean got_market() {
		return got_bid && got_ask && got_bidqty && got_askqty;
	}
	
	public boolean got_last() {
		return got_last && got_lastqty;
	}
	
	public boolean got_stats() {
		return got_open && got_high && got_low && got_volume && got_close;
	}
	
	public boolean got_close() {
		return got_close;
	}
	
	
	public Double getBid() {
		return bid;
	}

	public Integer getBidQty() {
		return bidqty;
	}

	public Double getAsk() {
		return ask;
	}

	public Integer getAskQty() {
		return askqty;
	}

	public Double getLast() {
		return last;
	}

	public Integer getLastQty() {
		return lastqty;
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

	public Integer getVolume() {
		return volume;
	}

	public Double getClose() {
		return close;
	}
	
	public IBNewContract getContract() {
		return nc;
	}
}
