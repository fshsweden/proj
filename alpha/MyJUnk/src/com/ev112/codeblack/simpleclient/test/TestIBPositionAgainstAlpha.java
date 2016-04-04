package com.ev112.codeblack.simpleclient.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ev112.codeblack.common.generated.messages.RiskController_PositionStatus;
import com.ev112.codeblack.common.generated.messages.RiskController_PositionStatusBdx;
import com.ev112.codeblack.pricecollector.ib.ITwsMarketControllerHandler;
import com.ev112.codeblack.pricecollector.ib.TwsMarketController;
import com.ev112.codeblack.simpleclient.alphasystem.AlphaSystem;
import com.ev112.codeblack.simpleclient.alphasystem.AlphaSystemModule;
import com.ev112.codeblack.simpleclient.alphasystem.AlphaSystemStatus;
import com.ev112.codeblack.simpleclient.alphasystem.IAlphaPositionUpdateHandler;
import com.ev112.codeblack.simpleclient.alphasystem.IAlphaSystemConnectionStatus;
import com.ib.client.Contract;
import com.ib.controller.ApiController.IPositionHandler;

public class TestIBPositionAgainstAlpha implements	
	IAlphaPositionUpdateHandler,
	IAlphaSystemConnectionStatus,
	ITwsMarketControllerHandler
{

	private Logger logger = LogManager.getLogger(getClass());
	private TwsMarketController	ibMarket;

	/*
	 * Connection Parameters - Edit these before use
	 */
	private String cfgHost="localhost";
	private String cfgPort="7777";
	private AlphaSystem alphaSystem;

	private String sTWSHost="192.168.2.199";	
	private Integer sTWSPort = 7496;
	private Integer sTWSClientId = 2;
	
	public static void main(String[] args) {
		new TestIBPositionAgainstAlpha();
	}

	/*
	 * A Data item to keep positions from both IB and Alpha
	 */
	public class PositionComparison {
		private String symbol;
		private Double ibpos;
		private Integer alphapos;
		
		public PositionComparison(String symbol, Double ibpos, Integer alphapos) {
			super();
			this.symbol = symbol;
			this.ibpos = ibpos;
			this.alphapos = alphapos;
		}

		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public Double getIbPos() {
			return ibpos;
		}

		public void setIbPos(Double ibpos) {
			this.ibpos = ibpos;
		}

		public Integer getAlphaPos() {
			return alphapos;
		}

		public void setAlphaPos(Integer alphapos) {
			this.alphapos = alphapos;
		}
	}
	
	private Map<String, PositionComparison> pos = new HashMap<String, PositionComparison>();
	
	/**
	 * 
	 */
	public TestIBPositionAgainstAlpha() {
	
		alphaSystem = new AlphaSystem("TEST");
		alphaSystem.connect(new IAlphaSystemConnectionStatus() {
			@Override
			public void alphaConnectionStatus(AlphaSystemModule module, AlphaSystemStatus status) {
				if (module == AlphaSystemModule.System && status == AlphaSystemStatus.Connected) {
					alphaSystem.SubscribeToPositionStatusBdx(TestIBPositionAgainstAlpha.this);	// will call IPositionUpdateHandler::riskDataUpdated
				}
				
			}
		});
		
		/*
		 * Connect to IB/TWS
		 */
		logger.info("Connecting to market at Host:" + sTWSHost + " port:" + sTWSPort);
		ibMarket = new TwsMarketController(this);
		ibMarket.connect(sTWSHost, sTWSPort, sTWSClientId,"");
		
		ibMarket.reqPositions(new IPositionHandler() {
			@Override
			public void positionEnd() {
				
			}
			@Override
			public void position(String account, Contract contract, double position, double avgCost) {
				System.out.println("\nIB Position:" + account + " " + contract.symbol() + " " + position + " " + avgCost + "\n");
				
				PositionComparison pc = pos.get(contract.symbol());
				if (pc == null) {
					pc = new PositionComparison(contract.symbol(), position, 0);
				}
				else {
					pc.setIbPos(position);
				}
				pos.put(contract.symbol(), pc);
			}
		});
		
		System.out.println("Sleeping 600s before exiting pgm....");
		sleepSec(600);
		System.exit(1);
	}

	/*
	 * 
	 */
	private void sleepSec(int secs) {
		try {
			Thread.sleep(secs * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * 
	 */
	@Override
	public void riskDataUpdated(RiskController_PositionStatusBdx pRiskUpdate) {
		
		System.out.println("Alpha: Total Exposure:" + pRiskUpdate.getTotalExposure());
		
		for(RiskController_PositionStatus p : pRiskUpdate.getPositions()) {
			if (p.getPosition() != 0) {
				System.out.println("    " + p.getSymbol() + " pos:" + p.getPosition() + " " + p.getProfitAndLoss() + " " + p.getUnmatchedProfitAndLoss());
			}
			
			PositionComparison pc = pos.get(p.getSymbol());
			if (pc == null) {
				pc = new PositionComparison(p.getSymbol(), 0d, p.getPosition());
			}
			else {
				pc.setAlphaPos(p.getPosition());
			}
			pos.put(p.getSymbol(), pc);
		}
		
		printPositionComparison();
	}
	
	/*
	 * 
	 */
	private void printPositionComparison() {
		System.out.println("------------------- POSITION COMPARISON ---------------------");
		for (PositionComparison pc : pos.values()) {
			System.out.println("SYMBOL:" + pc.getSymbol() + " IB:" + pc.getIbPos() + " ALPHA:" + pc.getAlphaPos());
		}
		System.out.println("------------------- POSITION COMPARISON END -----------------");
	}

	/*
	 * 
	 */
	@Override
	public void connectedToTws() {
		/* we're connected to Tws */
	}

	/*
	 * 
	 */
	@Override
	public void disconnectedFromTws() {
		/* we're disconnected from Tws */
	}

	/*
	 * 
	 */
	@Override
	public void infoFromTws(int id, int code, String msg) {
		/* we got a message from Tws (this will be obsolete, all errors should be connected to the Request */
	}

	/*
	 * 
	 */
	@Override
	public void alphaConnectionStatus(AlphaSystemModule module, AlphaSystemStatus status) {
		/* we're connected or diconnected from ALPHA */
	}

}
