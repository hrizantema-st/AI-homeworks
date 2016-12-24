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

public class Main {
	@SuppressWarnings({ "boxing", "nls" })
	public static void generateTable(final List<List<String>> data, final String party, final List<Integer> result) {
		Stream<List<String>> streamch = data.stream().filter(a -> a.get(0).equals(party));
		List<List<String>> tmp = streamch.collect(Collectors.toList());

		for (int i = 1; i <= 16; i++) {
			final int j = i;
			Stream<String> stream1 = tmp.stream().map(a -> a.get(j));
			Map<String, Long> map = stream1.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			result.add((int) (long) map.get("y"));
			result.add((int) (long) map.get("n"));
		}
	}

	@SuppressWarnings({ "nls", "boxing" })
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

	public static double getAccuracy(List<List<String>> testSet, List<List<String>> predictions) {
		int correct = 0;
		for (int i = 0; i < testSet.size(); i++) {
			if (testSet.get(i).get(0).equals(predictions.get(i).get(0))) {
				correct += 1;
			}
		}
		return (correct / (double) testSet.size()) * 100.0;
	}

	public static double getAccuracy2(List<String> testSet, List<String> predictions) {
		int correct = 0;
		for (int i = 0; i < testSet.size(); i++) {
			if (testSet.get(i).equals(predictions.get(i))) {
				correct += 1;
			}
		}
		return (correct / (double) testSet.size()) * 100.0;
	}

	@SuppressWarnings("nls")
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

		List<List<String>> setToClassify = new ArrayList<>(dataAsTable.subList(0, 20));
		List<List<String>> resultSet = new ArrayList<>();
		List<String> names = new ArrayList<>();
		for (int i = 0; i < setToClassify.size(); i++) {
			resultSet.add(setToClassify.get(i));
			String name = setToClassify.get(i).get(0);
			names.add(name);
			System.out.println(name);
		}
		List<List<String>> trainingSet = new ArrayList<>(dataAsTable.subList(20, allLinesFromDataset.size()));
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
		System.out.println("--------------------");
		for (int i = 0; i < resultSet.size(); i++) {
			System.out.println(resultSet.get(i).get(0));
		}

		System.out.println("ACCURACY: "
				+ getAccuracy2(names, setToClassify.stream().map(a -> a.get(0)).collect(Collectors.toList())));
	}
}