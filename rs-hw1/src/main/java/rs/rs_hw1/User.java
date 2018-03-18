package rs.rs_hw1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class User {
	
	private String directory;
	private List<Reader> userPreferences = new ArrayList<>();
	
	public User(final String directory) {
		
		this.directory = directory;
	}
	
	private void getFiles() throws FileNotFoundException {
		File[] fileNames = new File(this.directory).listFiles();
			for (File file : fileNames) {
				@SuppressWarnings("nls")
				Reader target = new BufferedReader(new FileReader(this.directory + "\\" + file.getName()));
				this.userPreferences.add(target);
			}
	}
	
	public Reader[] getUserPreferences() throws FileNotFoundException {
		getFiles();
		return this.userPreferences.toArray(new Reader[this.userPreferences.size()]);
	}
}
