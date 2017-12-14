package random_forest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("nls")
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

	public static List<Feature> deepCopyOfFeatures(final List<Feature> source) {
		List<Feature> resultSet = new ArrayList<>();
		for (int i = 0; i < source.size(); i++) {
			Feature currentEl = source.get(i);
			List<String> coppiedValues = new ArrayList<>();
			for (int j = 0; j < currentEl.getValues().size(); j++) {
				coppiedValues.add(new String(currentEl.getValues().get(j)));
			}
			Feature currentCoppiedFeature = new Feature(new String(currentEl.getName()), coppiedValues,
					currentEl.getColumnPosition());
			
			resultSet.add(currentCoppiedFeature);
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
	 * This method is calculating all features for a give data set
	 * 
	 * @param csvFileName
	 *            the name of the data set
	 * @return list of all possible features for the current data set
	 * @throws IOException
	 */
	public static List<Feature> calculateAttributes(final String csvFileName) throws IOException {
		List<List<String>> dataTable = readTXTFile(csvFileName);
		return calculateFeatures(dataTable);
	}

	/**
	 * This method is calculating all possible features with their values for
	 * a give data table
	 * 
	 * @param dataTable
	 * @return list of all possible features for the current data set
	 * @throws IOException
	 */
	public static List<Feature> calculateFeatures(final List<List<String>> dataTable) throws IOException {
		List<Feature> attributes = new ArrayList<>();
		for (int i = 0; i < dataTable.get(0).size() - 1; i++) {
			final int index = i;
			List<String> listOfValues = dataTable.stream().map(row -> row.get(index)).distinct()
					.collect(Collectors.toList());
			Feature newAttribute = new Feature("Attr_" + i, listOfValues, i);
			attributes.add(newAttribute);
		}
		return attributes;
	}

	
	public static List<Feature> calculateAttributesPlusClassAsLastAttr(final List<List<String>> dataTable) throws IOException {
		List<Feature> attributes = new ArrayList<>();
		for (int i = 0; i < dataTable.get(0).size(); i++) {
			final int index = i;
			List<String> listOfValues = dataTable.stream().map(row -> row.get(index)).distinct()
					.collect(Collectors.toList());
			Feature newAttribute = new Feature("Attr_" + i, listOfValues, i);
			attributes.add(newAttribute);
		}
		return attributes;
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
	 * 
	 * @param dataset
	 * @param dataAfterClassification
	 * @return
	 */
	public static BigDecimal calculateTotalPrecision(final List<List<String>> dataset,
			final List<List<String>> dataAfterClassification) {
		long denominator = dataset.size();
		long numerator = 0;
		int classNameIndex = dataset.get(0).size() - 1;
		for (int i = 0; i < dataset.size(); i++) {
			if (dataset.get(i).get(classNameIndex).equals(dataAfterClassification.get(i).get(classNameIndex))) {
				numerator++;
			}
		}
		return BigDecimal.valueOf(numerator).divide(BigDecimal.valueOf(denominator), 5, RoundingMode.HALF_UP);
	}
	
	public static BigDecimal calculatePrecision(final List<List<String>> dataset,
			final List<String> classesObtained) {
		long denominator = dataset.size();
		long numerator = 0;
		int classNameIndex = dataset.get(0).size() - 1;
		for (int i = 0; i < dataset.size(); i++) {
			if (dataset.get(i).get(classNameIndex).equals(classesObtained.get(i))) {
				numerator++;
			}
		}
		return BigDecimal.valueOf(numerator).divide(BigDecimal.valueOf(denominator), 5, RoundingMode.HALF_UP);
	}
	
	/**
	 * This method is responsible for producing random subset of features with a
	 * specified size;
	 * 
	 * @param attributes
	 *            - the set of attributes from which to extract the random
	 *            subset
	 * @param numberOfFeatures
	 *            - the size of the subset needed
	 * @return random subset of features with size numberOfFeatures
	 */
	public static List<Feature> randomSubsetOfAttributes(final List<Feature> attributes, final int numberOfFeatures) {
		List<Feature> coppiedAttributes = Utils.deepCopyOfFeatures(attributes);
		if(numberOfFeatures > attributes.size()) {
			return coppiedAttributes;
		}
		Collections.shuffle(coppiedAttributes);
		return coppiedAttributes.subList(0, numberOfFeatures);
	}

	@SuppressWarnings("unused")
	private static final String[] carAttrNames = { "buying", "maint", "doors", "persons", "lug_boot", "safety" };
	@SuppressWarnings("unused")
	private static final String[] nurseryArrtNames = { "parents", "has_nurs", "form", "children", "housing", "finance",
			"social", "health" };
}
