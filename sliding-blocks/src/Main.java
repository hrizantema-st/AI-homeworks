import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

@SuppressWarnings("nls")
public class Main {
	private static Map<Integer, int[]> correctPlaces = new HashMap<>();

	static int[][] delta = { { -1, 0 }, { 0, 1 }, { 1, 0 },
			{ 0, -1 }/* , { -1, -1 }, { -1, 1 }, { 1, 1 }, { 1, -1 } */ };

	private static HashMap<State, State> pathMap = new HashMap<>();
	private static List<State> visited = new ArrayList<>();

	private static boolean isVisited(State node) {
		return visited.contains(node);
	}

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

	
	private static void printBoard(final int[][] board) {
		int length = board.length;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				System.out.print(board[i][j] + "   ");
			}
			System.out.println();
		}
	}

	public static void aAsterics(int[][] initialState, int[][] finalState) {
		PriorityQueue<State> pq = new PriorityQueue<>();
		pq.add(new State(0, 0, initialState));
		while (!pq.isEmpty()) {
			State curr = pq.poll();
			if (isVisited(curr)) {
				continue;
			}
			if (isTarget(curr.board, finalState)) {
				break;
			}
			visited.add(curr);
			int boardLength = curr.board.length; // size
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
				System.out.println("HEURISTIC: " + newHeuristic);
				System.out.println("PRICE: " + newPrice);
				printBoard(newBoard);
				System.out.println("---------------");
				State newState = new State(newPrice, newHeuristic, newBoard);
				if (isVisited(newState)) {
					continue;
				}
				pathMap.put(curr, newState);
				pq.add(newState);
			}

		}

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

			printBoard(initialState);
			System.out.println("-----------");
			printBoard(finalState);
			System.out.println("-----------");
			
			aAsterics(initialState, finalState);
			for (Entry<Integer, int[]> entry : correctPlaces.entrySet()) {
				System.out.println(entry.getKey() + ":[" + entry.getValue()[0] + " ," + entry.getValue()[1] + "]");
			}
			/*
			 * This part is responsible for printing the result
			 */
			/*
			 * State finalStateAsState = null; Stack<State> stack = new
			 * Stack<>(); for (Map.Entry<State, State> entry :
			 * pathMap.entrySet()) { printBoard(entry.getKey().board);
			 * System.out.println("------"); if
			 * (Arrays.deepEquals(entry.getKey().board,finalState)) {
			 * finalStateAsState = entry.getValue(); System.out.println("OK"); }
			 * }
			 * 
			 * State currentParent = finalStateAsState;
			 * System.out.println(currentParent); while
			 * (!isTarget(currentParent.board, initialState)) {
			 * stack.push(currentParent); State tmp =
			 * pathMap.get(currentParent); currentParent = tmp; } while
			 * (!stack.isEmpty()) { State node = stack.pop();
			 * printBoard(node.board); System.out.println(); }
			 */

		}
	}

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
}
