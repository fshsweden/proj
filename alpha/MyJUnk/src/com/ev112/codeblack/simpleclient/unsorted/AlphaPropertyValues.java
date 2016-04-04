package com.ev112.codeblack.simpleclient.unsorted;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
 
//See: http://crunchify.com/java-properties-file-how-to-read-config-properties-values-in-java/

// File is in /resources/buildinfo.properties

/**
 * 
 * @author peterandersson
 *
 */
public class AlphaPropertyValues {
 
	private Properties prop = new Properties();
	private String propFileName = "alpha.properties";
	
	public AlphaPropertyValues() {
		try {
			loadPropValues();
		} catch (IOException e) {
			
		}
	}
	
	public void loadPropValues() throws IOException {
		String result = "";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		if (inputStream == null) {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		prop.load(inputStream);
	}
	
	public String getProperty(String key) {
		return prop.getProperty(key);
	}
	
	public void setProperty(String key, String value) {
		prop.setProperty(key, value);
	}
	
	public String getBuildNumber() {
		return prop.getProperty("build.number");
	}
	
	public void save() throws IOException {
		OutputStream output = null;
		try {
			output = new FileOutputStream("alpha.properties"); // where?
			prop.store(output, null);
	 
		} catch (IOException io) {
			throw io;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
		}		
	}
}

