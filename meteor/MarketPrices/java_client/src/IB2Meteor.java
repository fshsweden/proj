import java.util.HashMap;
import java.util.Map;

import com.ev112.codeblack.pricecollector.ib.IBTickType;

import mytrade.common_if.ConnectionEventHandler;
import mytrade.common_if.GenericSecType;
import mytrade.common_if.InformationEventHandler;
import mytrade.common_if.InformationType;
import mytrade.common_if.KeyValues;
import mytrade.common_if.MarketPriceDataItem;
import mytrade.common_if.MarketPriceEventHandler;
import mytrade.common_if.MarketQtyDataItem;
import mytrade.common_if.MyInstrument;
import mytrade.common_if.RetCodes;
import mytrade.common_if.SymbolEventHandler;
import mytrade.ib.IBMarketAPI;

public class IB2Meteor {
	
	public class Instrument {

		private String symbol;
		private String exchange;
		private String currency;
		private String expiry;
		
		private Double bid,ask,last,high,low,close;
		private Integer bidsize,asksize,lastsize,volume;
		
		public Integer getLastsize() {
			return lastsize;
		}

		public void setLastsize(Integer lastsize) {
			this.lastsize = lastsize;
		}

		public Double getBid() {
			return bid;
		}

		public void setBid(Double bid) {
			this.bid = bid;
		}

		public Double getAsk() {
			return ask;
		}

		public void setAsk(Double ask) {
			this.ask = ask;
		}

		public Double getLast() {
			return last;
		}

		public void setLast(Double last) {
			this.last = last;
		}

		public Double getHigh() {
			return high;
		}

		public void setHigh(Double high) {
			this.high = high;
		}

		public Double getLow() {
			return low;
		}

		public void setLow(Double low) {
			this.low = low;
		}

		public Double getClose() {
			return close;
		}

		public void setClose(Double close) {
			this.close = close;
		}

		public Integer getBidsize() {
			return bidsize;
		}

		public void setBidsize(Integer bidsize) {
			this.bidsize = bidsize;
		}

		public Integer getAsksize() {
			return asksize;
		}

		public void setAsksize(Integer asksize) {
			this.asksize = asksize;
		}

		public Integer getVolume() {
			return volume;
		}

		public void setVolume(Integer volume) {
			this.volume = volume;
		}

		public Instrument(String symbol, String exchange, String currency,
				String expiry) {
			super();
			this.symbol = symbol;
			this.exchange = exchange;
			this.currency = currency;
			this.expiry = expiry;
		}

		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public String getExchange() {
			return exchange;
		}

		public void setExchange(String exchange) {
			this.exchange = exchange;
		}

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

		public String getExpiry() {
			return expiry;
		}

		public void setExpiry(String expiry) {
			this.expiry = expiry;
		}
		
	}
	
	public interface InstrumentEventHandler {
		public void updated(Instrument i);
	}
	
	
	
	private IBMarketAPI mkt;
	private InstrumentEventHandler handler = null;
	
	public IB2Meteor(InstrumentEventHandler evt) {

		handler = evt;
		
		mkt = new IBMarketAPI();
		KeyValues kv = new KeyValues();
		kv.put("HOST", "192.168.10.22");
		kv.put("PORT", String.format("%d", 6661));
		kv.put("CLIENTID", String.format("%d",23));
		kv.put("ACCOUNT", "");
		
		mkt.configure(kv);
		
		ConnectionEventHandler cev = new ConnectionEventHandler() {
			@Override
			public void disconnected(KeyValues info) {
			}
			@Override
			public void connected(KeyValues info) {
				loadSomeSymbols();
			}
		};
		
		InformationEventHandler iev = new InformationEventHandler() {
			@Override
			public void information(InformationType it, KeyValues info) {
				// TODO implement this
			}
		};

		
		/* 
		 * Connect to the IB marketplace 
		 */
		System.out.println("Connecting to IB Market");
		mkt.configure(kv);
		RetCodes rc = mkt.connect(cev, iev);
		
		if (rc.get("STATUS").equals("OK")) {
			System.out.println("Connected OK to IB Market");
		}
		else {
			System.out.println("Failed to connect to IB TWS Host:" + "192.168.10.22"  + " Port:" + 6661);
		}
		
	}
	
