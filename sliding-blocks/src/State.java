import java.util.Arrays;

/**
 * This class is responsible for keeping the current state of the game, which
 * includes the state of the board, price of the path from the initial state to
 * the current state of the game, value of the heuristic function which
 * represents the estimation to the goal state of the game (sum of all the moves
 * that all tiles has to do in order to reach their final place. This class also
 * keeps the coordinates of the empty tail(represented by 0 in the game) The
 * class implement the Comparable interface in order to be put in a priority
 * queue later.
 * 
 * @author hstancheva
 *
 */
public class State implements Comparable<State> {
	public int price, heuristic;
	public int[][] board;
	public int x, y;

	public State() {
		super();
	}

	public State(final int price, final int heuristic, final int[][] board) {
		super();
		this.price = price;
		this.heuristic = heuristic;
		this.board = board;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (board[i][j] == 0) {
					this.x = i;
					this.y = j;
				}
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(this.board);
		result = prime * result + this.heuristic;
		result = prime * result + this.price;
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
		State other = (State) obj;
		if (!Arrays.deepEquals(this.board, other.board))
			return false;
		if (this.heuristic != other.heuristic)
			return false;
		if (this.price != other.price)
			return false;
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		return true;
	}

	@Override
	public int compareTo(State o) {
		return Integer.compare((this.price + this.heuristic), (o.price + o.heuristic));
	}

}
