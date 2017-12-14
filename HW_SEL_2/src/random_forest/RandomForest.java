package random_forest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("nls")
public class RandomForest {

	@SuppressWarnings("boxing")
	public static void main(String[] args) throws IOException {
		String dataset = args[0];
		int numberOfTrees = Integer.valueOf(args[1]);
		int numberOfFeatures = Integer.valueOf(args[2]);
		System.out.println("The chosen dataset is " + dataset);

		final String csvFileName = dataset + ".txt";
		List<List<String>> data = null;
		try {
			data = Utils.readTXTFile(csvFileName);
			System.out.println("Total number of instaces in the dataset: " + data.size());
		} catch (IOException e) {
			System.out.println("No file with the given name exists!!!");
		}
		System.out.println("Total number of trees in the forest: " + numberOfTrees);
		System.out.println("Total number of features selected: " + numberOfFeatures);

		List<Feature> features = Utils.calculateFeatures(data);

		List<List<String>> trainingData = new ArrayList<>(data.subList(0, (int) (data.size() * 0.66)));
		//List<List<String>> testData = new ArrayList<>(data.subList((int) (data.size() * 0.66), data.size()));
		List<List<String>> testData = Utils.deepCopyOfData(trainingData);

		int indexOfClassLabel = data.get(0).size() - 1;
		System.out.println("==========================");

		// Collections.shuffle(data);
		List<DecisionTree> randomForest = randomForest(numberOfFeatures, numberOfTrees, trainingData, features);

		for (int i = 0; i < randomForest.size(); i++) {
			randomForest.get(i).printTree();
		}
		List<String> classesObtained = new ArrayList<>();
		for (int i = 0; i < testData.size(); i++) {
			String resultClass = classify(testData.get(i), randomForest);
			classesObtained.add(resultClass);
			System.out.println(
					"RESULT should be: " + testData.get(i).get(indexOfClassLabel) + " --- it is: " + resultClass);
		}
		System.out.println("PRECISION: " + Utils.calculatePrecision(testData, classesObtained));
		
		List<Feature> sortedFeatures = featuresImportance(randomForest);
		System.out.println("FEATURES SIZE: ---------------------------- " + sortedFeatures.size());
		for(int i = sortedFeatures.size() - 1; i >= 0; i--) {
			System.out.println(sortedFeatures.get(i));
		}
	}

	/**
	 * This method is responsible for inducing the model (the random forest)
	 * from the training data set,
	 * 
	 * @param F
	 *            - number of random features used in the splitting of the nodes
	 * @param NT
	 *            * -number of trees desired.
	 * @param dataSet
	 * @param attributes
	 *            - all attributes available
	 * 
	 */

	public static List<DecisionTree> randomForest(final int F, final int NT, final List<List<String>> dataSet,
			List<Feature> attributes) {

		List<DecisionTree> forest = new ArrayList<>();
		List<List<String>> data = Utils.deepCopyOfData(dataSet);
		for (int i = 0; i < NT; i++) {
			List<Feature> coppiedAttr = Utils.deepCopyOfFeatures(attributes);
			Node dt = DecisionTree.id3forRandomForest(data.subList(0, data.size()), coppiedAttr, F);
			forest.add(new DecisionTree(dt));
		}
		return forest;
	}

	

	/**
	 * This method is responsible for producing an ordered list of the features used in the forest,
	 * according to its importance. The importance can be estimated as the
	 * frequency of its appearance in the random forest constructed.
	 */
	public static List<Feature> featuresImportance(final List<DecisionTree> forest) {
		Map<Feature, Integer> frequencies = new HashMap<>();
		for(DecisionTree dt: forest) {
			dt.traverseTree(frequencies);
			
		}
		frequencies.remove(null);
		Map<Feature, Integer> sortedFeatures = MapUtil.sortByValue(frequencies);
		List<Feature> result = new ArrayList<>();
		for(Feature f: sortedFeatures.keySet()) {
			result.add(f);
			
		}
		return result;
	}

	/**
	 * This method return a class obtained for a given data example by a id3
	 * @param dataSample - example to be classified
	 * @param t tree which to classify the example
	 * @return class obtained from application of id3 algorithm
	 */
	

	@SuppressWarnings("boxing")
	/**
	 * This method return a class obtained for a given data example by a random forest
	 * @param dataSample - example to be classified
	 * @param forest - random forest 
	 * @return class
	 */
	public static String classify(final List<String> dataSample, final List<DecisionTree> forest) {
		final Map<String, Long> countClasses = forest.stream().map(tree -> tree.classify(dataSample))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		final long maxFrequency = countClasses.values().stream().mapToLong(count -> count).max().orElse(-1);
		return countClasses.entrySet().stream().filter(tuple -> tuple.getValue() == maxFrequency).map(Map.Entry::getKey)
				.collect(Collectors.toList()).get(0);
	}
}
