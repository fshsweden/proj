package com.ev112.codeblack.common.exchange;

public class IBTopOfMarket implements TopOfMarket {

	private Integer bidqty,askqty,lastqty;
	private Double bid,ask,last,open,close,high,low;
	
	@Override
	public Integer getBidQty() {
		return bidqty;
	}

	@Override
	public Double getBid() {
		return bid;
	}

	@Override
	public Double getAsk() {
		return ask;
	}

	@Override
	public Integer getAskQty() {
		return askqty;
	}

	@Override
	public Double getLast() {
		return last;
	}

	@Override
	public Integer getLastQty() {
		return lastqty;
	}

	@Override
	public Double getOpen() {
		return open;
	}

	@Override
	public Double getClose() {
		return close;
	}

	@Override
	public Double getHigh() {
		return high;
	}

	@Override
	public Double getLow() {
		return low;
	}

	
	@Override
	public void setBidQty(Integer bq) {
		bidqty = bq;
	}

	@Override
	public void setBid(Double b) {
		bid = b;
	}

	@Override
	public void setAsk(Double a) {
		ask = a;
	}

	@Override
	public void setAskQty(Integer aq) {
		askqty = aq;
	}

	@Override
	public void setLast(Double l) {
		last = l;
	}

	@Override
	public void setLastQty(Integer lq) {
		lastqty = lq;
	}

	@Override
	public void setOpen(Double o) {
		open = o;
	}

	@Override
	public void setClose(Double c) {
		close = c;
	}

	@Override
	public void setHigh(Double h) {
		high = h;
	}

	@Override
	public void setLow(Double l) {
		low = l;
	}

}
