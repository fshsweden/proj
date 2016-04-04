package com.ev112.codeblack.common.exchange;

import java.util.TreeMap;

public class KeyValues extends TreeMap<String,String> {
	public Integer getInt(final String key) {
		return Integer.parseInt(get(key));
	}
}
