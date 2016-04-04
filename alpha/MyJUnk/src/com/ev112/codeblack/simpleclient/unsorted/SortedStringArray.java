package com.ev112.codeblack.simpleclient.unsorted;

import java.io.Serializable;
import java.util.ArrayList;

import com.ev112.codeblack.common.utilities.Pair;

public class SortedStringArray implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5451081448029411813L;
	
	private ArrayList<String>	key_array	= new ArrayList<String>();
	
	public SortedStringArray() {
	}
	
	public ArrayList<String> getKeyArray() {
		return key_array;
	}

	public int add(String key) {
		Pair<Boolean, Integer> result = findLocation(key);
		if (!result.getFirst()) {
			insert(result.getSecond(),key);
			return result.getSecond();
		}
		else {
			// System.out.println("Record " + key + " exists, can't add!");
			return -1;
		}
	}
	
	public void insert(Integer pos, String key) {
		// Pair<Boolean, Integer> result = findLocation(key);
		// assert result.getSecond() == pos;
		key_array.add(pos, key);
	}
	
	public Integer findIndex(String key) {
		Pair<Boolean, Integer> res = findLocation(key);
		if (!res.getFirst())
			return -1;
		else
			return res.getSecond();
	}
	
	Pair<Boolean, Integer> findLocation(String key) {

//		System.out.println("Looking for " + key);
		
		Pair<Boolean,Integer> result = new Pair<Boolean, Integer>(false, 0);
		int first = 0;
		int last = key_array.size() - 1;
		int middle = (first + last) / 2;

		while (first <= last) {
			// System.out.println("BEFORE test first = " + first + " middle = " + middle + " last = " + last);
			// System.out.println("Comparing " + key_array.get(middle) + " with " + key);
			if (key_array.get(middle).compareTo(key) < 0)
				first = middle + 1;
			else
				if (key_array.get(middle).equals(key)) {
					// System.out.println(key + " found at location " + middle + ".");
					break;
				} 
				else
					last = middle - 1;

			middle = (first + last) / 2;
		}

//		try {
//			System.out.println("Arrived at first/middle/last = " + key_array.get(first) + " " + key_array.get(middle) + " " + key_array.get(last));
//		}
//		catch (Exception ex) {
//			System.out.println("Exception:" + ex.getLocalizedMessage());
//		}
		
		if (first > last) {
			Integer position = first;
//			System.out.println(key + " is not present in the list. Would be inserted in pos: " + position);
			result.setFirst(false);
			result.setSecond(position);
		}
		else {
			Integer position = middle;
//			System.out.println(key + " is present in the list. Found at pos: " + position);
			result.setFirst(true);
			result.setSecond(position);
		}
		return result;
	}
}

