package com.ev112.codeblack.simpleclient.test;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

public class TestDirTools {

	public static void main(String[] args) {
		
//		ArrayList<File> files = new ArrayList<File>();
//		
//		File[] fs = DirTools.listf("~/", files);
//		
//		for (File f : fs) {
//			System.out.println("File name is:<" + f.getAbsolutePath() + ">");
//		}

//		public static Collection<File> listFiles(
//				File directory,
//                String[] extensions,
//                boolean recursive);
		
		String dir = System.getProperty("user.dir");
		String [] ext = {"zdat", "play", "csv"};
		
		System.out.println("Current dir:" + dir);
		
		Collection<File> fc = FileUtils.listFiles(new File(dir), ext, true);
		
		for (Iterator iterator = fc.iterator(); iterator.hasNext();) {
			File file = (File) iterator.next();
			
			System.out.println(file.getAbsolutePath());
			
		}
	}

}
