package rs.rs_hw1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

public class App {
	private static void addDocuments(IndexWriter w, File[] fileNames) throws IOException {
		for (File file : fileNames) {
			{
				try {
					String fileContent = readContent(file);
					System.out.println("filecontent size: " + fileContent.length());
					String fileName = file.getName();
					System.out.println("filecontent name: " + fileName);
					System.out.println("=======================");
					addDoc(w, fileName, fileContent);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static String readTXTFile(final String csvFileName) throws IOException {

		String line = null;
		StringBuilder csvData = new StringBuilder();
		try (BufferedReader stream = new BufferedReader(new FileReader(csvFileName))) {
			while ((line = stream.readLine()) != null)
				csvData.append(line);
		}
		return csvData.toString();
	}

	public static String readContent(File file) throws IOException {
		System.out.println("read file " + file.getCanonicalPath());
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();

		return new String(data, "UTF-8");
	}

	public static File[] listAllFiles(File folder) {
		System.out.println("In listAllfiles(File) method");
		File[] fileNames = folder.listFiles();
		return fileNames;
	}

	@SuppressWarnings("nls")
	public static void main(String[] args) throws IOException {
		/*StandardAnalyzer analyzer = new StandardAnalyzer();
		Directory index = new RAMDirectory();

		IndexWriterConfig config = new IndexWriterConfig(analyzer);

		IndexWriter w = new IndexWriter(index, config);*/
		/*
		 * File[] fileNames = listAllFiles(new
		 * File("..\\resources\\20_newsgroups\\alt.atheism"));
		 * 
		 * System.out.println("filenames size: " + fileNames.length);
		 * addDocuments(w, fileNames);
		 */
		File f = new File("resources\\20_newsgroups\\alt.atheism\\49960.txt");
		System.out.println(f.getAbsolutePath());
		System.out.println(f.isDirectory());
		//w.close();
		String fileName = "resources\\49960.txt";
		List<String> allLinesFromDataset = new ArrayList<>();

		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			allLinesFromDataset = stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(allLinesFromDataset.size());
	}

	private static void addDoc(final IndexWriter w, final String title, final String content) throws IOException {
		Document doc = new Document();
		System.out.println("addDoc called");
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new StringField("content", content, Field.Store.YES));
	}
}
