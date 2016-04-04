package com.ev112.codeblack.simpleclient.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.ev112.codeblack.atc.connections.CONNECTION_STATUS;
import com.ev112.codeblack.atc.connections.PriceCollectorConnection;
import com.ev112.codeblack.atc.connections.PriceCollectorConnectionEventHandler;
import com.ev112.codeblack.atc.connections.StatusEventHandler;
import com.ev112.codeblack.atc.connections.ServerConnection;
import com.ev112.codeblack.atc.connections.ServerConnectionEventHandler;
import com.ev112.codeblack.common.configuration.Configuration;
import com.ev112.codeblack.common.generated.messages.PriceCollectorClockPulseBdx;
import com.ev112.codeblack.common.generated.messages.PriceCollectorFacilityBdx;
import com.ev112.codeblack.common.generated.messages.PriceCollectorQuoteBdx;
import com.ev112.codeblack.common.generated.messages.PriceCollectorReplayStartedBdx;
import com.ev112.codeblack.common.generated.messages.PriceCollectorReplayStoppedBdx;
import com.ev112.codeblack.common.generated.messages.PriceCollectorTradeBdx;
import com.ev112.codeblack.common.generated.messages.StatusEvent;
import com.ev112.codeblack.common.instmodel.InstrumentModel;
import com.ev112.codeblack.simpleclient.unsorted.SortedStringArray;

public class TestPriceCollectorSwing {

	private JFrame frame;
	private JTable table;


	public class Item {
		private String symbol;
		private Integer bidQty,askQty;
		private Double bid,ask;
		
		public Item(String symbol, Integer bidQty, Integer askQty, Double bid,Double ask) {
			super();
			this.symbol = symbol;
			this.bidQty = bidQty;
			this.askQty = askQty;
			this.bid = bid;
			this.ask = ask;
		}

		public String getSymbol() {
			return symbol;
		}

		public Integer getBidQty() {
			return bidQty;
		}

		public Integer getAskQty() {
			return askQty;
		}

		public Double getBid() {
			return bid;
		}

		public Double getAsk() {
			return ask;
		}
		
	}
	
	public class MyModel extends DefaultTableModel {

		private static final long serialVersionUID = -6790470663986989025L;
		
		private SortedStringArray keys = new SortedStringArray();
		private Map<String,Item> items = new HashMap<String,Item>();

		public MyModel() {
			addItem("VOLV.B", new Item("VOLV.B",111,111,11.1,12.1));
			addItem("MTG.B" , new Item("MTG.B" ,222,222,22.1,23.1));
			addItem("ATCO.A", new Item("ATCO.A",333,333,33.1,34.1));
		}
		
		public void addItem(String key, Item item) {
			keys.add(key);
			items.put(key, item);
			
			int row = keys.findIndex(key);
			fireTableRowsUpdated(row, row);
		}
		
		@Override
		public int getRowCount() {
			if (keys == null || keys.getKeyArray() == null) {
				return 0;
			}
			else
				return keys.getKeyArray().size();
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
				case 0:
					return "Symbol";
				case 1:
					return "BidQty";
				case 2:
					return "Bid";
				case 3:
					return "Ask";
				case 4:
					return "AskQty";
			}
			return "";
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
				case 0:
					return String.class;
				case 1:
					return Integer.class;
				case 2:
					return Double.class;
				case 3:
					return Double.class;
				case 4:
					return Integer.class;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			String key = keys.getKeyArray().get(rowIndex);
			Item i = items.get(key);
			switch (columnIndex) {
				case 0: return i.getSymbol();
				case 1: return i.getBidQty();
				case 2: return i.getBid();
				case 3: return i.getAsk();
				case 4: return i.getAskQty();
			}
			return "";
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
		}

