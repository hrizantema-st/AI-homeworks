package kMeans;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	public Cluster(int id) {
		this.id = id;
		this.points = new ArrayList<>();
		this.centroid = null;
	}
	
	@SuppressWarnings("nls")
	public void print() {
		System.out.println("Cluster id: " + this.id);
		System.out.println("Centroid: " + this.centroid);
		System.out.println("Points:");
		for(Point p : this.points) {
			System.out.println(p);
		}
		System.out.println();
	}

	public List<Point> points;
	public Point centroid;
	public int id;
	
	public Point getCentroid() {
		return this.centroid;
	}
}
