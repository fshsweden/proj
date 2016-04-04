package com.ev112.codeblack.simpleclient.test;

import java.util.List;

import com.ev112.codeblack.atc.connections.CONNECTION_STATUS;
import com.ev112.codeblack.atc.connections.ServerConnection;
import com.ev112.codeblack.atc.connections.ServerConnectionEventHandler;
import com.ev112.codeblack.atc.connections.StrategyConnectionCallback;
import com.ev112.codeblack.atc.connections.StrategyServerConnection;
import com.ev112.codeblack.common.configuration.Configuration;
import com.ev112.codeblack.common.generated.messages.PLStrategy;
import com.ev112.codeblack.common.generated.messages.StatusEvent;
import com.ev112.codeblack.common.generated.messages.StrategyServer_OwnOrder;
import com.ev112.codeblack.common.generated.messages.StrategyServer_OwnTrade;

public class TestStrategyControllerCLI {

	public static void main(String[] args) {
		new TestStrategyControllerCLI();
	}
	
	private StrategyServerConnection mStrategyServer;
	
	public TestStrategyControllerCLI() {
		
		boolean pInSwing = false;
		String pHostOverride = null;
		
		//   L O A D   C O N F I G U R A T I O N
		Configuration mConfigurationLoader = new Configuration("TEST");

		
		// S E T U P   A   S E R V E R   C O N N E C T I O N   C A L L B A C K
		
		ServerConnectionEventHandler serverConnectionCallback = new ServerConnectionEventHandler() {

			@Override
			public void serverStatisticsUpdate() {
				System.out.println("ServerConnection serverStatisticsUpdate");
			}

			@Override
			public void connectionStatusChangeCallback(ServerConnection pConnection, CONNECTION_STATUS pStatus) {
				System.out.println("ServerConnection status change:" + pStatus.name());
				
				if (pStatus == CONNECTION_STATUS.CONNECTED) {
					System.out.println("Connected to Strategy Server, Starting Strategy One_Trade_text.xml");
					//mStrategyServer.startStrategy("One_Trade_Test");
					mStrategyServer.unloadStrategy("One_Trade_Test");
				}
			}
		};
		
		
		//   S E T U P   S T R A T E G Y   C O N N E C T I O N   C A L L B A C K
		StrategyConnectionCallback strategyConnectionCallback = new StrategyConnectionCallback() {

			@Override
			public void addEvent(List<StatusEvent> pEvents) {
				// TODO Auto-generated method stub
			}

			@Override
			public void strategyListUpdated(List<PLStrategy> pStrategies) {
				System.out.println("updateListOfStrategies");
				for (PLStrategy sty : pStrategies) {
					System.out.println("Strategy:" + sty.getStrategyId());
				}
			}

			@Override
			public void setMatcher(String pMatcher) {
				System.out.println("setMatcher(" + pMatcher + ")");
			}

			@Override
			public void addOrder(StrategyServer_OwnOrder pOrder, boolean pBroadcast) {
				System.out.println("addOrder()");
			}

			@Override
			public void addTrade(StrategyServer_OwnTrade pTrade, boolean pBroadcast) {
				System.out.println("addTrade()");
			}

			@Override
			public void strategyLoaded(String strategy, Integer status) {
				
				
			}

			@Override
			public void strategyUnloaded(String strategy, Integer status) {
				
				
			}

			@Override
			public void strategyListReply(List<PLStrategy> pStrategies) {
				// TODO implement this
				strategyListUpdated(pStrategies);
			}

		};
		
		
		mStrategyServer = new StrategyServerConnection(
				serverConnectionCallback, 
				strategyConnectionCallback, 
				mConfigurationLoader, 
				"StrategyServer", 
				pInSwing);
		
		mStrategyServer.start();
	}

}
