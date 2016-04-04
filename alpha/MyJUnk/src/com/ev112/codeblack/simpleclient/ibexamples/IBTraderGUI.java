package com.ev112.codeblack.simpleclient.ibexamples;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;

import com.ib.controller.NewContractDetails;

import mytrade.ib.IBMarketAPI.ContractInfoHandler;
import net.miginfocom.swing.MigLayout;

public class IBTraderGUI extends JDialog {

	private final JPanel	contentPanel	= new JPanel();
	private MarketAPI		trader;

	private DefaultListModel<String>	contractListModel	= new DefaultListModel<String>();
	private DefaultListModel<String>	underlyingsModel	= new DefaultListModel<String>();

	private JList<String>	underlyingsList;
	private JXList	contractList;

	private Set<String>						underlyings	= new HashSet<String>();
	private Map<String, NewContractDetails>	derivatives	= new HashMap<String, NewContractDetails>();

	private NewContractDetails selected_contract;
	private ContractPanel contractPanel;
	
	ContractInfoHandler cih = new ContractInfoHandler() {
		@Override
		public void contractInfo(NewContractDetails ncd) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					addUnderlying(ncd);
				}
			});
		}
	};

	ContractInfoHandler derivHandler = new ContractInfoHandler() {
		@Override
		public void contractInfo(NewContractDetails ncd) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					addDerivative(ncd);
				}
			});
		}
	};
	private JTextField edUnderlying;
	private JLabel lblUnderlying;

	/**
	 * Create the dialog.
	 */
	public IBTraderGUI(MarketAPI trader) {

		this.trader = trader;
		
		addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				// TODO implement this
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				saveWindowPrefs(IBTraderGUI.this, e);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				saveWindowPrefs(IBTraderGUI.this, e);
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO implement this
			}
		});
		
		setBounds(100, 100, 1024, 550);
		loadWindowPrefs(IBTraderGUI.this);

		// MigLayout mig = new MigLayout("debug", "[100px][grow][grow]", "[][grow][200px]");
		MigLayout mig = new MigLayout("", "[100px][grow][grow]", "[][grow][200px]");
		
		contentPanel.setLayout(mig);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		
		/*
		 * 
		 *  N O R T H
		 * 
		 * 
		 * 
		 */
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.NORTH);

			lblUnderlying = new JLabel("Underlying");
			buttonPane.add(lblUnderlying);
			
			edUnderlying = new JTextField();
			buttonPane.add(edUnderlying);
			edUnderlying.setColumns(10);
			
			JButton pbAddUnderlying = new JButton("Add");
			pbAddUnderlying.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (edUnderlying.getText().equals("")) {
						
					}
					else {
						
						// Replace by LoadSymbol!
						// trader.requestSymbols(/* error IB specific! */SecType.STK, edUnderlying.getText(), cih);
						// trader.requestSymbols(/* error IB specific! */SecType.IND, edUnderlying.getText(), cih);
						
						addPref(edUnderlying.getText());
					}
				}
			});
			buttonPane.add(pbAddUnderlying);
			
			getRootPane().setDefaultButton(pbAddUnderlying);
		}		
		
		
		
		
		/*
		 * 
		 *  C E N T E R
		 * 
		 * 
		 * 
		 */
		
		
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		/* UNDERLYINGS LISTBOX */
		{
			underlyingsList = new JList<String>(underlyingsModel);
			underlyingsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			underlyingsList.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						JList l = (JList) e.getSource();
						System.out.println("Selection:" + l.getSelectedValue());

						contractListModel.clear();
						derivatives.clear();
						selected_contract = null;
						contractPanel.updateDialog(selected_contract);

						// Replace by LoadSymbol()
						// trader.requestSymbols(/* error IB specific! */SecType.FUT, (String) l.getSelectedValue(), derivHandler);
					}
				}
			});
			
			JLabel lblUnderlying = new JLabel("Underlying");
			contentPanel.add(lblUnderlying, "flowx,cell 0 0");
			JScrollPane scrollPane = new JScrollPane(underlyingsList);
			contentPanel.add(scrollPane, "cell 0 1,grow");
		}

		/* DERIVATIVES LISTBOX */
		{
			JScrollPane scrollPane = new JScrollPane();
			contractList = new JXList(contractListModel);
			contractList.setSortable(true);
			contractList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			contractList.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {

					if (!e.getValueIsAdjusting()) {
						JList l = (JList) e.getSource();
						System.out.println("Selection:" + l.getSelectedValue());

						// use selectedValue to find NewContract!
						NewContractDetails ncd = derivatives.get(l.getSelectedValue());
						if (ncd != null) {
							selected_contract = ncd;
							contractPanel.updateDialog(selected_contract);
							
						} else {
							System.out.println("ERROR.....");
						}
					}
				}
			});
			JLabel lblUnderlying = new JLabel("Stocks & Futures definitions");
			contentPanel.add(lblUnderlying, "flowx,cell 1 0");
			scrollPane.setViewportView(contractList);
			contentPanel.add(scrollPane, "cell 1 1,grow");
		}
		
		{
			contractPanel = new ContractPanel(null);
			contentPanel.add(contractPanel, "cell 2 1,grow");
		}
		
		{
			EnterOrderPanel enterOrderPanel = new EnterOrderPanel();
			contentPanel.add(enterOrderPanel, "cell 1 3,grow");
		}
		
		
		
		/*
		 * 
		 *  S O U T H
		 * 
		 * 
		 * 
		 */
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
			}
			
			{
				JButton btnSendOrder = new JButton("Send order");
				btnSendOrder.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						
						/*
						 * Problem: trader accepts a Alpha Symbol (String) and not a NewContract! 
						 */
						// trader.enterOrder(selected_contract, 1, 1.0, Action.BUY, TimeInForce.DAY);
					}
				});
				buttonPane.add(btnSendOrder);
			}
			
		}

		Preferences prefs = Preferences.userNodeForPackage(IBTraderGUI.class);

		// First time.....
		int count = prefs.getInt("count", 0);
		if (count == 0) {
			prefs.putInt("count", 1);
			prefs.put("ul-0", "CL");
		}
		
		loadPrefs();
		for (String s : prefsCache) {
			System.out.println("prefsCache had " + s);
			
			// Replace by LoadSymbol()
			// trader.requestSymbols(/* error IB specific! */SecType.STK, s, cih);
			// trader.requestSymbols(/* error IB specific! */SecType.IND, s, cih);
		}
		
	}

	private void addUnderlying(NewContractDetails ncd) {
		if (!underlyings.contains(ncd.contract().symbol())) {
			underlyings.add(ncd.contract().symbol());
			underlyingsModel.addElement(ncd.contract().symbol());
			System.out.println("UNDERLYING\n" + ncd.toString());
		}
	}

	private void addDerivative(NewContractDetails ncd) {
		String info = ncd.longName() + " / " + ncd.contract().tradingClass() + " / " + ncd.contract().symbol() + " / " + ncd.contract().expiry() + " / " + ncd.contract().exchange() + " / " + ncd.contract().currency();
		contractListModel.addElement(info);
		System.out.println("DERIVATIVE\n" + ncd.toString());
		derivatives.put(info, ncd);
	}
	
	private Set<String> prefsCache = new HashSet<String>();
	
	private boolean addPref(String s) {
		if (prefsCache.contains(s))
			return false;
		prefsCache.add(s);
		
		savePrefs(prefsCache);
		return true;
	}
	
	private void savePrefs(Set<String> set) {
		Preferences prefs = Preferences.userNodeForPackage(IBTraderGUI.class);
		prefs.putInt("count", set.size());
		System.out.println("Saving " + set.size() + " items");
		int count=0;
		for (String s : set) {
			String key = String.format("ul-%d",count); 
			prefs.put(key, s);
			System.out.println("Saving key " + key + " value " + s);
			count++;
		}
	}

	private Set<String> loadPrefs() {
		Set<String> set = new HashSet<String>();
		Preferences prefs = Preferences.userNodeForPackage(IBTraderGUI.class);
		int count = prefs.getInt("count", 0);
		System.out.println("PREFS has " + count + " items");
		for (int i=0; i<count; i++) {
			String key = String.format("ul-%d",i);
			System.out.println("LOADING ITEM " + key);
			String s = prefs.get(key, "");
			if (!s.isEmpty()) {
				set.add(s);
				System.out.println("ITEM " + s);
			}
			else
				System.out.println("ONE ITEM WAS EMPTY!");
		}
		
		prefsCache = set;
		
		return set;
	}
	
	private void saveWindowPrefs(JDialog dlg, ComponentEvent e) {
		Preferences prefs = Preferences.userNodeForPackage(IBTraderGUI.class);
		prefs.putInt("x", dlg.getX());
		prefs.putInt("y", dlg.getY());
		prefs.putInt("width", dlg.getWidth());
		prefs.putInt("height", dlg.getHeight());
	}

	private void loadWindowPrefs(JDialog dlg) {
		Preferences prefs = Preferences.userNodeForPackage(IBTraderGUI.class);
		int x = prefs.getInt("x", dlg.getX());
		int y = prefs.getInt("y", dlg.getY());
		int w = prefs.getInt("width", dlg.getWidth());
		int h = prefs.getInt("height", dlg.getHeight());
		
		dlg.setBounds(x, y, w, h);
	}
}
