package kMeans;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	private static List<Point> points = new ArrayList<>();
	private static List<Cluster> clusters = new ArrayList<>();

	public static double calculateDistance(final Point point1, final Point point2) {
		double result = Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2));
		return result;
	}

	public static void formClusters() {
		int minDistance = Double.MAX_EXPONENT;
		for (int i = 0; i < points.size(); i++) {
			for (int j = 0; j < clusters.size(); j++) {
				double dist = calculateDistance(points.get(i), clusters.get(j).centroid);
				int clusterIndex = -1;
				if (dist < minDistance) {
					clusterIndex = j;
				}
				for (int k = 0; k < clusters.size(); k++) {
					if (clusters.get(k).id == clusterIndex) {
						clusters.get(k).points.add(points.get(i));
						points.get(i).clusterNumber = k;

					}
				}
			}

		}
	}

	private static List<Point> getCentroids() {
		return clusters.stream().map(a -> a.getCentroid()).collect(Collectors.toList());
	}

	private static void calculateCentroids() {
		for (Cluster cluster : clusters) {
			double sumX = 0;
			double sumY = 0;
			List<Point> allPointsInCluster = cluster.points;

			for (Point point : allPointsInCluster) {
				sumX += point.x;
				sumY += point.y;
			}

			Point centroid = cluster.centroid;
			if (allPointsInCluster.size() > 0) {
				double newX = sumX / allPointsInCluster.size();
				double newY = sumY / allPointsInCluster.size();
				centroid.x = newX;
				centroid.y = newY;
			}
		}
	}

	@SuppressWarnings({ "boxing", "nls" })
	public static void main(String[] args) {
		int k;
		try (Scanner reader = new Scanner(System.in)) {
			System.out.println("Enter value for k: ");
			k = reader.nextInt();
		}

		String fileName = "resources/unbalance.txt";

		List<String> allLinesFromDataset = new ArrayList<>();

		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			allLinesFromDataset = stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		Collections.shuffle(allLinesFromDataset);

		for (String line : allLinesFromDataset) {
			List<String> pointAsArray = Arrays.asList(line.split(" "));
			Point point = new Point(Double.valueOf(pointAsArray.get(0)), Double.valueOf(pointAsArray.get(1)));
			points.add(point);
		}

		List<Point> centroids = new ArrayList<>(points.subList(0, k));
		for (int i = 0; i < k; i++) {
    		Cluster cluster = new Cluster(i);
    		cluster.centroid = centroids.get(i);
    		clusters.add(cluster);
		}
		boolean finish = false;

		while (!finish) {
			for (Cluster cluster : clusters) {
				cluster.points.clear();
			}
			List<Point> lastCentroids = getCentroids();
			formClusters();

			calculateCentroids();
			List<Point> currentCentroids = getCentroids();

			double distanceBetweenTheLastTwoSetsOfCentroids = 0;
			for (int i = 0; i < lastCentroids.size(); i++) {

				distanceBetweenTheLastTwoSetsOfCentroids += calculateDistance(lastCentroids.get(i),
						currentCentroids.get(i));

			}
			if (distanceBetweenTheLastTwoSetsOfCentroids == 0) {
				finish = true;
			}
		}
		for (int i = 0; i < k; i++) {
			Cluster cluster = clusters.get(i);
			cluster.print();
		}
	}

}
