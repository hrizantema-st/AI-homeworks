package cn2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.jws.soap.SOAPBinding;

public class Main {

	@SuppressWarnings("nls")
	public static void main(String[] args) throws IOException {
		final String csvFileName = "resources\\car.txt";
		List<List<String>> data = null;
		try {
			data = readTXTFile(csvFileName);
			System.out.println("Total number of instaces in the dataset: " + data.size());
		} catch (IOException e) {
			System.out.println("No file with the given name exists!!!");
		}
		List<Selector> selectors = calculateSelectors(csvFileName);
		
		//Collections.shuffle(data);
		
		List<List<String>> trainingData = new ArrayList<>(data.subList(0, (int) (data.size() * 0.7)));
		List<Rule> ruleset = CN2(3, trainingData);

		System.out.println("======= RULESET SIZE: ======= " + ruleset.size());
		
		ruleset.stream().forEach(System.out::println);

		List<List<String>> testData = new ArrayList<>(data.subList((int) (data.size() * 0.7), data.size()));
		
		List<List<String>> results = classifyData(ruleset, testData);
		for (int i = 0; i < results.size(); i++) {
			System.out.println(results.get(i).get(4) + "  -  " + testData.get(i).get(4));
		}
		
		int i = 0;
		for(Rule r: ruleset) {
			System.out.println("Coverage of " + i + ": "  + calculateRuleCoverage(r, data));
			System.out.println("Precision of " + i + ": "  + calculateRulePrecision(r, testData, results));
			System.out.println("---------------------------------------");
			i++;
		}

	}

	/*
	 * This method is calculating log of base 2 of n
	 * 
	 * @param n which is the
	 * 
	 * @return the value of Log2n
	 */
	private static double log2(final double n) {
		return Math.log(n) / Math.log(2);
	}

	/*
	 * This method is responsible for reading a dataset in csv file format
	 */
	@SuppressWarnings("nls")
	public static List<List<String>> readTXTFile(final String csvFileName) throws IOException {

		String line = null;
		List<List<String>> csvData = new ArrayList<>();
		try (BufferedReader stream = new BufferedReader(new FileReader(csvFileName))) {
			while ((line = stream.readLine()) != null)
				csvData.add(Arrays.asList(line.split(",")));
		}
		return csvData;
	}

	public static List<Selector> calculateSelectors(final String csvFileName) throws IOException {
		List<List<String>> dataTable = readTXTFile(csvFileName);
		return calculateSelectors(dataTable);
	}

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

	public static List<List<String>> findAllExamplesThatAComplexCovers(final Complex complex, List<List<String>> data) {
		return data.stream().filter(example -> complex.doesComplexCoverExample(example)).collect(Collectors.toList());
	}

