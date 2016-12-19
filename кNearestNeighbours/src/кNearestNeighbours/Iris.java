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
	
	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "Iris [sepalLength=" + this.sepalLength + ", sepalWidth=" + this.sepalWidth + ", petalLength=" + this.petalLength
				+ ", petalWidth=" + this.petalWidth + ", name=" + this.name + "]";
	}

	public double sepalLength;
	public double sepalWidth;
	public double petalLength;
	public double petalWidth;
	public String name;
}
