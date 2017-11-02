package cn2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
	
	/**
	 * Utility method to copy the data set into a new data structure.
	 * 
	 * @param source
	 *            the list to be copied
	 * @return the newly created list containing the data
	 */
	public static List<List<String>> deepCopyOfData(final List<List<String>> source) {
		List<List<String>> resultSet = new ArrayList<>();
		for (int i = 0; i < source.size(); i++) {
			List<String> currentEl = source.get(i);
			List<String> coppiedEl = new ArrayList<>();
			for (int j = 0; j < currentEl.size(); j++) {
				coppiedEl.add(new String(currentEl.get(j)));
			}
			resultSet.add(coppiedEl);
		}
		return resultSet;
	}
	
	/**
	 * This method is calculating log of base 2 of n
	 * 
	 * @param n
	 *            which is the
	 * 
	 * @return the value of Log2n
	 */
	public static double log2(final double n) {
		return Math.log(n) / Math.log(2);
	}
	

	public static List<List<String>> readTXTFile2(final String csvFileName) throws IOException {

		String line = null;
		List<List<String>> csvData = new ArrayList<>();
		try (BufferedReader stream = new BufferedReader(new FileReader(csvFileName))) {
			while ((line = stream.readLine()) != null)
				csvData.add(Arrays.asList(line.split(",")));
		}
		return csvData;
	}

	
	/**
	 * coverage(rule Ci) = #instances satisfying the antecedent / #instances of
	 * the specific class Ci or we can use in the denominator #total instances
	 * of the training set
	 */
	public static double calculateRuleCoverage(final Rule rule, final List<List<String>> data) {
		long instancesSatisfyingTheRule = data.stream().filter(example -> Main.isRuleApplicable(rule, example)).count();
		long instancesSatisfyingTheAntecedent = data.stream()
				.filter(example -> rule.getAttributes().doesComplexCoverExample(example)).count();
		int classNameIndex = data.get(0).size() - 1;
		long instancesOfTheClass = data.stream()
				.filter(example -> example.get(classNameIndex).equals(rule.getClassName())).count();
		long allInstances = data.size();
		return (double) instancesSatisfyingTheAntecedent / (double) allInstances;
	}

	/**
	 * precision = #instances satisfying antecedent and consequence(in other
	 * words correctly classified instances) divided by #instances satisfying
	 * the antecedent
	 * 
	 */
	public static double calculateRulePrecision(final Rule rule, final List<List<String>> dataset,
			final List<List<String>> dataAfterCN2Classification) {
		long numerator = 0;
		long denominator = 0;
		int classNameIndex = dataset.get(0).size() - 1;
		for (int i = 0; i < dataset.size(); i++) {
			if (Main.isRuleApplicable(rule, dataset.get(i))) {
				denominator++;
				if (dataset.get(i).get(classNameIndex).equals(dataAfterCN2Classification.get(i).get(classNameIndex))) {
					numerator++;
				}
			}
		}
		return (double) numerator / (double) denominator;

	}
	
	/**
	 * This method is responsible for applying the rules induced from the
	 * training data onto a new data in the given order so that it extract and
	 * assigns class label to the given data
	 * 
	 * @param rules
	 *            the induced rules used to classify the data
	 * @param setToClassify
	 *            the data to be classified
	 * @return the classified data
	 * @throws IOException
	 */
	public static List<List<String>> classifyData(final List<Rule> rules, final List<List<String>> setToClassify)
			throws IOException {
		List<List<String>> resultSet = deepCopyOfData(setToClassify);

		int classLabelIndex = resultSet.get(0).size() - 1;
		for (int i = 0; i < resultSet.size(); i++) {
			for (int j = 0; j < rules.size(); j++)
				if (Main.isRuleApplicable(rules.get(j), resultSet.get(i))) {
					resultSet.get(i).set(classLabelIndex, rules.get(j).getClassName());
					break;
				}
		}
		return resultSet;
	}
}