		@Override
		public void addTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub
		}
		
	}
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestPriceCollectorSwing window = new TestPriceCollectorSwing();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TestPriceCollectorSwing() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 865, 546);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		table = new JTable();
		frame.getContentPane().add(table, BorderLayout.CENTER);
		
		table.setModel(new MyModel());
		
		
		
		// L O A D C O N F I G U R A T I O N
		Configuration conf = new Configuration("TEST");
		InstrumentModel instrModel = conf.getInstrumentModel();
		
		/**
		 * 
		 */
		final PriceCollectorConnectionEventHandler ftl = new PriceCollectorConnectionEventHandler() {
			@Override
			public void priceCollectorTradeEvent(PriceCollectorTradeBdx pBdx) {
				// System.out.println("TradeBdx:" + pBdx.toString());
				System.out.println("TradeBdx:" + pBdx.getPrice());
			}

			@Override
			public void priceCollectorQuoteEvent(PriceCollectorQuoteBdx pBdx) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void priceCollectorClockPulseEvent(PriceCollectorClockPulseBdx pBdx) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void facilityUpdate(PriceCollectorFacilityBdx pBdx) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void priceCollectorStatusEvent(List<StatusEvent> pEvents) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void priceCollectorReplayStarted(PriceCollectorReplayStartedBdx pBdx) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void priceCollectorReplayStopped(PriceCollectorReplayStoppedBdx pBdx) {
				// TODO Auto-generated method stub
				
			}
		};
		
		/**
		 * 
		 */
		ServerConnectionEventHandler scb = new ServerConnectionEventHandler() {
			
			@Override
			public void serverStatisticsUpdate() {
				
			}

			@Override
			public void connectionStatusChangeCallback(ServerConnection pConnection, CONNECTION_STATUS pStatus) {
				System.out.println("connectionStatusChangeCallback() - value :" + pStatus.name());

				if (pStatus == CONNECTION_STATUS.CONNECTED) {
					
					PriceCollectorConnection p = (PriceCollectorConnection) pConnection;
					p.subscribeTrades("SAND", new PriceCollectorConnectionEventHandler() {
						
						@Override
						public void priceCollectorTradeEvent(PriceCollectorTradeBdx pBdx) {
							
						}

						@Override
						public void priceCollectorQuoteEvent(PriceCollectorQuoteBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorClockPulseEvent(PriceCollectorClockPulseBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void facilityUpdate(PriceCollectorFacilityBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorStatusEvent(List<StatusEvent> pEvents) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorReplayStarted(PriceCollectorReplayStartedBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorReplayStopped(PriceCollectorReplayStoppedBdx pBdx) {
							// TODO Auto-generated method stub
							
						}
						
					});
					
					p.subscribeTrades("BOL", new PriceCollectorConnectionEventHandler() {
						@Override
						public void priceCollectorTradeEvent(PriceCollectorTradeBdx pBdx) {
							System.out.println(pBdx.getSymbol() + " Price:" + pBdx.getPrice() + " Col:" + pBdx.getVolume());
						}

						@Override
						public void priceCollectorQuoteEvent(PriceCollectorQuoteBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorClockPulseEvent(PriceCollectorClockPulseBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void facilityUpdate(PriceCollectorFacilityBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorStatusEvent(List<StatusEvent> pEvents) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorReplayStarted(PriceCollectorReplayStartedBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorReplayStopped(PriceCollectorReplayStoppedBdx pBdx) {
							// TODO Auto-generated method stub
							
						}
					});

					p.subscribeTrades("FB", new PriceCollectorConnectionEventHandler() {
						@Override
						public void priceCollectorTradeEvent(PriceCollectorTradeBdx pBdx) {
							System.out.println(pBdx.getSymbol() + " Price:" + pBdx.getPrice() + " Col:" + pBdx.getVolume());
						}

						@Override
						public void priceCollectorQuoteEvent(PriceCollectorQuoteBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorClockPulseEvent(PriceCollectorClockPulseBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void facilityUpdate(PriceCollectorFacilityBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorStatusEvent(List<StatusEvent> pEvents) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorReplayStarted(PriceCollectorReplayStartedBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorReplayStopped(PriceCollectorReplayStoppedBdx pBdx) {
							// TODO Auto-generated method stub
							
						}
					});
					
					p.subscribeTrades("TWTR", new PriceCollectorConnectionEventHandler() {
						@Override
						public void priceCollectorTradeEvent(PriceCollectorTradeBdx pBdx) {
							System.out.println(pBdx.getSymbol() + " Price:" + pBdx.getPrice() + " Col:" + pBdx.getVolume());
						}

						@Override
						public void priceCollectorQuoteEvent(PriceCollectorQuoteBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorClockPulseEvent(PriceCollectorClockPulseBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void facilityUpdate(PriceCollectorFacilityBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorStatusEvent(List<StatusEvent> pEvents) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorReplayStarted(PriceCollectorReplayStartedBdx pBdx) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void priceCollectorReplayStopped(PriceCollectorReplayStoppedBdx pBdx) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}
		};

		StatusEventHandler pcb = new StatusEventHandler() {
			@Override
			public void addEvent(List<StatusEvent> pEvents) {
				for (StatusEvent e : pEvents) {
					System.out.println(e.getMessage());
				}
			}
		};
		
		System.out.println("Connecting to PriceCollector");
		
		/* Break up this class - too many args in constructor */
		PriceCollectorConnection p = new PriceCollectorConnection(
				scb, pcb, conf, "PriceCollector", /*inSwing*/true);
		
		p.subscribeQuotes("SAND", new PriceCollectorConnectionEventHandler() {
			@Override
			public void priceCollectorQuoteEvent(PriceCollectorQuoteBdx pBdx) {
				MyModel m = (MyModel)table.getModel();
				Item item = new Item(pBdx.getSymbol(), 0, 0, pBdx.getBid(), pBdx.getAsk());
				m.addItem(pBdx.getSymbol(), item);
			}

			@Override
			public void priceCollectorTradeEvent(PriceCollectorTradeBdx pBdx) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void priceCollectorClockPulseEvent(PriceCollectorClockPulseBdx pBdx) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void facilityUpdate(PriceCollectorFacilityBdx pBdx) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void priceCollectorStatusEvent(List<StatusEvent> pEvents) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void priceCollectorReplayStarted(PriceCollectorReplayStartedBdx pBdx) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void priceCollectorReplayStopped(PriceCollectorReplayStoppedBdx pBdx) {
				// TODO Auto-generated method stub
				
			}
		});
		
		p.start(); /*  inkonsekvent, denna klass är tyligen en Thread.... */
		
	}

}
