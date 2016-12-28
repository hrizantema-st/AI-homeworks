import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("nls")
public class Main {
	/**
	 * This method creates a frequency table for all the 16 categories for a
	 * given party name; There are two columns for each category: first one is
	 * for the number of 'yes' answers and the second one is for 'no' answers
	 * 
	 * @param data
	 *            initial data for which to make frequency table
	 * @param party
	 *            the name of the party for which to generate frequency table
	 * @param result
	 *            the result of the calculations
	 */
	@SuppressWarnings({ "boxing" })
	public static void generateTable(final List<List<String>> data, final String party, final List<Integer> result) {
		Stream<List<String>> streamch = data.stream().filter(a -> a.get(0).equals(party));
		List<List<String>> tmp = streamch.collect(Collectors.toList());

		for (int i = 1; i <= 16; i++) {
			final int j = i;
			Stream<String> stream1 = tmp.stream().map(a -> a.get(j));
			Map<String, Long> map = stream1.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

			long yesAnswers = map.get("y") == null ? 0 : map.get("y");
			result.add((int) (long) yesAnswers);
			long noAnswers = map.get("n") == null ? 0 : map.get("n");
			result.add((int) (long) noAnswers);
		}
	}

	@SuppressWarnings({ "boxing" })
	/**
	 * This method calculate the likelihood for given person to be from a given
	 * party, based on given data
	 * 
	 * @param data
	 *            frequency table, which is used to make the calculations
	 * @param party
	 *            the party for which to calculate the probability
	 * @param person
	 *            the person to classify
	 * @param all
	 *            number of all data in the dataset
	 * @return
	 */
	public static double likelihood(final List<List<Integer>> data, final String party, final List<String> person,
			final double all) {
		double likelihood = 1.0;
		for (int i = 1; i <= 16; i++) {
			List<Integer> currentDataToUseBasedOnParty = party.equals("democrat") ? data.get(1) : data.get(0);
			if (person.get(i).equals("?")) {
				continue;
			} else {
				int index = person.get(i).equals("y") ? (2 * i - 2) : (2 * i - 1);
				likelihood *= (currentDataToUseBasedOnParty.get(index) / all);
			}
		}
		return likelihood;
	}

	/**
	 * This method find the accuracy of classification
	 * 
	 * @param testSet
	 *            the initial names
	 * @param predictions
	 *            the names found by the classification algorithm
	 * @return percentage of accuracy
	 */
	public static double getAccuracy(List<String> testSet, List<String> predictions) {
		int correct = 0;
		for (int i = 0; i < testSet.size(); i++) {
			if (testSet.get(i).equals(predictions.get(i))) {
				correct += 1;
			}
		}
		return (correct / (double) testSet.size()) * 100.0;
	}

	/**
	 * This method is responsible for classifying set of given data, using
	 * information from another set(the second argument) and returns the
	 * accuracy of classification.
	 * 
	 * @param setToClassify
	 *            this is the target set for which to find the classes of
	 *            objects
	 * @param trainingSet
	 *            this set is used to teach the algorithm
	 * @return the accuracy of the classification
	 */
	public static double kFoldCrossValidation(List<List<String>> setToClassify, List<List<String>> trainingSet) {
		List<List<String>> resultSet = new ArrayList<>();
		List<String> names = new ArrayList<>();
		for (int i = 0; i < setToClassify.size(); i++) {
			resultSet.add(setToClassify.get(i));
			String name = setToClassify.get(i).get(0);
			names.add(name);
		}
		double numberOfRepublicans = trainingSet.stream().filter(candidate -> candidate.get(0).equals("republican"))
				.count();
		double probabilityToBeRepublican = numberOfRepublicans / trainingSet.size();
		double numberOfDemocrats = trainingSet.stream().filter(candidate -> candidate.get(0).equals("democrat"))
				.count();
		double probabilityToBeDemocrat = numberOfDemocrats / trainingSet.size();

		List<List<Integer>> result = new ArrayList<>();

		List<Integer> republicanStatistic = new ArrayList<>();
		List<Integer> democratStatistics = new ArrayList<>();
		generateTable(trainingSet, "republican", republicanStatistic);
		generateTable(trainingSet, "democrat", democratStatistics);
		result.addAll(Arrays.asList(republicanStatistic, democratStatistics));

		int numberOfAllCandidatesInTrainigSet = trainingSet.size();

		for (int i = 0; i < resultSet.size(); i++) {
			double posteriorProbabilityToBeRepublican = likelihood(result, "republican", resultSet.get(i),
					numberOfAllCandidatesInTrainigSet);
			posteriorProbabilityToBeRepublican *= probabilityToBeRepublican;

			double posteriorProbabilityToBeDemocrat = likelihood(result, "democrat", resultSet.get(i),
					numberOfAllCandidatesInTrainigSet);
			posteriorProbabilityToBeDemocrat *= probabilityToBeDemocrat;
			String nameClassified = posteriorProbabilityToBeDemocrat > posteriorProbabilityToBeRepublican ? "democrat"
					: " republican";
			resultSet.get(i).set(0, nameClassified);

		}
		double currentAccuracy = getAccuracy(names,
				setToClassify.stream().map(a -> a.get(0)).collect(Collectors.toList()));
		return currentAccuracy;
	}

	/**
	 * The main method reads the data from txt file, shuffle it and split it
	 * into 10 (equal) pieces, for each of which applies k fold cross
	 * validation.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String fileName = "resources/house-votes.txt";

		List<String> allLinesFromDataset = new ArrayList<>();
		List<List<String>> dataAsTable = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			allLinesFromDataset = stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		Collections.shuffle(allLinesFromDataset);

		for (String line : allLinesFromDataset) {
			String[] strArray = line.split(",");
			List<String> tmp = new ArrayList<>(Arrays.asList(strArray));
			dataAsTable.add(tmp);

		}
		double sumOfAccuracy = 0;
		int tmp = dataAsTable.size() / 10;
		for (int i = 0; i < 10; i++) {
			/*
			 * System.out.println("INTERVAL: [" + (i * tmp)+ "," + ((i+1)*tmp) +
			 * "]");
			 */
			List<List<String>> setToClassify = new ArrayList<>(dataAsTable.subList(i * tmp, (i + 1) * tmp));
			List<List<String>> trainingSet = new ArrayList<>();
			List<List<String>> trainingSet2 = new ArrayList<>();
			if (i >= 1) { //for all instead of first set
				trainingSet = new ArrayList<>(dataAsTable.subList(0, i * tmp));
			}
			if (i < 9) { //for all instead of last set
				trainingSet2 = new ArrayList<>(dataAsTable.subList((i + 1) * tmp, dataAsTable.size()));
			}
			trainingSet.addAll(trainingSet2);
			double currentAccuracy = kFoldCrossValidation(setToClassify, trainingSet);
			
			System.out.println("Accuracy of " + (i + 1) + "set: " + currentAccuracy + "%");

			sumOfAccuracy += currentAccuracy;
		}
		System.out.println("Average accuracy: " + sumOfAccuracy / (double) 10 + "%");

	}

}