	public static void main(String[] args) {
		new IB2Meteor(new InstrumentEventHandler() {
			
			@Override
			public void updated(Instrument i) {
				System.out.println("Instrument " + i.getSymbol() + " was updated: " + i.getBidsize() + " " + i.getBid() + " -- " + i.getAsk() + " " + i.getAsksize());
				
			}
		});
	}
	
	
	
	
	
	private void loadSomeSymbols() {

		
		Instrument i = new Instrument("ERIC.B","SFB","SEK","");
		instruments.put(generateKey(i), i);
		
		i = new Instrument("FING.B","SFB","SEK","");
		instruments.put(generateKey(i), i);
		i = new Instrument("VOLV.B","SFB","SEK","");
		instruments.put(generateKey(i), i);
		i = new Instrument("SEB.A","SFB","SEK","");
		instruments.put(generateKey(i), i);
		i = new Instrument("NDA","SFB","SEK","");
		instruments.put(generateKey(i), i);
		i = new Instrument("SAND","SFB","SEK","");
		instruments.put(generateKey(i), i);
		i = new Instrument("TLSN","SFB","SEK","");
		instruments.put(generateKey(i), i);
		i = new Instrument("SSAB.A","SFB","SEK","");
		instruments.put(generateKey(i), i);
		i = new Instrument("NOA3","HEX","EUR","");
		instruments.put(generateKey(i), i);
		i = new Instrument("TPSL","CPH","DKK","");
		instruments.put(generateKey(i), i);
		
		
		for (Instrument ii : instruments.values()) {
			
			System.out.println("Loading................");
			
			final GenericSecType gst = GenericSecType.STK;   // BUG
			final String symbol = ii.getSymbol();
			final String exch = ii.getExchange();
			final String currency = ii.getCurrency();
			final String expiry = ii.getExpiry();
			
			mkt.loadSymbols(gst, symbol, exch, currency, expiry, new SymbolEventHandler() {
				@Override
				public void symbolLoaded(MyInstrument myinstr) {
					
					System.out.println(">>>>>>>>>>>>>>> " + myinstr.getSymbol() + " " + expiry + " Loaded, starting subscription!");
					
					mkt.subscribeToInstrument(myinstr, new MarketPriceEventHandler() {
						
						@Override
						public void marketQtyUpdated(MyInstrument myinstr, MarketQtyDataItem data) {
							
							System.out.println(">>>>>>>>>>>>>>> " + myinstr.getSymbol() + " Qty update! " + data.getValue());
							
							IBTickType x = IBTickType.UNKNOWN;
							Long cur = System.currentTimeMillis();
							
							switch (data.getQtyType()) {
								case AskQty :
									x = IBTickType.ASK_SIZE;
									setField(symbol, exch, currency, expiry, IBTickType.ASK_SIZE, data.getValue());
								break;
								case BidQty :
									x = IBTickType.BID_SIZE;
									setField(symbol, exch, currency, expiry, IBTickType.BID_SIZE, data.getValue());
								break;
								case LastQty :
									x = IBTickType.LAST_SIZE;
									setField(symbol, exch, currency, expiry, IBTickType.LAST_SIZE, data.getValue());
								break;
								case Volume :
									x = IBTickType.VOLUME;
									setField(symbol, exch, currency, expiry, IBTickType.VOLUME, data.getValue());
								break;
								default :
								case None :
									System.out.println("Unknown Qty Type " + data.getQtyType().name());
								break;
							}
						}
						
						@Override
						public void marketPriceUpdated(MyInstrument myinstr, MarketPriceDataItem data) {
							
							System.out.println(">>>>>>>>>>>>>>> " + myinstr.getSymbol() + " Price update! " +  + data.getValue());
							
							IBTickType x = IBTickType.UNKNOWN;
							Long cur = System.currentTimeMillis();
							setField(symbol, IBTickType.TIMESTAMP, cur);
							
							switch (data.getPriceType()) {
								case Ask :
									x = IBTickType.ASK_PRICE;
									setField(symbol, exch, currency, expiry, IBTickType.ASK_PRICE, data.getValue());
									break;
								case Bid :
									x = IBTickType.BID_PRICE;
									setField(symbol, exch, currency, expiry, IBTickType.BID_PRICE, data.getValue());
									break;
								case Open :
									x = IBTickType.OPEN_TICK;
									setField(symbol, exch, currency, expiry, IBTickType.OPEN_TICK, data.getValue());
								break;
								case Close:
									x = IBTickType.CLOSE_PRICE;
									setField(symbol, exch, currency, expiry, IBTickType.CLOSE_PRICE, data.getValue());
								break;
								case High:
									x = IBTickType.HIGH;
									setField(symbol, exch, currency, expiry, IBTickType.HIGH, data.getValue());
								break;
								case Low :
									x = IBTickType.LOW;
									setField(symbol, exch, currency, expiry, IBTickType.LOW, data.getValue());
								break;
								case Last :
									x = IBTickType.LAST_PRICE;
									setField(symbol, exch, currency, expiry, IBTickType.LAST_PRICE, data.getValue());
									break;
								default :
								case None :
									System.out.println("Unknown Qty Type " + data.getPriceType().name());
									break;
							}
							
							// instrumentFieldUpdated(ii, x);
						}
						
						@Override
						public void publicTrade(MyInstrument myinstr, Double last_trade_price,Integer last_trade_size, Long last_trade_time, Integer volume) {
							
							System.out.println(">>>>>>>>>>>>>>> " + myinstr.getSymbol() + " Trade! " +  + last_trade_size + " " + last_trade_price);
							
							long cur_ms = System.currentTimeMillis();
							setField(symbol, IBTickType.LAST_TIMESTAMP, System.currentTimeMillis());
							
							instrumentTraded(symbol, 
								last_trade_price, 
								last_trade_size, 
								String.format("%d", last_trade_time), 
								cur_ms);
						}
					});
					
				}
				
				@Override
				public void noMoreSymbols() {
					System.out.println(">>>>>>>>>>>>>>> " + 
						symbol + " " + exch + " " + currency + " " + expiry + " No More Symbols!");
				}
			});
		}
	}
	
