package com.ev112.codeblack.simpleclient.test;

import java.io.IOException;

import com.ev112.codeblack.simpleclient.unsorted.AlphaPropertyValues;

// See: http://crunchify.com/java-properties-file-how-to-read-config-properties-values-in-java/

public class TestReadProperty {
	public static void main(String[] args) throws IOException {
		AlphaPropertyValues alphaproperties = new AlphaPropertyValues();		
		System.out.println("Build number is:" + alphaproperties.getProperty("build.number"));
	}
}