	public static double evaluateComplexQuality(final Complex complex, List<List<String>> data) {
		List<List<String>> allCoveredExamples = findAllExamplesThatAComplexCovers(complex, data);
		if (allCoveredExamples.isEmpty() || complex.getAttributes().isEmpty()) {
			return Double.MAX_VALUE;
		}

		int classIndex = allCoveredExamples.get(0).size() - 1;
		Map<Object, List<String>> res = allCoveredExamples.stream().map(a -> a.get(classIndex))
				.collect(Collectors.groupingBy(Function.identity()));

		// real calculation
		int numberOfExamples = allCoveredExamples.size();
		double entropy = 0;
		for (Map.Entry<Object, List<String>> entry : res.entrySet()) {
			int currentNumOfInstances = entry.getValue().size();
			double currentProbability = (double) currentNumOfInstances / (double) numberOfExamples;
			double nv = currentProbability * log2(currentProbability);
			entropy -= nv;
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
	 * best K complex found(k-beam)
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
		for (int i = 0; i < complex.getAttributes().size(); i++) {
			newComplex.addSelector(new Selector(complex.getAttributes().get(i).getAttribute(),
					complex.getAttributes().get(i).getAttributeIndex(), complex.getAttributes().get(i).getValue()));
		}
		newComplex
				.addSelector(new Selector(selector.getAttribute(), selector.getAttributeIndex(), selector.getValue()));
		return newComplex;
	}

	public static Complex Find_Best_Complex(final List<List<String>> instances, final int k,
			final List<Selector> allSelectors) throws IOException {
		// Let STAR be the set containing the empty complex.
		Comparator<Complex> complexComparator = (Complex c1, Complex c2) -> Double
				.valueOf(evaluateComplexQuality(c1, instances))
				.compareTo(Double.valueOf(evaluateComplexQuality(c2, instances)));
				// List<Selector> allSelectors = calculateSelectors(instances);

		// Let BEST.CPX be nil.
		Complex theEmptyComplex = new Complex(new ArrayList<Selector>());
		Complex bestComplex = theEmptyComplex; // or null?!

		List<Complex> star = new ArrayList<>();
		star.add(theEmptyComplex);

		while (!star.isEmpty()) {
			/*
			 * Specialize all complexes in STAR as follows: Let NEWSTAR be the
			 * set {x & y|x € STAR, y € SELECTORS}
			 */
			List<Complex> newStar = new ArrayList<>();
			for (Complex c : star) {
				for (Selector s : allSelectors) {
					Complex nc = intersection(c, s);
					if (nc != null && !star.contains(nc)) {
						newStar.add(nc);
					}

				}
			}
			/*
			 * For every complex Ci in NEWSTAR: If Ci is statistically
			 * significant and better than BEST.CPX by user-defined criteria
			 * when tested on E, Then replace the current value of BEST.CPX by
			 * Ci.
			 */
			for (Complex c : newStar) {
				if (complexComparator.compare(c, bestComplex) < 0) {
					System.out.println("CURRENT COMPLEX VALUE: " + c + " --- " + evaluateComplexQuality(c, instances));
					bestComplex = c;
				}
			}
			/*
			 * Repeat until size of NEWSTAR < user-defined maximum: Remove the
			 * worst complex from NEWSTAR. Let STAR be NEWSTAR.
			 */
			while (newStar.size() >= k) {
				// System.out.println("STAR SIZE ---- : " + newStar.size());
				Complex minValueComplex = Collections.max(newStar, complexComparator);
				newStar.remove(minValueComplex);
			}
			star = newStar;
		}

		return bestComplex;
	}

	public static List<List<String>> instacesCoveredByComplex(final Complex complex,
			final List<List<String>> instances) {
		return instances.stream().filter(example -> complex.doesComplexCoverExample(example))
				.collect(Collectors.toList());
	}


	public static List<Rule> CN2(final int k, final List<List<String>> instances) throws IOException {
		// Compute SELECTORS
		List<Selector> selectors = calculateSelectors(instances);
		// CN2_LIST ←∅
		List<Rule> cn2List = new ArrayList<>();

		Complex bestComplex = Find_Best_Complex(instances, k, selectors);

		while (bestComplex != null && !instances.isEmpty()) {
			// E' ←instancescoveredbytheBEST_CPX
			
			
			for(Rule r: cn2List) {
				if(r.getAttributes().equals(bestComplex)) {
					bestComplex = Find_Best_Complex(instances, k, selectors);
					break;
				}
			}
			
			
			List<List<String>> coveredInstances = instacesCoveredByComplex(bestComplex, instances);
			// System.out.println("covered instances size in CN2 while: " +
			// coveredInstances.size());
			// C ←the mode class of the set of instances E
			String modeClass = findModeClass(coveredInstances);
			// System.out.println("MODE CLASS IN CN2: " + modeClass);
			// Create the rule R: “if BEST_CPX -> class C”
			Rule newRule = new Rule(bestComplex, modeClass);
			cn2List.add(newRule);
			// E ←E -E’
			instances.removeAll(coveredInstances);
			bestComplex = Find_Best_Complex(instances, k, selectors);
			System.out.println("CN======== current best complex: " + bestComplex);
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
	
	public static List<Rule> CN2(final int k, final String dataset) throws IOException {
		List<List<String>> instances = readTXTFile(dataset);
		return CN2(k, instances);
	}


	public static boolean isRuleApplicable(final Rule rule, final List<String> example) {
		return rule.getAttributes().doesComplexCoverExample(example);
	}

	public static String applyRule(final Rule rule) {
		return rule.getClassName();
	}

	/*
	 * coverage(rule Ci) = #instances satisfying the antecedent / #instances of
	 * the specific class Ci or we can use in the denominator #total instances
	 * of the training set
	 */
	public static double calculateRuleCoverage(final Rule rule, final List<List<String>> data) {
		long instancesSatisfyingTheRule = data.stream().filter(example -> isRuleApplicable(rule, example)).count();
		System.out.println("Num instances satisfying the rule: " + instancesSatisfyingTheRule);
		
		long instancesOfTheClass = data.size();
		return (double) instancesSatisfyingTheRule / (double) instancesOfTheClass;
	}

	/*
	 * precision = #instances satisfying antecedent and consequence(in other
	 * words correctly classified instances) divided by #instances satisfying
	 * the antecedent
	 * 
	 */
	public static double calculateRulePrecision(final Rule rule, final List<List<String>> realData,
			final List<List<String>> dataAfterCN2Classification) {
		long numerator = 0;
		long denominator = 0;
		int classNameIndex = realData.get(0).size() - 1;
		for (int i = 0; i < realData.size(); i++) {
			if (isRuleApplicable(rule, realData.get(i))) {
				denominator ++;
				if(realData.get(i).get(classNameIndex).equals(dataAfterCN2Classification.get(i).get(classNameIndex))) {
					numerator++;
				}
			}
		}
		return (double) numerator / (double) denominator;

	}

	public static List<List<String>> classifyData(final List<Rule> rules, final List<List<String>> setToClassify)
			throws IOException {
		List<List<String>> resultSet = deepCopyOfData(setToClassify);
		
		int classLabelIndex = resultSet.get(0).size() - 1;
		for (int i = 0; i < resultSet.size(); i++) {
			for (int j = 0; j < rules.size(); j++)
				if (isRuleApplicable(rules.get(j), resultSet.get(i))) {
					resultSet.get(i).set(classLabelIndex, applyRule(rules.get(j)));
					continue;
				}
		}
		return resultSet;

	}
	
	public static List<List<String>> deepCopyOfData(final List<List<String>> source) {
		List<List<String>> resultSet = new ArrayList<>();
		for (int i = 0; i < source.size(); i++) {
			List<String> currentEl = source.get(i);
			List<String> coppiedEl = new ArrayList<>();
			for(int j = 0; j < currentEl.size(); j ++) {
				coppiedEl.add(new String(currentEl.get(j)));
			}
			resultSet.add(coppiedEl);
		}
		return resultSet;
	}

}
