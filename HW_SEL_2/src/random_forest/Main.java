package random_forest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.jws.soap.SOAPBinding;

@SuppressWarnings("nls")
public class Main {
	
	public static void main(String[] args) throws IOException {
		String dataset = args[0];
		System.out.println("The chosen dataset is " + dataset);

		final String csvFileName = dataset + ".txt";
		List<List<String>> data = null;
		try {
			data = Utils.readTXTFile(csvFileName);
			System.out.println("Total number of instaces in the dataset: " + data.size());
		} catch (IOException e) {
			System.out.println("No file with the given name exists!!!");
		}
		List<Feature> attributes = Utils.calculateAttributesPlusClass(data);
		System.out.println("ORIGINAL SIZE: " + attributes.size());
		List<Feature> coppiedF = Utils.deepCopyOfFeatures(attributes);
		coppiedF.remove(0);

		Feature targetAttribute = attributes.get(attributes.size() - 1);
		System.out.println("==========================");
		// DecisionTree dt = new DecisionTree(DecisionTree.id3(data,
		// targetAttribute, attributes.subList(0, attributes.size() - 1)));
		// dt.print();
		Node rootNode = DecisionTree.id3(data, targetAttribute, attributes.subList(0, attributes.size() - 1));
		// List<String> curData = Arrays.asList("pre-prebi?pic", "miope", "no",
		// "normal");
		// String resultClass = classify(curData, rootNode);
		// System.out.println("RESULT should be cap;; it is: " + resultClass);
		DecisionTree dt = new DecisionTree(rootNode);
		dt.printTree();
	}

	/**
	 * This method is responsible for inducing the model (the random forest)
	 * from the training data set,
	 * 
	 * @param F - number of random features used in the splitting of the nodes 
	 * @param NT * -number of trees desired.
	 * @param dataSet 
	 * @param attributes - all attributes available
	 * 
	 */

	public static List<DecisionTree> randomForest(final int F, final int NT, final List<List<String>> dataSet,
			List<Feature> attributes) {

		List<DecisionTree> forest = new ArrayList<>();
		List<List<String>> data = Utils.deepCopyOfData(dataSet);
		for (int i = 0; i < NT; i++) {
			//Collections.shuffle(data);
			List<Feature> currentSubsetOfAttr = randomSubsetOfAttributes(attributes, F);
			Node dt = DecisionTree.id3(data.subList(0, data.size() / 10), null, currentSubsetOfAttr);
			forest.add(new DecisionTree(dt));
		}
		return forest;
	}

	public static List<Feature> randomSubsetOfAttributes(final List<Feature> attributes, final int numberOfFeatures) {
		List<Feature> coppiedAttributes = Utils.deepCopyOfFeatures(attributes);
		Collections.shuffle(coppiedAttributes);
		return coppiedAttributes.subList(0, numberOfFeatures);
	}

	/**
	 * forest interpreter that given a random forest would be able to classify a
	 * validation/test data set, obtaining the corresponding classification
	 * accuracy values (or generalization error) for different combination of
	 * values of F and NT.
	 * 
	 */
	public static void forestInterpreter(final List<?> model, final List<List<String>> dataset) {

	}

	public static String classify(List<String> dataSample, Node node) {
		while (!node.isLeaf()) {
			for (int i = 0; i < node.getChildren().size(); i++) {
				if (node.getBranchValues().get(i).equals(dataSample.get(node.getFeature().getColumnPosition()))) {
					node = node.getChildren().get(i);
					break;
				}
			}
		}
		return node.getLabel();
	}
}
