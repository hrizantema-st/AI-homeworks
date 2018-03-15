package rs.hw1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class App {
	

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
		//create
		StandardAnalyzer analyzer = new StandardAnalyzer();
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = null;
		try {
			w = new IndexWriter(index, config);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File[] fileNames = listAllFiles(new File("src\\main\\resources\\user1"));
		System.out.println("filenames size: " + fileNames.length);
		addDocuments(w, fileNames);
		//close writer to commit changes
		w.close();
		
		//search
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		// comparison
		MoreLikeThis mlt = new MoreLikeThis(reader); // Pass the index reader
		
		mlt.setAnalyzer(analyzer); // "main" java.lang.UnsupportedOperationException: To use MoreLikeThis without term vectors, you must provide an Analyzer
		
		
		mlt.setFieldNames(new String[] {"content"}); // specify the fields for similiarity
	 	Reader target = new BufferedReader(new FileReader("src\\main\\resources\\user2\\51121")); // orig source of doc you want to find similarities to
		Query query = mlt.like("content", target); // Pass the doc id 
		TopDocs similarDocs = searcher.search(query, 1); // Use the searcher
		ScoreDoc[] hits = similarDocs.scoreDocs;
		if (similarDocs.totalHits == 0)
		    // Do handling
			System.out.println();
		System.out.println("scores: " + similarDocs.getMaxScore());
		//display
		System.out.println("Found " + hits.length + " hits.");
		for(int i=0;i<hits.length;++i) {
		    int docId = hits[i].doc;
		    Document d = searcher.doc(docId);
		    System.out.println((i + 1) + ". " + d.get("content") + "\t" + d.get("title"));
		}
		

	}

	private static void addDoc(final IndexWriter w, final String title, final String content) throws IOException {
		Document doc = new Document();
		System.out.println("addDoc called");
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new StringField("content", content, Field.Store.YES));
	}
	
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
}