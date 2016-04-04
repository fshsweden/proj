package com.ev112.codeblack.simpleclient.test;

import com.ev112.codeblack.common.utilities.Pair;
import com.ev112.codeblack.simpleclient.unsorted.SortedStringArray;

public class TestOrderedList {

	// class Person left out for brevity

	public TestOrderedList() {

		SortedStringArray db = new SortedStringArray();
		Pair<Boolean, Integer> result;
		
		db.add("Abba");
		db.add("Östen");
		db.add("Adam");
		db.add("Aaaaaaaaarg");
		db.add("Danmark");
		db.add("Åreskutan");
		db.add("Aarau");
		db.add("Adam"); // !
		db.add("Alvin");
		db.add("Benny");
		db.add("Bertil");
		db.add("Calvin");
		db.add("Ceasar");
		db.add("Charlie");
		db.add("Danmark");
		db.add("Danne");
		db.add("Åke");
		db.add("Ärlig");
		db.add("Öken");

		System.out.println("Adam is on index:" + db.findIndex("Adam"));
		
		System.out.println("\n\nKEYS:\n");
		for (String s : db.getKeyArray()) {
			System.out.println(s + " at pos " + db.findIndex(s)) ;
		}
		
	}
	
	public static void main(String[] args) {
		new TestOrderedList();
	}
}
