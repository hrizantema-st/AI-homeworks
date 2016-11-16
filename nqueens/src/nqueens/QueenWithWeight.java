package nqueens;

public class QueenWithWeight implements Comparable<Object> {
	public QueenWithWeight(final int x, final int y, final int weigth) {
		this.x = x;
		this.y = y;
		this.weigth = weigth;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.weigth;
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
		QueenWithWeight other = (QueenWithWeight) obj;
		if (this.weigth != other.weigth)
			return false;
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		return true;
	}
	@Override
	public int compareTo(Object other) {
		QueenWithWeight qww = (QueenWithWeight) other;
	    return Integer.compare(this.weigth, qww.weigth);
	}
	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "["+ this.x +", " + this.y + "]" + ":" + this.weigth;
		
	}
	public int x;
	public int y;
	public int weigth;
	
}