	private Map<String, Instrument> instruments = new HashMap<String,Instrument>();
	
	private void setField(String symbol, String exch, String currency, String expiry, IBTickType tickType, Double data) {
		
		String key = symbol + "-" + exch + "-" + currency + "-" + expiry;
		
		Instrument i = instruments.get(key);
		if (i == null) {
			i = new Instrument(symbol, exch,currency,expiry);
			instruments.put(key, i);
		}
		
		switch (tickType) {
			case BID_PRICE:
				i.setBid(data);
				break;
			case ASK_PRICE:
				i.setAsk(data);
				break;
			case LAST_PRICE:
				i.setLast(data);
				break;
			case HIGH:
				i.setHigh(data);
				break;
			case LOW:
				i.setLow(data);
				break;
			case CLOSE_PRICE:
				i.setClose(data);
				break;
		}

		handler.updated(i);
	}
	
	private void setField(String symbol, String exch, String currency, String expiry, IBTickType tickType, Integer data) {
		
		String key = symbol + "-" + exch + "-" + currency + "-" + expiry;
		
		Instrument i = instruments.get(key);
		if (i == null) {
			i = new Instrument(symbol, exch,currency,expiry);
			instruments.put(key, i);
		}
		
		switch (tickType) {
			case BID_SIZE:
				i.setBidsize(data);
				break;
			case ASK_SIZE:
				i.setAsksize(data);
				break;
			case LAST_SIZE:
				i.setLastsize(data);
				break;
			case VOLUME:
				i.setVolume(data);
				break;
		}
		
		handler.updated(i);
	}
	
	private void setField(String symbol, IBTickType tickType, String data) {
		
	}
	
	private void setField(String symbol, IBTickType tickType, long data) {
		
	}

	private String generateKey(Instrument i) {
		String key = i.getSymbol() + "-" + i.getExchange() + "-" + i.getCurrency() + "-" + i.getExpiry();
		return key;
	}
	private void instrumentTraded(String symbol, 
		Double last_trade_price, 
		Integer last_trade_size, 
		String last_trade_time, 
		long cur_ms) 
	{
		
	}
}

