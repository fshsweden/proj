package com.ev112.codeblack.simpleclient.ibexamples;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

public class EnterOrderPanel extends JPanel {
	
	private JTextField edAccount;
	private JTextField edPrice;
	private JTextField edQty;

	/**
	 * Create the dialog.
	 */
	public EnterOrderPanel() {
		setBounds(100, 100, 450, 187);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new MigLayout("", "[61px][134px,grow]", "[28px][][][][]"));
		
		JLabel lblAccount = new JLabel("Account");
		add(lblAccount, "cell 0 0,alignx left,aligny center");
		
		edAccount = new JTextField();
		add(edAccount, "cell 1 0,alignx left,aligny top");
		edAccount.setColumns(10);
		
		JLabel lblPrice = new JLabel("Price");
		add(lblPrice, "cell 0 1,alignx trailing");
		
		edPrice = new JTextField();
		add(edPrice, "cell 1 1,growx");
		edPrice.setColumns(8);
		
		JLabel lblQty = new JLabel("Qty");
		add(lblQty, "cell 0 2,alignx trailing");
		
		edQty = new JTextField();
		add(edQty, "cell 1 2,growx");
		edQty.setColumns(5);
		
		JRadioButton rbIOC = new JRadioButton("IOC");
		add(rbIOC, "flowx,cell 1 3");
		
		JRadioButton rbEOD = new JRadioButton("EOD");
		add(rbEOD, "cell 1 3,alignx right");
		
		JButton pbBUY = new JButton("BUY");
		add(pbBUY, "flowx,cell 1 4");
		
		JButton pbSELL = new JButton("SELL");
		add(pbSELL, "cell 1 4");
		
		{
//			JPanel buttonPane = new JPanel();
//			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
//			{
//				JButton okButton = new JButton("OK");
//				okButton.setActionCommand("OK");
//				buttonPane.add(okButton);
//				getRootPane().setDefaultButton(okButton);
//			}
//			{
//				JButton cancelButton = new JButton("Cancel");
//				cancelButton.setActionCommand("Cancel");
//				buttonPane.add(cancelButton);
//			}
		}
	}

}
