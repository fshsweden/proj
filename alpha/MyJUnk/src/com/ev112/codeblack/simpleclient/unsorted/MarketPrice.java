package com.ev112.codeblack.simpleclient.unsorted;

public class MarketPrice {
	
	private String symbol;
	private long timestamp;
	private Integer bidqty;
	private Double bid;
	private Double ask;
	private Integer askqty;
	private Integer lastqty;
	private Double last;
	private Double open;
	private Double close;
	private Double high;
	private Double low;
	private Double totvol;
	public Boolean is_replay;
	
	public MarketPrice() {
		this.symbol = "";
		this.bidqty = 0;
		this.bid = 0.0;
		this.ask = 0.0;
		this.askqty = 0;
		this.lastqty = 0;
		this.last = 0.0;
		this.open = 0.0;
		this.close = 0.0;
		this.high = 0.0;
		this.low = 0.0;
		this.totvol = 0.0;
		this.is_replay = false;
	}
	
	public MarketPrice(String symbol, Integer bidqty, Double bid, Double ask, Integer askqty, Integer lastqty, Double last, Double open, Double close, Double high, Double low, Double totvol, Boolean is_replay) {
		super();
		this.symbol = symbol;
		this.bidqty = bidqty;
		this.bid = bid;
		this.ask = ask;
		this.askqty = askqty;
		this.lastqty = lastqty;
		this.last = last;
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
		this.totvol = totvol;
		this.is_replay = is_replay;
	}

	public Integer getBidqty() {
		return bidqty;
	}

	public void setBidQty(Integer bidqty) {
		this.bidqty = bidqty;
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

	public Integer getAskQty() {
		return askqty;
	}

	public void setAskQty(Integer askqty) {
		this.askqty = askqty;
	}

	public Integer getLastQty() {
		return lastqty;
	}

	public void setLastQty(Integer lastqty) {
		this.lastqty = lastqty;
	}

	public Double getLast() {
		return last;
	}

	public void setLast(Double last) {
		this.last = last;
	}

	public Double getOpen() {
		return open;
	}

	public void setOpen(Double open) {
		this.open = open;
	}

	public Double getClose() {
		return close;
	}

	public void setClose(Double close) {
		this.close = close;
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

	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol; 
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long update_time) {
		this.timestamp = update_time;
	}

	public Integer getAskqty() {
		return askqty;
	}

	public void setAskqty(Integer askqty) {
		this.askqty = askqty;
	}

	public Integer getLastqty() {
		return lastqty;
	}

	public void setLastqty(Integer lastqty) {
		this.lastqty = lastqty;
	}

	public void setBidqty(Integer bidqty) {
		this.bidqty = bidqty;
	}

	public Double getTotvol() {
		return totvol;
	}

	public void setTotvol(Double totvol) {
		this.totvol = totvol;
	}

	public Boolean getIsReplay() {
		return is_replay;
	}

	public Boolean getIs_replay() {
		return is_replay;
	}

	public void setIsReplay(Boolean is_replay) {
		this.is_replay = is_replay;
	}
	
	
}
