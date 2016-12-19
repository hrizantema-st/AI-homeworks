package кNearestNeighbours;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	public static double calculateDistance(final Iris iris1, final Iris iris2) {
		double result = Math.sqrt(
				Math.pow(iris1.petalLength - iris2.petalLength, 2) + 
				Math.pow(iris1.petalWidth - iris2.petalWidth, 2) + 
				Math.pow(iris1.sepalLength - iris2.sepalLength, 2) + 
				Math.pow(iris1.sepalWidth - iris2.sepalWidth, 2));
		return result;
	}

	public static List<Iris> getNeighbors(final Iris testInstance, final List<Iris> trainingSet, final int k) {
		List<Iris> neighbours = new ArrayList<>();
		Comparator<Iris> compareByDistance = (iris1, iris2) -> Double.compare(calculateDistance(iris1, testInstance),
				calculateDistance(iris2, testInstance));

		List<Iris> allDistances = trainingSet.stream().sorted(compareByDistance).collect(Collectors.toList());
		for (int i = 0; i < k; i++) {
			neighbours.add(i, allDistances.get(i));
		}
		return neighbours;
	}

	@SuppressWarnings("boxing")
	public static String getClassOfIris(final Iris iris, final List<Iris> kNeighbours) {
		Map<String, Long> result = kNeighbours.stream()
				.collect(Collectors.groupingBy(Iris::getName, Collectors.counting()));
		Long max = new Long(0);
		String name = null;
		for (Map.Entry<String, Long> entry : result.entrySet()) {
			if (entry.getValue() > max) {
				name = entry.getKey();
			}
		}
		return name;
	}

	public static double getAccuracy(List<Iris> testSet, List<Iris> predictions) {
		int correct = 0;
		for (int i = 0; i < testSet.size(); i++) {
			if (testSet.get(i).name.equals(predictions.get(i).name)) {
				correct += 1;
			}
		}
		return (correct / (double)testSet.size()) * 100.0;
	}

	@SuppressWarnings({ "boxing", "nls" })
	public static void main(String[] args) {
		int k;
		try (Scanner reader = new Scanner(System.in)) {
			System.out.println("Enter value for k: ");
			k = reader.nextInt();
		}

		String fileName = "resources/iris.txt";

		List<String> allLinesFromDataset = new ArrayList<>();
		List<Iris> allIrisesFromDataset = new ArrayList<>();

		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			allLinesFromDataset = stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		Collections.shuffle(allLinesFromDataset);

		for (String line : allLinesFromDataset) {
			List<String> irisAsString = Arrays.asList(line.split(","));
			Iris iris = new Iris(Double.valueOf(irisAsString.get(0)), Double.valueOf(irisAsString.get(1)),
					Double.valueOf(irisAsString.get(2)), Double.valueOf(irisAsString.get(3)), irisAsString.get(4));
			allIrisesFromDataset.add(iris);
		}

		List<Iris> setToClassify = new ArrayList<>(allIrisesFromDataset.subList(0, 20));
		List<Iris> resultSet = new ArrayList<>();
		for (int i = 0; i < setToClassify.size(); i++) {
			resultSet.add(setToClassify.get(i));
		}

		List<Iris> trainingSet = new ArrayList<>(allIrisesFromDataset.subList(20, allIrisesFromDataset.size()));

		for (int i = 0; i < resultSet.size(); i++) {
			List<Iris> neighbours = getNeighbors(resultSet.get(i), trainingSet, k);
			
			String irisName = getClassOfIris(resultSet.get(i), neighbours);
			Iris tmpIris = new Iris(resultSet.get(i).sepalLength, resultSet.get(i).sepalWidth,
					resultSet.get(i).petalLength, resultSet.get(i).petalWidth, irisName);
			resultSet.set(i, tmpIris);
		}
		
		for(Iris iris: resultSet) { 
			System.out.println(iris.getName());
		}
		System.out.println("ACCURACY: " + getAccuracy(resultSet, setToClassify));
	}

}
