package com.ev112.codeblack.simpleclient.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ev112.codeblack.common.configuration.Configuration;
import com.ev112.codeblack.common.database.AlphaDbSingleton;
import com.ev112.codeblack.common.database.IAlphaDatabase;
import com.ev112.codeblack.common.instmodel.Instrument;
import com.ev112.codeblack.common.instmodel.InstrumentModel;
import com.ev112.codeblack.common.model.parameters.Parameter;

public class TestConfigurationServer {

	private InstrumentModel tInstModel;

	public TestConfigurationServer() {
		Configuration mConfiguration = new Configuration("TEST");
		
		IAlphaDatabase db = AlphaDbSingleton.getDbInstance("alphatrading.dnsalias.com", "3306", "alpha", "alpha", "alpha");
		if (db == null) {
			System.err.println("Failed to open database!");
			System.exit(1);
		}
		System.out.println("DATABASE OPENED!!");

		try {
			tInstModel = mConfiguration.getInstrumentModel();
			
			/*
			 * Add a getSortedInstruments();
			 */
			Collection<Instrument> c = tInstModel.getInstruments();
			List<Instrument> list = new ArrayList<Instrument>(c);
			Collections.sort(list, new Comparator<Instrument>(){
				@Override
				public int compare(Instrument o1, Instrument o2) {
					return o1.compareTo(o2);
				}
			});
			
			for (Instrument i : list) {
			
				db.updateProductsTable(i);
				
				System.out.println(i.getType().toString() + " " + i.getSymbol() + " " +
						i.getBroker() + " " + i.getExchange() + " " + i.getLocalSymbol() + " <" + i.getMarket().toString() + ">" +
						i.getOrderRoute() + " " + i.getPriceRoute() + " " + i.getRootSymbol() + " " + i.getVolumeFactor());
				
				for (Parameter p : i.getParameterList()) {
					System.out.println("    " + p.getName() + " " + p.getType().toString() + " " + p.getValue().toString());
				}
			}
			
		}
		catch (Exception ex) {
			System.out.println("Exception: " + ex.getLocalizedMessage());
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		new TestConfigurationServer();
	}

}
