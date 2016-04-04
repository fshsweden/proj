package com.ev112.codeblack.simpleclient.test;

import com.ev112.codeblack.common.configuration.Configuration;
import com.ev112.codeblack.common.instmodel.Instrument;
import com.ev112.codeblack.common.instmodel.InstrumentModel;

public class TestInstrumentModel {

	public TestInstrumentModel() {
		
		Configuration config = new Configuration("TEST");
		InstrumentModel tModel = config.getInstrumentModel();
		
		Instrument i = tModel.getInstrument("APPL");
		
		System.out.println(i.toString());
	}
	
	public static void main(String[] args) {
		new TestInstrumentModel();
	}
}
