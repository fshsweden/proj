package com.ev112.codeblack.simpleclient.unsorted;

import com.ib.client.Contract;

public class IBNewContract extends Contract {
	
	public String getID() {
		switch (secType()) {
			case None:
			return "";
			
			case STK:
			return symbol() + "/" + exchange() + "/" + currency();
			
			case OPT:
			break;
			
			case FUT:
				return symbol() + "/" + lastTradeDateOrContractMonth() + "/" + exchange() + "/" + currency();
			
			case CASH:
			case BOND:
			case CFD:
			case FOP:
			case WAR:
			case IOPT:
			case FWD:
			case BAG:
			case IND:
			case BILL:
			case FUND:
			case FIXED:
			case SLB:
			case NEWS:
			case CMDTY:
			case BSK:
			case ICU:
			case ICS:
			default:
		}
		return "???";
	}
}
