package cn2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Test {
	public static void main(String[] args) throws IOException {
		final String csvFileName = "resources\\eye.txt";
		List<List<String>> data = null;
		try {
			data = Main.readTXTFile(csvFileName);
			System.out.println("Total number of instaces in the dataset: " + data.size());
		} catch (IOException e) {
			System.out.println("No file with the given name exists!!!");
		}
		List<Selector> allSelectors = Main.calculateSelectors(csvFileName);
		System.out.println("ALL SELECTORS SIZE: " + allSelectors.size());

		Complex theEmptyComplex = new Complex(new ArrayList<Selector>());

		List<Complex> star = new ArrayList<>();
		star.add(theEmptyComplex);

		List<Complex> newStar = new ArrayList<>();
		for (Complex c : star) {
			for (Selector s : allSelectors) {
				Complex nc = Main.intersection(c, s);
				if (nc != null && !star.contains(nc)) {
					newStar.add(nc);
				}
			}
		}

		Selector s1 = new Selector("Attr0", 0, "jove");
		Selector s2 = new Selector("Attr1", 1, "miope");
		Selector s3 = new Selector("Attr3", 3, "redu?da");
		Complex tmp = new Complex(Arrays.asList(s1));
		Complex tmp2 = new Complex(Arrays.asList(s1, s2));
		Complex tmp3 = new Complex(Arrays.asList(s1, s2, s3));
		double quality = Main.evaluateComplexQuality(tmp, data);
		System.out.println("Quality1: " + quality);

		/*double quality2 = Main.evaluateComplexQuality(tmp2, data);
		System.out.println("Quality2: " + quality2);

		double quality3 = Main.evaluateComplexQuality(tmp3, data);
		System.out.println("Quality3: " + quality3);*/

		final List<List<String>> data2 = data;
		Comparator<Complex> complexComparator = (Complex c1, Complex c2) -> Double
				.valueOf(Main.evaluateComplexQuality(c1, data2))
				.compareTo(Double.valueOf(Main.evaluateComplexQuality(c2, data2)));

		System.out.println(Main.evaluateComplexQuality(theEmptyComplex, data));
		System.out.println(complexComparator.compare(theEmptyComplex, tmp2));
		System.out.println(complexComparator.compare(tmp2, theEmptyComplex));
		
		/*
		 * for (int i = 0; i < selectors.size(); i++) {
		 * System.out.println(selectors.get(i)); }
		 * 
		 * List<String> allClasses = allClasses(csvFileName);
		 * allClasses.forEach(item -> System.out.println(item));
		 */

		//

		/*Selector s1 = new Selector("Attr0", 0, "jove");
		Selector s2 = new Selector("Attr1", 1, "miope");
		Selector s3 = new Selector("Attr3", 3, "redu?da");
		Complex tmp = new Complex(Arrays.asList(s1, s2, s3));
		List<List<String>> res = findAllExamplesThatAComplexCovers(tmp, data);
		System.out.println(tmp);
		System.out.println("size covered inst: " + res.size());
		System.out.println("entropy: " + evaluateComplexQuality(tmp, data));

		System.out.println("Mode of the dataset: " + findModeClass(data));

		Complex c = Find_Best_Complex(res, 10, selectors);
		System.out.println("NOTOVERWTF");
		System.out.println(c);
		System.out.println("-----------");*/

	}

}
