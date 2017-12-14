package random_forest;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class contains the data structure that is used for the decision tree and
 * all the methods that are necessary for the intermediate calculations
 * 
 * @author hstancheva
 *
 */
@SuppressWarnings("nls")
public class DecisionTree {
	private Node root;

	public DecisionTree() {
		this.root = null;
	}

	public DecisionTree(final Node root) {
		this.root = root;
	}

	public Node getRoot() {
		return this.root;
	}

	public void setRoot(final Node root) {
		this.root = root;
	}

	private static void printNodeValue(Node node) {
		System.out.println(node.getLabel());

	}
	/*
	 * public void print() { this.root.print(); if (this.root.getChildren() !=
	 * null) { for (int j = 0; j < this.root.getChildren().size(); j++) {
	 * System.out.println("Branch VALUE: " +
	 * this.root.getBranchValues().get(j));
	 * this.root.getChildren().get(j).print(); } } }
	 */

	private void printTree(Node node, boolean isRight, String indent) {
		int numChild = 0;
		if (!node.getChildren().isEmpty()) {
			numChild = node.getChildren().size();
		}
		for (int i = 0; i < numChild / 2; i++) {
			if (!node.getChildren().isEmpty() && node.getChildren().get(0) != null) {
				printTree(node.getChildren().get(0), true, indent + (isRight ? "        " : " |      "));
			}
		}
		System.out.print(indent);

		if (isRight) {
			System.out.print(" /");
		} else {
			System.out.print(" \\");
		}
		System.out.print("----- ");
		printNodeValue(node);
		for (int i = numChild / 2; i < numChild; i++) {
			if (node.getChildren().size() > 1 && node.getChildren().get(1) != null) {
				printTree(node.getChildren().get(1), false, indent + (isRight ? " |      " : "        "));
			}
		}
	}

	public void printTree() {
		printSubtree(root);
	}

	public void printSubtree(Node node) {
		int numChild = 0;
		if (!node.getChildren().isEmpty()) {
			numChild = node.getChildren().size();
		}
		for (int i = 0; i < numChild / 2; i++) {
			if (!node.getChildren().isEmpty() && node.getChildren().get(i) != null) {
				printTree(node.getChildren().get(0), true, "");
			}
		}
		printNodeValue(node);
		for (int i = numChild / 2; i < numChild; i++) {
			if (node.getChildren().size() > 1 && node.getChildren().get(1) != null) {
				printTree(node.getChildren().get(1), false, "");
			}
		}
	}

	/**
	 * id3 algorithm responsible for generating a model
	 * 
	 * @param features
	 *            - vector of randomly selected features
	 * @param examples
	 *            - training data
	 */
	public static Node id3(final List<List<String>> examples, final Feature targetAttribute,
			final List<Feature> features) {
		int classIndex = examples.get(0).size() - 1;
		List<Feature> attributes = Utils.deepCopyOfFeatures(features);
		Node root = new Node();

		if (isHomogenous(examples)) {
			String currentClass = examples.get(0).get(classIndex);
			root.setLabel(currentClass);
			root.setLeaf(true);
			// System.out.println("LEAF ADDED: " + currentClass);
			return root;
		}
		if (attributes.isEmpty()) {
			String majorityClass = majorityClass(examples);
			root.setLabel(majorityClass);
			root.setLeaf(true);
			// System.out.println("LEAF ADDED: " + majorityClass);
			return root;
		} else {
			Feature maxAttribute = maxGain(attributes, examples);
			root.setFeature(maxAttribute);

			for (String value : maxAttribute.getValues()) {
				List<List<String>> newExamples = examples.stream()
						.filter(example -> example.get(maxAttribute.getColumnPosition()).equals(value))
						.collect(Collectors.toList());
				if (newExamples.isEmpty()) {
					String majorityClass = majorityClass(examples);
					root.setLabel(majorityClass);
					root.setLeaf(true);
					// System.out.println("LEAF ADDED: " + majorityClass);
					return root;
				} else {
					attributes.remove(maxAttribute);
					root.addChild(id3(newExamples, targetAttribute, attributes));
					// System.out.println("CHILD ADDED: ");
					root.addBranchValue(value);
				}
			}
		}
		return root;
	}

