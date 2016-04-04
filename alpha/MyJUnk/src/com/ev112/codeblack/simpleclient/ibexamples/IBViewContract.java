package com.ev112.codeblack.simpleclient.ibexamples;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.ib.controller.NewContractDetails;

public class IBViewContract extends JDialog {

	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			IBViewContract dialog = new IBViewContract(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public IBViewContract(NewContractDetails ncd) {
		setBounds(100, 100, 450, 541);
		
		JPanel contentPanel = new ContractPanel(ncd);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			
			add(buttonPane, BorderLayout.SOUTH);

			// addRadioButtons(buttonPane, new String[] {"Buy","Sell"} );
			
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
	}

}
