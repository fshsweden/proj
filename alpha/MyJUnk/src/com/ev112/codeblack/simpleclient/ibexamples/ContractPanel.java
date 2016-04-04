package com.ev112.codeblack.simpleclient.ibexamples;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.ib.controller.NewContractDetails;

import net.miginfocom.swing.MigLayout;

public class ContractPanel extends JPanel {

	NewContractDetails ncd;
	
	public ContractPanel(NewContractDetails ncd) {

		this.ncd = ncd;
		
		// Content Panel
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new MigLayout("", "[][][][grow]", "[][]"));
		
		addEntries();
	    
	}

	public void addEntries() {
		addEntry("Contract ID");
		addEntry("Symbol");
		addEntry("Local Symbol");
		addEntry("Sec ID");
		addEntry("Expiry");
		addEntry("Currency");
		addEntry("Multiplier");
		addEntry("Exchange");
		addEntry("Prim Exchange");
		addEntry("Trading class");
//	    private SecType m_secType = SecType.None;
//	    private String m_tradingClass;
//	    private SecIdType m_secIdType = SecIdType.None;
	}
	
	public void updateDialog(NewContractDetails ncd) {
		if (ncd == null) {
			inputs.get("Contract ID").setText("");
			inputs.get("Symbol").setText("");
			inputs.get("Local Symbol").setText("");
			inputs.get("Sec ID").setText("");
			inputs.get("Expiry").setText("");
			inputs.get("Currency").setText("");
			inputs.get("Multiplier").setText("");
			inputs.get("Exchange").setText("");
			inputs.get("Prim Exchange").setText("");
			inputs.get("Trading class").setText("");
		}
		else {
			inputs.get("Contract ID").setText(String.format("%d",ncd.contract().conid()));
			inputs.get("Symbol").setText(ncd.contract().symbol());
			inputs.get("Local Symbol").setText(ncd.contract().localSymbol());
			inputs.get("Sec ID").setText(ncd.contract().secId());
			inputs.get("Expiry").setText(ncd.contract().expiry());
			inputs.get("Currency").setText(ncd.contract().currency());
			inputs.get("Multiplier").setText(ncd.contract().multiplier());
			inputs.get("Exchange").setText(ncd.contract().exchange());
			inputs.get("Prim Exchange").setText(ncd.contract().primaryExch());
			inputs.get("Trading class").setText(ncd.contract().tradingClass());
		}
	}
	
	
	private int row = 0;
	private Map<String,JTextField> inputs = new HashMap<String,JTextField>();
	
	private void addEntry(final String label, final String str) {
		JLabel lblNewLabel = new JLabel(label);
		String lblFmt = String.format("cell 1 %d", row);
		
		add(lblNewLabel, lblFmt);
		
		JTextField textField = new JTextField(str);
		textField.setEditable(false);
		String textFmt = String.format("cell 3 %d, growx", row);
		
		add(textField, textFmt);
		
		textField.setColumns(10);
		inputs.put(label, textField);
		row++;
	}

	private void addEntry(final String label) {
		JLabel lblNewLabel = new JLabel(label);
		String lblFmt = String.format("cell 1 %d", row);
		
		add(lblNewLabel, lblFmt);
		
		JTextField textField = new JTextField("");
		textField.setEditable(false);
		String textFmt = String.format("cell 3 %d, growx", row);
		
		add(textField, textFmt);
		
		textField.setColumns(10);
		inputs.put(label, textField);
		row++;
	}
	
	private void addRadioButtons(JPanel buttonPane, String[] buttons) {
		
		ButtonGroup buttonGroup = new ButtonGroup();

		for (String s : buttons)
		{
			JRadioButton rd = new JRadioButton(s);
			buttonPane.add(rd);
			
			buttonGroup.add(rd);
		}
		
		
	}
}
