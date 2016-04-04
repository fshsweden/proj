package com.ev112.codeblack.simpleclient.ibexamples;

import com.ib.controller.NewContract;

/*
 * Create an artificial key that can be constructed from NewContract instead !!!!!!!!!!!!
 * 
 * 
 */
public class ContractDef {

	private NewContract nc;
	
	public ContractDef(NewContract nc) {
		this.nc = nc;
	}
	
	@Override
	public int hashCode() {
		
		switch (nc.secType()) {
			case FWD:
			case FUT:
				break;
			case STK:
				break;
			case OPT:
				break;
				
			case BAG:
			case BILL:
			case BOND:
			case BSK:
			case CASH:
			case CFD:
			case CMDTY:
			case FIXED:
			case FOP:
			case FUND:
			case ICS:
			case ICU:
			case IND:
			case IOPT:
			case NEWS:
			case None:
			case SLB:
			case WAR:
			default:
				break;
			
		}
		
		return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
	            // if deriving: appendSuper(super.hashCode()).
	            append(nc.conid()).
	            append(nc.symbol()).
	            append(nc.secType()).
	            append(nc.expiry()).
	            append(nc.strike()).
	            append(nc.right()).
	            append(nc.multiplier()).
	            append(nc.symbol()).
	            append(nc.symbol()).
	            append(nc.symbol()).
	            append(nc.symbol()).
	            append(nc.symbol()).
	            append(nc.symbol()).
	            toHashCode();
		
	    /* private int m_conid;
		private String m_symbol;
	    private SecType m_secType = SecType.None;
	    private String m_expiry;
	    private double m_strike;
	    private Right m_right = Right.None;
	    private String m_multiplier; // should be double
	    private String m_exchange;
	    private String m_currency;
	    private String m_localSymbol;
	    private String m_tradingClass;
	    private String m_primaryExch;
	    private SecIdType m_secIdType = SecIdType.None;
	    private String m_secId;*/
	}

	@Override
	public boolean equals(Object obj) {
		
/*		
		f (!(obj instanceof Person))
        return false;
    if (obj == this)
        return true;

    Person rhs = (Person) obj;
    return new EqualsBuilder().
        // if deriving: appendSuper(super.equals(obj)).
        append(name, rhs.name).
        append(age, rhs.age).
        isEquals();	}
*/
		return false;
	}
}
