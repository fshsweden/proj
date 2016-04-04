package com.ev112.codeblack.simpleclient.test;

import java.util.LinkedHashMap;
import java.util.Map;

public class TestGuava {
	LinkedHashMap<Integer,String> queue;
	
	public TestGuava() {

		queue = new LinkedHashMap<Integer, String>() {
			private static final long serialVersionUID = -5882425272275177479L;
			@Override
			protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
				return this.size() > 2;
			}
		};
		
		// LinkedHashSet<E>
		
		queue.put(2, "2");
		pr();
		queue.put(13, "13");
		pr();
		queue.put(4, "4");
		pr();
		queue.put(112, "112");
		pr();
		queue.put(7, "7");
		pr();
		queue.put(5, "5");
		pr();
		queue.put(22, "22");
		pr();
		queue.put(33, "33");
		pr();
		queue.put(87, "87");
		pr();
		queue.put(67, "67");
		pr();
		queue.put(15, "15");
		pr();
		
	}

	private void pr() {
		System.out.println("-----------------------------");
		for (Integer i : queue.keySet()) {
			System.out.println(queue.get(i));
		}
	}
	public static void main(String[] args) {
		new TestGuava();
	}

}
