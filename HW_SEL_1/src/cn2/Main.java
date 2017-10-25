package cn2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

	@SuppressWarnings("nls")
	public static void main(String[] args) throws IOException {
		final String csvFileName = "resources\\eye.txt";
		List<List<String>> data = null;
		try {
			data = readTXTFile(csvFileName);
			System.out.println("Total number of instaces in the dataset: " + data.size());
		} catch (IOException e) {
			System.out.println("No file with the given name exists!!!");
		}
		List<Selector> selectors = calculateSelectors(csvFileName);
		for (int i = 0; i < selectors.size(); i++) {
			System.out.println(selectors.get(i));
		}

		List<String> allClasses = allClasses(csvFileName);
		allClasses.forEach(item -> System.out.println(item));

		//
		Selector s1 = new Selector("Attr0", 0, "jove");
		Selector s2 = new Selector("Attr1", 1, "miope");
		Selector s3 = new Selector("Attr3", 3, "redu?da");
		Complex tmp = new Complex(Arrays.asList(s1, s2, s3));
		List<List<String>> res = findAllExamplesThatAComplexCovers(tmp, data);

		System.out.println("size covered inst: " + res.size());
		System.out.println("entropy: " + evaluateComplexQuality(tmp, data));

		System.out.println("Mode of the dataset: " + findModeClass(data));

	}

	private static double log2(final double n) {
		return Math.log(n) / Math.log(2);
	}

	/*
	 * This method is responsible for reading a dataset in csv file format
	 */
	@SuppressWarnings("nls")
	private static List<List<String>> readTXTFile(final String csvFileName) throws IOException {

		String line = null;
		BufferedReader stream = null;
		List<List<String>> csvData = new ArrayList<>();

		try {
			stream = new BufferedReader(new FileReader(csvFileName));
			while ((line = stream.readLine()) != null)
				csvData.add(Arrays.asList(line.split(",")));
		} finally {
			if (stream != null)
				stream.close();
		}

		return csvData;

	}

	public static List<Selector> calculateSelectors(final String csvFileName) throws IOException {
		List<List<String>> dataTable = readTXTFile(csvFileName);
		List<Selector> selectors = new ArrayList<>();
		for (int i = 0; i < dataTable.get(0).size() - 1; i++) {
			final int index = i;
			List<String> tmpRes = dataTable.stream().map(row -> row.get(index)).distinct().collect(Collectors.toList());
			for (int j = 0; j < tmpRes.size(); j++) {
				Selector newSelector = new Selector("Attr" + i, i, tmpRes.get(j));
				selectors.add(newSelector);
			}
		}
		return selectors;
	}

	public static List<List<String>> findAllExamplesThatAComplexCovers(final Complex complex, List<List<String>> data) {
		return data.stream().filter(example -> complex.doesComplexCoverExample(example)).collect(Collectors.toList());
	}

	public static double evaluateComplexQuality(final Complex complex, List<List<String>> data) {
		List<List<String>> allCoveredExamples = findAllExamplesThatAComplexCovers(complex, data);
		int numAttributes = allCoveredExamples.get(0).size() - 1;
		Map<Object, List<String>> res = allCoveredExamples.stream().map(a -> a.get(numAttributes))
				.collect(Collectors.groupingBy(Function.identity()));

		// real calculation
		int numberOfExamples = allCoveredExamples.size();
		double entropy = 0;
		for (Map.Entry<Object, List<String>> entry : res.entrySet()) {
			int currentNumOfInstances = entry.getValue().size();
			double currentProbability = currentNumOfInstances / numberOfExamples;
			entropy -= currentProbability * log2(currentProbability);
		}
		return entropy;
	}

	public static List<String> allClasses(final String csvFileName) throws IOException {
		List<List<String>> dataTable = readTXTFile(csvFileName);
		return dataTable.stream().map(row -> row.get(dataTable.get(0).size() - 1)).distinct()
				.collect(Collectors.toList());
	}

	public static String findModeClass(final List<List<String>> instances) {
		int indexOfClassLabel = instances.get(0).size() - 1;
		final Map<String, Long> countFrequencies = instances.stream().map(a -> a.get(indexOfClassLabel))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		final long maxFrequency = countFrequencies.values().stream().mapToLong(count -> count).max().orElse(-1);
		return countFrequencies.entrySet().stream().filter(tuple -> tuple.getValue() == maxFrequency)
				.map(Map.Entry::getKey).collect(Collectors.toList()).get(0);
	}

	/*
	 * finding the best complex is a heuristic search algorithm, which keeps the
	 * best K complex found(k-beam),
	 */
	public static Complex Find_Best_Complex(final List<List<String>> instances, final int k) {
		PriorityQueue<Complex> pq = new PriorityQueue<>(k);
		return null;
	}

	public static List<List<String>> instacesCoveredByComplex(final Complex complex) {
		return null;
	}

	public static List<Rule> CN2(final int k, final String dataset) throws IOException {
		List<List<String>> instances = readTXTFile(dataset);
		// Compute SELECTORS
		List<Selector> selectors = calculateSelectors(dataset);
		// CN2_LIST ←∅
		List<Rule> cn2List = new ArrayList<>();
		int kBeamParameter = 0;
		Complex bestComplex = Find_Best_Complex(instances, kBeamParameter);

		while (bestComplex != null && !instances.isEmpty()) {
			// E' ←instancescoveredbytheBEST_CPX
			List<List<String>> coveredInstances = instacesCoveredByComplex(bestComplex);
			// C ←the mode class of the set of instances E
			String modeClass = findModeClass(coveredInstances);
			// Create the rule R: “if BEST_CPX -> class C”
			Rule newRule = new Rule(bestComplex, modeClass);
			cn2List.add(newRule);
			// E ←E -E’
			instances.removeAll(coveredInstances);
			bestComplex = Find_Best_Complex(instances, kBeamParameter);
		}
		if (instances.size() != 0) {
			// C ← the mode class of the set of instances E
			String modeClass = findModeClass(instances);
			// CreatetheruleDefRule: “if∅ -> classC”
			Rule defaultRule = new Rule(new Complex(Collections.emptyList()), modeClass);
			cn2List.add(defaultRule);
		}
		return cn2List;
	}

}
