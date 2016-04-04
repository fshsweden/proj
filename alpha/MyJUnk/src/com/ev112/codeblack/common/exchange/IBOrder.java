package com.ev112.codeblack.common.exchange;

import com.ib.client.Types;
import com.ib.client.Types.Action;

public class IBOrder implements Order {

	private Order newOrder = new Order();
	private OrderState2 state = OrderState2.None;

	public IBOrder() {
		
	}
	
	public Order getNewOrder() {
		return newOrder;
	}
	
	@Override
	public OrderState2 getOrderState() {
		return state;
	}

	@Override
	public Integer getInitialQty() {
		return newOrder.totalQuantity();
	}

	@Override
	public Integer getCurrentQty() {
		return newOrder.totalQuantity(); // CHECK!
	}

	@Override
	public Double getPrice() {
		return newOrder.lmtPrice();
	}

	@Override
	public Double getAuxPrice() {
		return newOrder.auxPrice();
	}

	@Override
	public OrderAction getAction() {
		
		switch (newOrder.action()) {
			default:
			case BUY:
				return OrderAction.BUY;
			case SELL:
				return OrderAction.SELL;
		}
	}

	@Override
	public TimeInForce getOrderTime() {
		switch (newOrder.tif()) {
			case DAY:
				return TimeInForce.EOD;
			
			case FOK:
			case IOC:
			case GTC:
			case GTD:
			case GTT:
			case AUC:
			case DTC:
			case GTX:
			case OPG:
			default:
				return TimeInForce.EOD;
			
		}
	}

	@Override
	public OrderType getOrderType() {
		switch (newOrder.orderType()) {
			case MKT:
				return OrderType.MKT;
			case LMT:
				return OrderType.LMT;
			case STP:
				return OrderType.STP;
				
			case BOX_TOP:
				break;
			case FIX_PEGGED:
				break;
			case LIT:
				break;
			case LMT_PLUS_MKT:
				break;
			case LOC:
				break;
			case MIT:
				break;
			case MKT_PRT:
				break;
			case MOC:
				break;
			case MTL:
				break;
			case None:
				break;
			case PASSV_REL:
				break;
			case PEG_BENCH:
				break;
			case PEG_MID:
				break;
			case PEG_MKT:
				break;
			case PEG_PRIM:
				break;
			case PEG_STK:
				break;
			case REL:
				break;
			case REL_PLUS_LMT:
				break;
			case REL_PLUS_MKT:
				break;
			case STP_LMT:
				break;
			case STP_PRT:
				break;
			case TRAIL:
				break;
			case TRAIL_LIMIT:
				break;
			case TRAIL_LIT:
				break;
			case TRAIL_LMT_PLUS_MKT:
				break;
			case TRAIL_MIT:
				break;
			case TRAIL_REL_PLUS_MKT:
				break;
			case VOL:
				break;
			case VWAP:
				break;
			default:
				break;
		}
		
		return OrderType.LMT;
	}

	@Override
	public String getExchangeOrderId() {
		return new Integer(newOrder.orderId()).toString();
	}

	@Override
	public String getOrderId() {
		return newOrder.orderRef();   // REF = ORDERID? OR.....????
	}

	@Override
	public void setOrderState(OrderState2 os) {
		state = os;
	}

	@Override
	public void setInitialQty(Integer iq) {
		newOrder.totalQuantity(iq);
	}

	@Override
	public void setCurrentQty(Integer cq) {
		newOrder.totalQuantity(cq);			// CEHCK!!!
	}

	@Override
	public void setPrice(Double p) {
		newOrder.lmtPrice(p);
	}

	@Override
	public void setAuxPrice(Double p) {
		newOrder.auxPrice(p);
	}

	@Override
	public void setAction(OrderAction a) {
		
		switch (a) {
			case BUY:
				newOrder.action(Action.BUY);
				break;
			case SELL:
				newOrder.action(Action.SELL);
				break;
			default:
				break;
			
		}
	}

	@Override
	public void setOrderTime(TimeInForce tf) {
		switch (tf) {
			case EOD:
				newOrder.tif(Types.TimeInForce.DAY);
				break;
			case GTC:
				newOrder.tif(Types.TimeInForce.GTC);
				break;
			case GTD:
				newOrder.tif(Types.TimeInForce.GTD);
			default:
				newOrder.tif(Types.TimeInForce.DAY);
				break;
			
		};
	}

	@Override
	public void setOrderType(OrderType ot) {
		
		switch (ot) {
			default:
			case LMT:
				newOrder.orderType(com.ib.controller.OrderType.LMT);
				break;
			case MKT:
				newOrder.orderType(com.ib.controller.OrderType.MKT);
				break;
			case STP:
				newOrder.orderType(com.ib.controller.OrderType.STP);
				break;
			case STP_LMT:
				newOrder.orderType(com.ib.controller.OrderType.STP_LMT);
				break;
		}
	}

	@Override
	public void setExchangeOrderId(String e) {
		newOrder.orderId(Integer.parseInt(e));		// REF = 
	}

	@Override
	public void setOrderId(String s) {
		newOrder.orderRef(s);
	}

	@Override
	public String toString() {
		
		StringBuilder str = new StringBuilder();
		str.append("Client Id:"); str.append(newOrder.clientId());
		str.append("\nOrder ID Id:"); str.append(newOrder.orderId());
		str.append("\nPerm Id:"); str.append(newOrder.permId());
		str.append("\nParent Id:"); str.append(newOrder.parentId());

		// primary attributes
		str.append("\nAccount:"); str.append(newOrder.account());
		str.append("\nAction:"); str.append(newOrder.action());
		str.append("\nTot Qty:"); str.append(newOrder.totalQuantity());
		str.append("\nDispQty:"); str.append(newOrder.displaySize());
		str.append("\nOrd Type:"); str.append(newOrder.orderType());
		str.append("\nLmt Prc:"); str.append(newOrder.lmtPrice());
		str.append("\nAux prc:"); str.append(newOrder.auxPrice());
		str.append("\nTif:"); str.append(newOrder.tif());
		
		return str.toString();
	}
}
