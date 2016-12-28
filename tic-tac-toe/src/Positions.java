

public class Positions {
	public Positions(final int x, final int y) {
		this.x = x;
		this.y = y;
	}
	public int x;
	public int y;
	
	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "[" + this.x + ", " + this.y + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.x;
		result = prime * result + this.y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Positions other = (Positions) obj;
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		return true;
	}
	
	
}