	/**
	 * This method is responsible for checking if a data set is homogenous,
	 * which means if all the examples in the data set have the same class
	 * 
	 * @param data
	 * @return true if the data set is homogenous, false otherwise
	 */
	private static boolean isHomogenous(final List<List<String>> data) {
		final int classIndex = data.get(0).size() - 1;
		final String classOfFirstElem = data.get(0).get(classIndex);
		return data.stream().map(example -> example.get(classIndex)).allMatch(e -> e.equals(classOfFirstElem));
	}

	/**
	 * Given a data set of examples this method is finding the mode class of all
	 * examples
	 * 
	 * @param data
	 *            from which we extract the mode class
	 * @return majority class
	 */
	private static String majorityClass(final List<List<String>> data) {
		final int classIndex = data.get(0).size() - 1;
		Map<Object, List<String>> mapOfClassFreq = data.stream().map(example -> example.get(classIndex))
				.collect(Collectors.groupingBy(Function.identity()));
		int maxCount = 0;
		String maxClassName = null;
		for (Map.Entry<Object, List<String>> entry : mapOfClassFreq.entrySet()) {
			if (entry.getValue().size() > maxCount) {
				maxCount = entry.getValue().size();
				maxClassName = entry.getKey().toString();
			}
		}
		return maxClassName;
	}

	/**
	 * This method is responsible for finding the feature with maximum
	 * information gain
	 * 
	 * @param attributes
	 *            the set of features from which we want to find the max feature
	 * @param dataset
	 *            on which the calculations are made
	 * @return the feature that provides the maximum information gain
	 */
	private static Feature maxGain(final List<Feature> attributes, final List<List<String>> dataset) {
		Feature maxAttr = attributes.get(0);
		double maxGain = calculateInformationGain(attributes.get(0), dataset);
		for (int i = 1; i < attributes.size(); i++) {
			double currentGain = calculateInformationGain(attributes.get(i), dataset);
			if (currentGain > maxGain) {
				maxGain = currentGain;
				maxAttr = attributes.get(i);
			}
		}
		return maxAttr;
	}

	/**
	 * This method is responsible for calculating the information gain for a
	 * given feature of a data set The calculations are based on the formula:
	 * Gain(X, Ak) = Info(X,C) -Info(X, Ak) ⇔ Info(X, Ak) ≈ 0
	 * 
	 * @param feature
	 *            for which we want to calculate the information gain
	 * @param dataset
	 *            is the data set used for the calculations
	 * @return double of the information gain calculated
	 */
	public static double calculateInformationGain(final Feature feature, final List<List<String>> dataset) {
		return calculateEntropy(dataset) - calculateInfo(feature, dataset);
	}

	/**
	 * This method is responsible for calculating the entropy of a given data
	 * set.
	 * 
	 * @param data
	 *            on which the calculations are based
	 * @return entropy
	 */
	public static double calculateEntropy(final List<List<String>> data) {
		int classIndex = data.get(0).size() - 1;
		Map<Object, List<String>> mapOfClassFreq = data.stream().map(example -> example.get(classIndex))
				.collect(Collectors.groupingBy(Function.identity()));

		int numberOfExamples = data.size();
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
	 * This method is responsible for calculating the info for a given attribute
	 * based on the following formula: Info(X, Ak)= Σp(X,vl) * H(A-1k(vl)),
	 * where vl is from V(Ak)
	 * 
	 * @param attribute
	 *            for which we want to make the calculations
	 * @param data
	 *            on which the calculations are based
	 * @return double of the calculated info value
	 */
	public static double calculateInfo(final Feature attribute, final List<List<String>> data) {
		Map<Object, List<String>> mapOfValuesFreq = data.stream()
				.map(example -> example.get(attribute.getColumnPosition()))
				.collect(Collectors.groupingBy(Function.identity()));

		int numberOfExamples = data.size();
		double info = 0;
		for (Map.Entry<Object, List<String>> entry : mapOfValuesFreq.entrySet()) {
			int currentNumOfInstances = entry.getValue().size();
			double currentProbability = (double) currentNumOfInstances / (double) numberOfExamples;
			List<List<String>> coveredExamples = data.stream()
					.filter(example -> example.get(attribute.getColumnPosition()).equals(entry.getKey()))
					.collect(Collectors.toList());
			info += currentProbability * calculateEntropy(coveredExamples);
		}
		return info;
	}
}
