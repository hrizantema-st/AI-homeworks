package kMeans;

public class Point {
	public Point(final double x, final double y) {
		super();
		this.x = x;
		this.y = y;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "[" + this.x + ", " +  this.y + "]";
	}

	public double x;
	public double y;
	public int clusterNumber;

}
