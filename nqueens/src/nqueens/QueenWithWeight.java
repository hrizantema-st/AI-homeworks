package nqueens;

public class QueenWithWeight implements Comparable<Object> {
	QueenWithWeight(final int x, final int y, final int weigth) {
		this.x = x;
		this.y = y;
		this.weigth = weigth;
	}
	@Override
	public int compareTo(Object other) {
		QueenWithWeight qww = (QueenWithWeight) other;
	    return Integer.compare(this.weigth, qww.weigth);
	}
	@Override
	public String toString() {
		return "["+ this.x +", " + this.y + "]" + ":" + this.weigth;
		
	}
	public int x;
	public int y;
	public int weigth;
	
}
