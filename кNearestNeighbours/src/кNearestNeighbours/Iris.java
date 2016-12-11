package кNearestNeighbours;

public class Iris {
	public Iris(final double sepalLength, final double sepalWidth, final double petalLength, final double petalWidth,
			final String name) {
		super();
		this.sepalLength = sepalLength;
		this.sepalWidth = sepalWidth;
		this.petalLength = petalLength;
		this.petalWidth = petalWidth;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return "Iris [sepalLength=" + sepalLength + ", sepalWidth=" + sepalWidth + ", petalLength=" + petalLength
				+ ", petalWidth=" + petalWidth + ", name=" + name + "]";
	}

	public double sepalLength;
	public double sepalWidth;
	public double petalLength;
	public double petalWidth;
	public String name;
}
