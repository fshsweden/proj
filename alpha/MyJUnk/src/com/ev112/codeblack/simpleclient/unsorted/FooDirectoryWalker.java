package com.ev112.codeblack.simpleclient.unsorted;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

public class FooDirectoryWalker extends DirectoryWalker {
	
	private static String rootFolder = "../../";

    private static IOFileFilter filter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
    		FileFilterUtils.suffixFileFilter("xml"));
    
	public FooDirectoryWalker(FileFilter filter) {
		super(filter, -1);
	}

	protected boolean handleDirectory(File directory, int depth, Collection results) {
		System.out.println("Directory:" + directory.getAbsolutePath());
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleFile(final File file, final int depth, final Collection results) throws IOException {
		System.out.println("File:" + file.getAbsolutePath());
		results.add(file);
	}

	public List<File> getFiles() {
		List<File> files = new ArrayList<File>();

		URL url = getClass().getResource(rootFolder);

		if (url == null) {
			System.out.println("Unable to find root folder of configuration files!");
			return files;
		}

		File directory = new File(url.getFile());

		try {
			walk(directory, files);
		} catch (IOException e) {
			System.out.println("Problem finding configuration files!" + e.getLocalizedMessage());
		}

		return files;
	}

	public static void main(String args[]) {

		// Use the filters to construct the walker
		FooDirectoryWalker walker = new FooDirectoryWalker(HiddenFileFilter.VISIBLE);
		
		walker.getFiles();

		// Build up the filters and create the walker
		// Create a filter for Non-hidden directories
		IOFileFilter fooDirFilter = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), HiddenFileFilter.VISIBLE);

		// Create a filter for Files ending in ".txt"
		IOFileFilter fooFileFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(".txt"));

		// Combine the directory and file filters using an OR condition
		java.io.FileFilter fooFilter = FileFilterUtils.or(fooDirFilter, fooFileFilter);

		// Use the filter to construct a DirectoryWalker implementation
		FooDirectoryWalker walker2 = new FooDirectoryWalker(fooFilter);
		walker2.getFiles();
	}

}
