import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Game {
	public static final int PLAYER = 2;
	public static final int COMPUTER = 1;

	static List<PointsAndScores> rootsChildrenScore = new ArrayList<>();

	public static final int n = 3;
	public static int[][] ticTacToeBoard = new int[n][n];
	public static List<Positions> availablePoints;
	public static Scanner scan = new Scanner(System.in);

	public static Positions returnBestMove() {
		 int MAX = -100000;
	        int best = -1;

	        for (int i = 0; i < rootsChildrenScore.size(); ++i) {
	            if (MAX < rootsChildrenScore.get(i).score) {
	                MAX = rootsChildrenScore.get(i).score;
	                best = i;
	            }
	        }

	        return rootsChildrenScore.get(best).point;
	/*	Optional<PointsAndScores> ps = rootsChildrenScore.stream().sorted().findFirst();
		if (ps.isPresent()) {
			return ps.get().point;
		}
		return null;*/
	}

	/**
	 * Returns a list of available positions in the board
	 * 
	 * @return
	 */
	public static List<Positions> getAvailableStates() {
		availablePoints = new ArrayList<>();
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				if (ticTacToeBoard[i][j] == 0) {
					availablePoints.add(new Positions(i, j));
				}
			}
		}
		return availablePoints;
	}

	public static void placeAMove(Positions point, int player) {
		ticTacToeBoard[point.x][point.y] = player; // player = 1 for X, 2 for O
	}

	@SuppressWarnings("nls")
	public static void takeHumanInput() {
		System.out.println("Your move: ");
		int x = scan.nextInt();
		int y = scan.nextInt();
		Positions point = new Positions(x, y);
		placeAMove(point, 2);
	}

	public static boolean isGameOver() {
		return (hasXWon() || hasOWon() || getAvailableStates().isEmpty());
	}

	public static boolean hasXWon() {
		return evaluate() == 10;
	}

	public static boolean hasOWon() {
		return evaluate() == (-10);
	}

	private static int evaluate() {
		for (int i = 0; i < n; i++) {
			// rows
			if ((ticTacToeBoard[i][0] == ticTacToeBoard[i][1]) && (ticTacToeBoard[i][1] == ticTacToeBoard[i][2])) {
				if (ticTacToeBoard[i][0] == PLAYER) {
					return -10;
				} else if (ticTacToeBoard[i][0] == COMPUTER) {
					return +10;
				}
			}
			// columns
			if ((ticTacToeBoard[0][i] == ticTacToeBoard[1][i]) && (ticTacToeBoard[1][i] == ticTacToeBoard[2][i])) {
				if (ticTacToeBoard[0][i] == PLAYER) {
					return -10;
				} else if (ticTacToeBoard[0][i] == COMPUTER) {
					return +10;
				}
			}
		}
		// main diagonal
		if ((ticTacToeBoard[0][0] == ticTacToeBoard[1][1]) && (ticTacToeBoard[1][1] == ticTacToeBoard[2][2])) {
			if (ticTacToeBoard[0][0] == PLAYER) {
				return -10;
			} else if (ticTacToeBoard[0][0] == COMPUTER) {
				return +10;
			}
		}
		// secondary diagonal
		if ((ticTacToeBoard[0][2] == ticTacToeBoard[1][1]) && (ticTacToeBoard[1][1] == ticTacToeBoard[2][0])) {
			if (ticTacToeBoard[0][2] == PLAYER) {
				return -10;
			} else if (ticTacToeBoard[0][2] == COMPUTER) {
				return +10;
			}
		}
		return 0;
	}

	@SuppressWarnings("nls")
	public static void print(final int[][] board) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static int[][] copyBoard(final int[][] board) {
		int[][] newBoard = new int[n][n];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				newBoard[i][j] = board[i][j];
			}
		}
		return newBoard;
	}

	/*
	 * public static List<int[][]> generateChildrens(final int player, final
	 * int[][] node) { List<int[][]> queue = new ArrayList<>(); for (int i = 0;
	 * i < 3; i++) { for (int j = 0; j < 3; j++) { Positions currentPosition =
	 * new Positions(i, j); if (!visited.contains(currentPosition)) { int[][]
	 * newBoard = copyBoard(node); newBoard[i][j] = player; queue.add(newBoard);
	 * } } } return queue; }
	 */
	static int uptoDepth = -1;

	public static int alphaBeta(int alpha, int beta, int depth, int turn) {
		if (beta <= alpha) {
			if (turn == 1)
				return Integer.MAX_VALUE;
			else
				return Integer.MIN_VALUE;
		}

		if (depth == uptoDepth || isGameOver())
			return evaluate();

		List<Positions> pointsAvailable = getAvailableStates();

		if (pointsAvailable.isEmpty())
			return 0;

		if (depth == 0)
			rootsChildrenScore.clear();

		int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;

		for (int i = 0; i < pointsAvailable.size(); ++i) {
			Positions point = pointsAvailable.get(i);
			int currentScore = 0;
			if (turn == 1) {
				placeAMove(point, 1);
				currentScore = alphaBeta(alpha, beta, depth + 1, 2);
				maxValue = Math.max(maxValue, currentScore);

				// Set alpha
				alpha = Math.max(currentScore, alpha);

				if (depth == 0) {
					rootsChildrenScore.add(new PointsAndScores(currentScore, point));
				}
			} else if (turn == 2) {
				placeAMove(point, 2);
				currentScore = alphaBeta(alpha, beta, depth + 1, 1);
				minValue = Math.min(minValue, currentScore);

				// Set beta
				beta = Math.min(currentScore, beta);
			}
			ticTacToeBoard[point.x][point.y] = 0;
			// If a pruning has been done, don't evaluate the rest of the
			// sibling states
			if (currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE)
				break;
		}
		return turn == 1 ? maxValue : minValue;
	}

	@SuppressWarnings({ "nls" })
	public static void main(String[] args) {
		while (!isGameOver()) {
			/* takeHumanInput(); */
			System.out.println("Your move: ");
			Positions userMove = new Positions(scan.nextInt(), scan.nextInt());

			placeAMove(userMove, 2);
			if (isGameOver()) {
				break;
			}

			alphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1);
			placeAMove(returnBestMove(), 1);

			System.out.println("Current state of the board:");
			print(ticTacToeBoard);

		}

		if (hasXWon()) {
			System.out.println("Unfortunately, you lost!");
		} else if (hasOWon()) {
			System.out.println("You win!");
		} else {
			System.out.println("It's a draw!");
		}
	}

}
