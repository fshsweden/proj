package com.ev112.codeblack.common.exchange;

public interface TopOfMarket {
	public Integer getBidQty();
	public Double getBid();
	public Double getAsk();
	public Integer getAskQty();
	
	public Double getLast();
	public Integer getLastQty();
	
	public Double getOpen();
	public Double getClose();  // previous
	public Double getHigh();
	public Double getLow();


	public void setBidQty(Integer bq);
	public void setBid(Double b);
	public void setAsk(Double a);
	public void setAskQty(Integer aq);
	
	public void setLast(Double l);
	public void setLastQty(Integer lq);
	
	public void setOpen(Double o);
	public void setClose(Double c);  // previous
	public void setHigh(Double h);
	public void setLow(Double l);
}
