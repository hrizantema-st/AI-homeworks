package cn2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("nls")
public class Main {

	public static void main(String[] args) throws IOException {
		String dataset = args[0];
		int k = Integer.valueOf(args[1]).intValue();
		System.out.println("The chosen dataset is " + dataset);

		final String csvFileName = /* "resources\\" + */dataset + ".txt";
		List<List<String>> data = null;
		try {
			data = readTXTFile(csvFileName);
			System.out.println("Total number of instaces in the dataset: " + data.size());
		} catch (IOException e) {
			System.out.println("No file with the given name exists!!!");
		}

		// Collections.shuffle(data);

		List<List<String>> trainingData = new ArrayList<>(data.subList(0, (int) (data.size() * 0.7)));

		List<Rule> ruleset = CN2(k, trainingData);

		System.out.println("======= RULESET SIZE: ======= " + ruleset.size());

		// ruleset.stream().forEach(System.out::println);

		List<List<String>> testData = new ArrayList<>(data.subList((int) (data.size() * 0.7), data.size()));

		// List<List<String>> results = classifyData(ruleset, testData);
		List<BigDecimal> coverage = new ArrayList<>();
		List<BigDecimal> precision = new ArrayList<>();
		List<List<String>> results = classifyDataAndCalculatePrecisionAndCoverage(ruleset, testData, coverage,
				precision);

		for (int i = 0; i < ruleset.size(); i++) {
			System.out.println(ruleset.get(i));
			System.out.println("Coverage: " + coverage.get(i));
			System.out.println("Precision: " + precision.get(i));
			System.out.println("---------------------------------------");
		}
		System.out.println("Accuracy: " + calculateTotalPrecision(testData, results));
		System.out.println("sum coverage=  " + coverage.stream().mapToDouble(BigDecimal::doubleValue).sum());

	}

