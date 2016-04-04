package com.ev112.codeblack.simpleclient.test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ev112.codeblack.common.database.AlphaDbSingleton;
import com.ev112.codeblack.common.database.IAlphaDatabase;
import com.ev112.codeblack.common.database.StrategyDefItem.STRATEGY_DIRECTION;
import com.ev112.codeblack.common.database.StrategyJobItem;
import com.ev112.codeblack.common.database.StrategyJobItem.JOB_STATUS;

public class TestMediumText {

	
	IAlphaDatabase adb = AlphaDbSingleton.getDbInstance("192.168.0.198", "3306", "alpha", "alpha", "alpha");
	
	public TestMediumText() {
	
		List<StrategyJobItem> list = adb.queryStrategyJobItems(JOB_STATUS.Inserted);
		for (StrategyJobItem strategyJobItem : list) {
			System.out.println("ITEM=>" + strategyJobItem.getStatus() + " JOB ID=>" + strategyJobItem.getId());
			
			int id = adb.saveStrategyJobItem(strategyJobItem);
			System.out.println("    after save:" + id);
		}
		
		System.out.println("Inserting new record...");
		
		DateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:MM:ss.SSS");
		try {
			
			Date cr		= df.parse("2015-01-20 16:01:34.703");
			Date start	= df.parse("2015-01-20 16:01:54.603");  
			Date fini	= df.parse("2015-01-20 16:01:64.103");
			
			StrategyJobItem item = new StrategyJobItem(
					0,
					"stratid",
					"<XML>LARGE DOCUMENT HERE </XML>", 
					"<XML>LARGE RESULTING DOCUMENT HERE </XML>", 
					"PETER", 
					"PROD01", 
					cr, 
					start, 
					fini, 
					JOB_STATUS.Inserted, 
					"",
					"",
					"",
					"",
					"",
					"",
					"",
					"",
					0,
					STRATEGY_DIRECTION.Unknown,
					new ArrayList<String>());
			
			int job_id = adb.saveStrategyJobItem(item);
			System.out.println("Saved job id was " + job_id);
			
			adb.closeDb();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

		
	}
	
	public static void main(String[] args) {
		new TestMediumText();
	}

}
