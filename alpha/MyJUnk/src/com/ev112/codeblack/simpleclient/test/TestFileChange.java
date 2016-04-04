package com.ev112.codeblack.simpleclient.test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class TestFileChange {

	public void test() {
		final Path path = FileSystems.getDefault().getPath(System.getProperty("user.home"), "Desktop");
		System.out.println(path);
		try {
			final WatchService watchService = FileSystems.getDefault().newWatchService();
			final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
			
			while (true) {
				final WatchKey wk = watchService.take();
				
				System.out.println("---- start -----");
				for (WatchEvent<?> event : wk.pollEvents()) {
					// we only register "ENTRY_MODIFY" so the context is always a Path.
					final Path changed = (Path) event.context();
					System.out.println(changed);
					if (changed.endsWith("myFile.txt")) {
						System.out.println("My file has changed");
					}
				}
				System.out.println("---- stop -----");
				
				// reset the key
				boolean valid = wk.reset();
				if (!valid) {
					System.out.println("Key has been unregistered");
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TestFileChange().test();
	}

}
