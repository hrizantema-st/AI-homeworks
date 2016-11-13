package nqueens;

import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

@SuppressWarnings("nls")
public class Main {
	private static Queen[] generateChessBoard(final int n) {
		System.out.println("Generating chess board..");
		Queen[] chessBoard = new Queen[n];
		Random rand = new Random();
		for (int column = 0; column < n; column++) {
			int row = rand.nextInt(n);
			chessBoard[column] = new Queen(row, column);
		}
		return chessBoard;
	}

	public static int countConflicts(Queen[] chessBoard) {
		int numberOfConflicts = 0;
		for (int i = 0; i < chessBoard.length - 1; i++) {
			for (int j = i + 1; j < chessBoard.length; j++) {
				if (areFighting(chessBoard[i], chessBoard[j])) {
					numberOfConflicts += 2;
				}
			}
		}
		return numberOfConflicts;
	}

	private static boolean contains(final Queen q, final Queen[] queens) {
		for (int i = 0; i < queens.length; i++) {
			if (queens[i].equals(q)) {
				return true;
			}
		}
		return false;
	}

	private static boolean areFighting(final Queen q1, final Queen q2) {
		if (q1.x == q2.x) {
			return true;
		}
		if (q1.y == q2.y) {
			return true;
		}
		// main diagonal
		if ((q1.x + q1.y) == (q2.x + q2.y)) {
			return true;
		}
		// secondary diagonal
		if ((q1.y - q1.x) == (q2.y - q2.x)) {
			return true;
		}
		return false;
	}

	public static PriorityQueue<QueenWithWeight> moveQueens(final Queen[] queens) {
		PriorityQueue<QueenWithWeight> queue = new PriorityQueue<>(queens.length * (queens.length - 1));

		for (int j = 0; j < queens.length; j++) {
			for (int i = 0; i < queens.length; i++) {
				Queen existing = queens[j];
				if (i != existing.x) {
					Queen[] tmp = new Queen[queens.length];
					for (int k = 0; k < queens.length; k++) {
						tmp[k] = queens[k];
					}
					tmp[j] = new Queen(i, j);
					int numCoflict = countConflicts(tmp);
					QueenWithWeight qww = new QueenWithWeight(i, j, numCoflict);
					queue.add(qww);
				}
			}
		}
		System.out.println("GENERATED PERMUTATION");
		return queue;
	}

	public static void printQueens(final Queen[] queens) {
		for (int i = 0; i < queens.length; i++) {
			for (int j = 0; j < queens.length; j++) {
				if (contains(new Queen(i, j), queens)) {
					System.out.print("* ");
				} else {
					System.out.print("_ ");
				}
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		int n;
		try (Scanner reader = new Scanner(System.in)) {
			System.out.println("Enter a number of queens: ");
			n = reader.nextInt();
		}

		Queen[] queens = generateChessBoard(n);

		while (countConflicts(queens) > 0) {
			PriorityQueue<QueenWithWeight> queue = moveQueens(queens);
			QueenWithWeight qww = queue.poll();
			queens[qww.y] = new Queen(qww.x, qww.y);
			System.out.println("QUEEN MOVED: " + queens[qww.y]);
		}
		printQueens(queens);

	}
}