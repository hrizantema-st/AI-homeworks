import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

@SuppressWarnings("nls")
public class Main {
	/**
	 * This map keeps the correct places(two dimensions as value) of a number(as
	 * key) on the board
	 */
	private static Map<Integer, int[]> correctPlaces = new HashMap<>();

	static int[][] delta = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };

	private static HashMap<State, State> pathMap = new HashMap<>();
	private static List<int[][]> visited = new ArrayList<>();

	private static boolean isVisited(State node) {
		return visited.contains(node);
	}

	/**
	 * This method checks if the matrix given as first parameter is equals to
	 * the matrix given as second argument
	 * 
	 * @param currentState
	 * @param targetState
	 * @return
	 */
	private static boolean isTarget(final int[][] currentState, final int[][] targetState) {
		for (int i = 0; i < currentState.length; i++) {
			for (int j = 0; j < currentState.length; j++) {
				if (currentState[i][j] != targetState[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	private static int[][] copyBoard(final int[][] board) {
		int length = board.length;
		int[][] newBoard = new int[length][length];
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				newBoard[i][j] = board[i][j];
			}
		}
		return newBoard;
	}

	@SuppressWarnings("unused")
	private static void printBoard(final int[][] board) {
		int length = board.length;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				System.out.print(board[i][j] + "   ");
			}
			System.out.println();
		}
	}

	/**
	 * This method calculates the heuristic distance from the
	 * argument(currentState) to the final state, using the hashmap
	 * correctPlaces
	 * 
	 * @param currentState
	 *            - the state for which to calculate the heuristic
	 * @return heuristic distance
	 */
	@SuppressWarnings("boxing")
	public static int heuristicDistance(final int[][] currentState) {
		int heuristic = 0;
		for (int i = 0; i < currentState.length; i++) {
			for (int j = 0; j < currentState.length; j++) {

				int currentValue = currentState[i][j];
				int[] correctPlace = correctPlaces.get(currentValue);
				heuristic += Math.abs(correctPlace[0] - i) + Math.abs(correctPlace[1] - j);
			}
		}
		return heuristic;
	}

	/**
	 * This method is responsible for performing A* algorithm
	 * 
	 * @param initialState
	 *            - the state from which to start our search
	 * @param finalState
	 *            - the state which we wanto to reach to
	 */
	public static void aAsterics(int[][] initialState, int[][] finalState) {
		PriorityQueue<State> pq = new PriorityQueue<>();
		State firstState = new State(0, 0, initialState);
		pq.add(firstState);
		while (!pq.isEmpty()) {
			State curr = pq.poll();
			if (isVisited(curr)) {
				continue;
			}
			if (isTarget(curr.board, finalState)) {
				break;
			}
			visited.add(curr.board);
			int boardLength = curr.board.length;
			for (int i = 0; i < delta.length; i++) {
				int newX = curr.x + delta[i][0];
				int newY = curr.y + delta[i][1];
				if (newX < 0 || newX >= boardLength || newY < 0 || newY >= boardLength) {
					continue;
				}
				int[][] newBoard = copyBoard(curr.board);
				int tmp = newBoard[newX][newY];
				newBoard[newX][newY] = 0;
				newBoard[curr.x][curr.y] = tmp;

				int newHeuristic = heuristicDistance(newBoard);
				int newPrice = curr.price + 1;
				State newState = new State(newPrice, newHeuristic, newBoard);
				if (isVisited(newState)) {
					continue;
				}
				pathMap.put(newState, curr);
				pq.add(newState);
			}
		}
	}

	/**
	 * this method return which move the blank tile should perform in order to
	 * move from "from" state to "to" state
	 * 
	 * @param from
	 *            initial state
	 * @param to
	 *            goal state
	 * @return
	 */
	private static String returnMove(final State from, final State to) {
		if ((from.x - to.x) == -1) {
			return "down";
		} else if ((from.x - to.x) == 1) {
			return "up";
		} else if ((from.y - to.y) == -1) {
			return "right";
		} else if ((from.y - to.y) == 1) {
			return "left";
		} else
			return "WTF";
	}

	@SuppressWarnings({ "boxing" })
	public static void main(String[] args) {
		try (Scanner reader = new Scanner(System.in)) {
			System.out.println("Enter a number of blocks: ");
			int numberOfTiles = reader.nextInt();
			double sqrt = Math.sqrt(numberOfTiles + 1);
			int x = (int) sqrt;
			int[][] initialState = new int[x][x];
			int[][] finalState = new int[x][x];
			if (Math.pow(sqrt, 2) == Math.pow(x, 2)) {

				for (int i = 0; i < x; i++) {
					for (int j = 0; j < x; j++) {
						int number = reader.nextInt();
						initialState[i][j] = number;
						finalState[i][j] = i * x + j + 1;
						correctPlaces.put(i * x + j + 1, new int[] { i, j });
					}
				}
				correctPlaces.remove(x * x);
				correctPlaces.put(0, new int[] { x - 1, x - 1 });
				finalState[x - 1][x - 1] = 0;
			} else {
				System.out.println("Incorrect input");
			}

			aAsterics(initialState, finalState);

			/*
			 * This part is responsible for printing the result
			 */
			State finalStateAsState = null;

			for (Map.Entry<State, State> entry : pathMap.entrySet()) {
				if (Arrays.deepEquals(entry.getKey().board, finalState)) {
					finalStateAsState = entry.getKey();
				}

			}
			Stack<String> stackOfMoves = new Stack<>();
			System.out.println(finalStateAsState.price);
			State currentParent = finalStateAsState;
			while (!isTarget(currentParent.board, initialState)) {
				State tmp = pathMap.get(currentParent);
				stackOfMoves.push(returnMove(tmp, currentParent));
				currentParent = tmp;
			}
			while (!stackOfMoves.isEmpty()) {
				String move = stackOfMoves.pop();
				System.out.println(move);
			}
		}
	}

}
