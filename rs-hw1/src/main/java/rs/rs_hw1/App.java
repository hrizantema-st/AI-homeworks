package rs.rs_hw1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

@SuppressWarnings("nls")
public class App {

	/**
	 * This method is responsible for reading a file's content
	 * 
	 * @param file
	 *            - the file which we want to read
	 * @return String representing the content
	 * @throws IOException
	 */
	public static String readContent(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();
		return new String(data, "UTF-8");
	}

	/**
	 * This method is adding a document in a Lucene index
	 * 
	 * @param w
	 * @param title
	 *            - the title of the document which we want to add
	 * @param content
	 *            - the content of the document which we want to add
	 * @throws IOException
	 */
	private static void addDoc(final IndexWriter w, final String title, final String content) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new TextField("content", content, Field.Store.NO));
		w.addDocument(doc);
	}

	/**
	 * Given a writer and a directory this method is adding all the files from
	 * the directory into the index
	 * 
	 * @param w
	 * @param directory
	 * @throws IOException
	 */
	private static void addDocuments(IndexWriter w, File directory) throws IOException {
		File[] fileNames = directory.listFiles();
		for (File file : fileNames) {
			{
				try {
					String fileContent = readContent(file);
					String fileName = file.getName();
					addDoc(w, fileName, fileContent);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		// create
		StandardAnalyzer analyzer = new StandardAnalyzer();
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = null;
		try {
			w = new IndexWriter(index, config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File corpus = new File("src\\main\\resources\\20_newsgroups");
		addDocuments(w, corpus);
		// close writer to commit changes
		w.close();

		// search
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);

		// comparison
		MoreLikeThis mlt = new MoreLikeThis(reader);
		mlt.setAnalyzer(analyzer);
		mlt.setFieldNames(new String[] { "content" }); // specify the fields for similarity

		User user = new User("src\\main\\resources\\user_profile3");
		Reader[] userPreferences = user.getUserPreferences();
		Query query = mlt.like("content", userPreferences);
		TopDocs similarDocs = searcher.search(query, 10);

		ScoreDoc[] hits = similarDocs.scoreDocs;
		System.out.println("You might also like these articles.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " + d.get("title"));
		}
	}

}
