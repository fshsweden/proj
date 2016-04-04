package com.ev112.codeblack.simpleclient.ibexamples;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.ev112.codeblack.simpleclient.ibexamples.MarketAPI.InformationType;
import com.ev112.codeblack.simpleclient.ibexamples.MarketAPI.KeyValues;

import mytrade.ib.IBMarketAPI;
import net.miginfocom.swing.MigLayout;

public class IBLoginGUI extends JDialog {

	private final JPanel	contentPanel	= new JPanel();
	private JTextField		edClientId;
	private JTextField		edPort;
	private JTextField		edHost;

	private MarketAPI	marketAPI;
	private Trader	trader;
	private JTextField	edAccount;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			IBLoginGUI dialog = new IBLoginGUI();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public IBLoginGUI() {

		Preferences prefs = Preferences.userNodeForPackage(IBLoginGUI.class);

        String hostname = prefs.get("hostname", "192.168.0.22");
        String port = prefs.get("port", "6661");
        String clientId = prefs.get("clientid", "0");
        String account = prefs.get("account", "");
		
		
		setBounds(100, 100, 450, 214);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][][grow]", "[grow][][][]"));

		{
			JLabel lblNewLabel = new JLabel("Host");
			contentPanel.add(lblNewLabel, "cell 0 0");
		}

		{
			JLabel lblNewLabel_1 = new JLabel("Port");
			contentPanel.add(lblNewLabel_1, "cell 0 1");
		}
		{
			JLabel lblNewLabel_2 = new JLabel("ClientId");
			contentPanel.add(lblNewLabel_2, "cell 0 2");
		}

		{
			edHost = new JTextField();
			edHost.setText(hostname);
			contentPanel.add(edHost, "cell 2 0,growx");
			edHost.setColumns(10);
		}

		{
			edPort = new JTextField();
			edPort.setText(port);
			contentPanel.add(edPort, "cell 2 1,growx");
			edPort.setColumns(10);
		}

		{
			edClientId = new JTextField();
			edClientId.setText("0");
			contentPanel.add(edClientId, "cell 2 2,growx");
			edClientId.setColumns(10);
		}
		{
			JLabel lblNewLabel_3 = new JLabel("Account");
			contentPanel.add(lblNewLabel_3, "cell 0 3");
		}
		{
			edAccount = new JTextField();
			contentPanel.add(edAccount, "cell 2 3,growx");
			edAccount.setColumns(10);
		}

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						marketAPI = new IBMarketAPI();

						MarketAPI.KeyValues kv = new MarketAPI.KeyValues();
						
						kv.put("HOST", "192.168.0.22");
						kv.put("PORT", "6661");
						kv.put("CLIENT_ID", "1");
						kv.put("ACCOUNT", "");
						
						marketAPI.configure(kv);
						
						marketAPI.connect(new MarketAPI.ConnectionEventHandler() {
							
							@Override
							public void information(InformationType it, KeyValues info) {
								// TODO implement this
								
							}
							
							@Override
							public void disconnected(KeyValues info) {
								// TODO implement this
								
							}
							
							@Override
							public void connected(KeyValues info) {

								// run on GUI thread!
								
								setVisible(false);
								IBTraderGUI traderGUI = new IBTraderGUI(marketAPI);
								traderGUI.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
								traderGUI.setVisible(true);

								traderGUI.addWindowListener(new WindowAdapter() {
									@Override
									public void windowClosed(WindowEvent e) {
										System.out.println("jdialog window closed event received");
										System.exit(1);
									}

									@Override
									public void windowClosing(WindowEvent e) {
										System.out.println("jdialog window closing event received");
									}
								});
								
								Preferences prefs = Preferences.userNodeForPackage(IBLoginGUI.class);
								
						        prefs.put("hostname", edHost.getText());
						        prefs.put("port", edPort.getText());
						        prefs.put("clientid", edClientId.getText());
						        prefs.put("account", edAccount.getText());
							}
						});
							
						
							
						
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(1);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