	/**
	 * This method is responsible for reading a data set in csv file format
	 * 
	 * @param csvFileName
	 *            the name of the data set
	 * @return list of list of strings containing the data
	 * @throws IOException
	 */
	public static List<List<String>> readTXTFile(final String csvFileName) throws IOException {

		String line = null;
		List<List<String>> csvData = new ArrayList<>();
		try (BufferedReader stream = new BufferedReader(
				new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(csvFileName)))) {
			while ((line = stream.readLine()) != null)
				csvData.add(Arrays.asList(line.split(",")));
		}
		return csvData;
	}

	/**
	 * This method is calculating all possible selectors for a give data set
	 * 
	 * @param csvFileName
	 *            the name of the data set
	 * @return list of all possible selectors for the current data set
	 * @throws IOException
	 */
	public static List<Selector> calculateSelectors(final String csvFileName) throws IOException {
		List<List<String>> dataTable = readTXTFile(csvFileName);
		return calculateSelectors(dataTable);
	}

	/**
	 * This method is calculating all possible selectors for a give data table
	 * 
	 * @param dataTable
	 * @return list of all possible selectors for the current data set
	 * @throws IOException
	 */
	public static List<Selector> calculateSelectors(final List<List<String>> dataTable) throws IOException {
		List<Selector> selectors = new ArrayList<>();
		for (int i = 0; i < dataTable.get(0).size() - 1; i++) {
			final int index = i;
			List<String> tmpRes = dataTable.stream().map(row -> row.get(index)).distinct().collect(Collectors.toList());
			for (int j = 0; j < tmpRes.size(); j++) {
				Selector newSelector = new Selector("Attr_" + i, i, tmpRes.get(j));
				selectors.add(newSelector);
			}
		}
		return selectors;
	}

	/**
	 * 
	 * @param complex
	 * @param data
	 * @return
	 */
	public static List<List<String>> findAllExamplesThatAComplexCovers(final Complex complex,
			final List<List<String>> data) {
		return data.stream().filter(example -> complex.doesComplexCoverExample(example)).collect(Collectors.toList());
	}

	/**
	 * This method is responsible for evaluating quality of a complex. The
	 * calculations are based on the entropy measure.
	 * 
	 * @param complex
	 * @param data
	 * @return
	 */
	public static double evaluateComplexQuality(final Complex complex, final List<List<String>> data) {
		List<List<String>> allCoveredExamples = findAllExamplesThatAComplexCovers(complex, data);
		if (allCoveredExamples.isEmpty() || complex.getAttributes().isEmpty()) {
			return Double.MAX_VALUE;
		}

		int classIndex = allCoveredExamples.get(0).size() - 1;
		Map<Object, List<String>> mapOfClassFreq = allCoveredExamples.stream().map(example -> example.get(classIndex))
				.collect(Collectors.groupingBy(Function.identity()));

		// real calculation
		int numberOfExamples = allCoveredExamples.size();
		double entropy = 0;
		for (Map.Entry<Object, List<String>> entry : mapOfClassFreq.entrySet()) {
			int currentNumOfInstances = entry.getValue().size();
			double currentProbability = (double) currentNumOfInstances / (double) numberOfExamples;
			double nv = currentProbability * Utils.log2(currentProbability);
			entropy -= nv;
		}
		return entropy;
	}

	/**
	 * Given a data set file name this method returns list of all class names.
	 * 
	 * @param csvFileName
	 * @return
	 * @throws IOException
	 */
	public static List<String> allClasses(final String csvFileName) throws IOException {
		List<List<String>> dataTable = readTXTFile(csvFileName);
		return dataTable.stream().map(row -> row.get(dataTable.get(0).size() - 1)).distinct()
				.collect(Collectors.toList());
	}

	/**
	 * Given a set of data instances this method returns the most common class.
	 * 
	 * @param instances
	 * @return
	 */
	@SuppressWarnings("boxing")
	public static String findModeClass(final List<List<String>> instances) {
		int indexOfClassLabel = instances.get(0).size() - 1;
		final Map<String, Long> countFrequencies = instances.stream().map(instance -> instance.get(indexOfClassLabel))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		final long maxFrequency = countFrequencies.values().stream().mapToLong(count -> count).max().orElse(-1);
		return countFrequencies.entrySet().stream().filter(tuple -> tuple.getValue() == maxFrequency)
				.map(Map.Entry::getKey).collect(Collectors.toList()).get(0);
	}

	/**
	 * This method returns new complex given an existing complex and a selector
	 * to be added to the complex. A null complex is one that contains a pair of
	 * incompatible selectors, e.g., big = y A big = n.)
	 * 
	 * @param complex
	 * @param selector
	 * @return new complex containing the selector ot the same complex
	 */
	public static Complex intersection(final Complex complex, final Selector selector) {
		for (Selector s : complex.getAttributes()) {
			if (s.getAttribute().equals(selector.getAttribute()) && !s.getValue().equals(selector.getValue())) {
				return null;
			}
			if (s.getAttribute().equals(selector.getAttribute()) && s.getValue().equals(selector.getValue())) {
				return complex;
			}
		}
		Complex newComplex = new Complex();
		// copying all the selectors from the old complex
		for (int i = 0; i < complex.getAttributes().size(); i++) {
			newComplex.addSelector(new Selector(new String(complex.getAttributes().get(i).getAttribute()),
					complex.getAttributes().get(i).getAttributeIndex(),
					new String(complex.getAttributes().get(i).getValue())));
		}
		// adding the new selector to the new complex
		newComplex.addSelector(new Selector(new String(selector.getAttribute()), selector.getAttributeIndex(),
				new String(selector.getValue())));
		return newComplex;
	}

	public static Complex findBestComplex(final List<List<String>> instances, final int k,
			final List<Selector> allSelectors) throws IOException {

		Comparator<Complex> complexComparator = (Complex c1, Complex c2) -> Double
				.valueOf(evaluateComplexQuality(c1, instances))
				.compareTo(Double.valueOf(evaluateComplexQuality(c2, instances)));

		// Let STAR be the set containing the empty complex.
		List<Complex> star = new ArrayList<>();

		// Let BEST.CPX be nil.
		Complex theEmptyComplex = new Complex(new ArrayList<Selector>());
		Complex bestComplex = theEmptyComplex; // or null?!
		star.add(theEmptyComplex);

		while (!star.isEmpty()) {

			// Specialize all complexes in STAR as follows: Let NEWSTAR be the
			// set {x & y|x € STAR, y € SELECTORS}. Remove all complexes in
			// NEWSTAR that are either in STAR or null

			List<Complex> newStar = new ArrayList<>();
			for (Complex c : star) {
				for (Selector s : allSelectors) {
					Complex nc = intersection(c, s);
					if (nc != null && !star.contains(nc)) {
						// System.out.println("new c >>>>>>>" + nc);
						newStar.add(nc);
					}
				}
			}

			// For every complex Ci in NEWSTAR: If Ci is statistically
			// significant and better than BEST.CPX when tested on E, Then
			// replace the current value of BEST.CPX by Ci.

			for (Complex c : newStar) {
				if (complexComparator.compare(c, bestComplex) < 0) {
					// System.out.println("CURRENT COMPLEX VALUE: " + c + " ---
					// " + evaluateComplexQuality(c, instances));
					bestComplex = c;
				}
			}

			// Repeat until size of NEWSTAR < user-defined maximum: Remove the
			// worst complex from NEWSTAR.
			while (newStar.size() >= k) {
				Complex worstComplex = Collections.max(newStar, complexComparator);
				newStar.remove(worstComplex);
			}
			// Let STAR be NEWSTAR.
			star = newStar;
		}

		return bestComplex;
	}

	public static Complex findBestComplex(final List<List<String>> instances, final int k) throws IOException {
		List<Selector> allSelectors = calculateSelectors(instances);
		return findBestComplex(instances, k, allSelectors);
	}

	public static List<List<String>> instacesCoveredByComplex(final Complex complex,
			final List<List<String>> instances) {
		return instances.stream().filter(example -> complex.doesComplexCoverExample(example))
				.collect(Collectors.toList());
	}

	/**
	 * The actual CN2 algorithm.
	 * 
	 * @param k
	 *            the parameter of the k-beam search
	 * @param instances
	 *            the data based on which the rules are generated
	 * @return list of induced rules based on the data set
	 * @throws IOException
	 */
	public static List<Rule> CN2(final int k, final List<List<String>> instances) throws IOException {
		// Compute SELECTORS
		List<Selector> selectors = calculateSelectors(instances);

		// CN2_LIST ←∅
		List<Rule> cn2List = new ArrayList<>();

		Complex bestComplex = findBestComplex(instances, k, selectors);

		while (bestComplex != null && !instances.isEmpty()) {
			// check if a rule with the same antecedent is already added in the
			// rule set
			int index = 0;
			while (index <= cn2List.size() - 1 && cn2List.get(index).getAttributes().equals(bestComplex)) {
				bestComplex = findBestComplex(instances, k, selectors);
				index++;
			}

			// E' ←instancescoveredbytheBEST_CPX
			List<List<String>> coveredInstances = instacesCoveredByComplex(bestComplex, instances);
			// C ←the mode class of the set of instances E
			String modeClass = findModeClass(coveredInstances);
			// Create the rule R: “if BEST_CPX -> class C”
			Rule newRule = new Rule(bestComplex, modeClass);
			System.out.println("CN======== current best complex: " + bestComplex);
			cn2List.add(newRule);
			// E ←E -E’
			instances.removeAll(coveredInstances);
			bestComplex = findBestComplex(instances, k, selectors);
		}
		if (instances.size() != 0) {
			// C ← the mode class of the set of instances E
			String modeClass = findModeClass(instances);
			// CreatetheruleDefRule: “if ∅ -> class C”
			Rule defaultRule = new Rule(new Complex(Collections.emptyList()), modeClass);
			cn2List.add(defaultRule);
		}
		return cn2List;
	}

	public static List<Rule> CN2(final int k, final String dataset) throws IOException {
		List<List<String>> instances = readTXTFile(dataset);
		return CN2(k, instances);
	}

	public static boolean isRuleApplicable(final Rule rule, final List<String> example) {
		return rule.getAttributes().doesComplexCoverExample(example);
	}

	@SuppressWarnings("boxing")
	public static List<List<String>> classifyDataAndCalculatePrecisionAndCoverage(final List<Rule> rules,
			final List<List<String>> setToClassify, final List<BigDecimal> coverage, final List<BigDecimal> precision)
					throws IOException {
		List<List<String>> resultSet = Utils.deepCopyOfData(setToClassify);
		List<Integer> instacesSatisfyingTheAntecedent = new ArrayList<>(Collections.nCopies(resultSet.size(), 0));
		List<Integer> instacesCoveredByRule = new ArrayList<>(Collections.nCopies(resultSet.size(), 0));
		int classLabelIndex = resultSet.get(0).size() - 1;
		for (int i = 0; i < resultSet.size(); i++) {
			for (int j = 0; j < rules.size(); j++) {
				if (isRuleApplicable(rules.get(j), resultSet.get(i))) {
					instacesSatisfyingTheAntecedent.set(j, instacesSatisfyingTheAntecedent.get(j).intValue() + 1);
					resultSet.get(i).set(classLabelIndex, rules.get(j).getClassName());
					if (rules.get(j).getClassName().equals(setToClassify.get(i).get(classLabelIndex))) {
						instacesCoveredByRule.set(j, instacesCoveredByRule.get(j).intValue() + 1);
					}
					break;
				}
			}
		}
		for (int j = 0; j < rules.size(); j++) {
			BigDecimal currentPrecision;
			BigDecimal currentCoverage;
			if (instacesCoveredByRule.get(j).intValue() == 0
					|| instacesSatisfyingTheAntecedent.get(j).intValue() == 0) {
				currentPrecision = BigDecimal.ZERO;
				currentCoverage = BigDecimal.ZERO;
			} else {

				currentPrecision = BigDecimal.valueOf(instacesCoveredByRule.get(j).doubleValue()).divide(
						BigDecimal.valueOf(instacesSatisfyingTheAntecedent.get(j).doubleValue()), 5,
						RoundingMode.HALF_UP);

				currentCoverage = BigDecimal.valueOf(instacesSatisfyingTheAntecedent.get(j).doubleValue())
						.divide(BigDecimal.valueOf(resultSet.size()), 5, RoundingMode.HALF_UP);
			}

			precision.add(currentPrecision);
			coverage.add(currentCoverage);
		}
		return resultSet;
	}

	/**
	 * 
	 * @param dataset
	 * @param dataAfterCN2Classification
	 * @return
	 */
	public static BigDecimal calculateTotalPrecision(final List<List<String>> dataset,
			final List<List<String>> dataAfterCN2Classification) {
		long denominator = dataset.size();
		long numerator = 0;
		int classNameIndex = dataset.get(0).size() - 1;
		for (int i = 0; i < dataset.size(); i++) {
			if (dataset.get(i).get(classNameIndex).equals(dataAfterCN2Classification.get(i).get(classNameIndex))) {
				numerator++;
			}
		}
		return BigDecimal.valueOf(numerator).divide(BigDecimal.valueOf(denominator), 5, RoundingMode.HALF_UP);
	}

	@SuppressWarnings("unused")
	private static final String[] carAttrNames = { "buying", "maint", "doors", "persons", "lug_boot", "safety" };
	@SuppressWarnings("unused")
	private static final String[] nurseryArrtNames = { "parents", "has_nurs", "form", "children", "housing", "finance",
			"social", "health" };

}
