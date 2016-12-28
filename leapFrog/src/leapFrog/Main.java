package leapFrog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

@SuppressWarnings("nls")
public class Main {
	/**
	 * hasMap of arrayLists cause wtf HashMap of String[] doesn't work
	 */
	private static HashMap<List<String>, List<String>> pathMap = new HashMap<>();
	private static List<String[]> visited = new ArrayList<>();

	private static boolean isVisited(String[] node) {
		return visited.contains(node);
	}

	private static void printState(List<String> node) {
		for (int i = 0; i < node.size(); i++) {
			System.out.print(node.get(i));
		}
	}

	private static boolean isTarget(String[] node, int n) {
		for (int i = 0; i < n / 2; i++) {
			if (node[i] != "<") {
				return false;
			}
		}
		if (node[n / 2] != "_") {
			return false;
		}
		for (int i = n / 2 + 1; i < n; i++) {
			if (node[i] != ">") {
				return false;
			}
		}
		return true;
	}

	private static boolean isRootNode(List<String> currentParent) {
		return currentParent.equals(Arrays.asList("ROOT"));
	}

	private static int findPositionOfEmpty(String[] state) {
		for (int i = 0; i < state.length; i++) {
			if (state[i] == "_") {
				return i;
			}
		}
		return -1;
	}

	public static String[] copyState(final String[] state, final int n) {
		String[] tmp = new String[state.length];
		for (int i = 0; i < state.length; i++) {
			tmp[i] = state[i];
		}
		return tmp;
	}

	public static List<String[]> generateChildren(String[] state) {
		List<String[]> childs = new ArrayList<>();
		int empty = findPositionOfEmpty(state);

		if (empty + 1 < state.length) {
			if (state[empty + 1] == "<") {
				String[] tmp = copyState(state, state.length);
				String a = tmp[empty];
				tmp[empty] = tmp[empty + 1];
				tmp[empty + 1] = a;
				childs.add(tmp);
			}
		}
		if (empty + 2 < state.length) {
			if (state[empty + 2] == "<") {
				String[] tmp = copyState(state, state.length);
				String a = tmp[empty];
				tmp[empty] = tmp[empty + 2];
				tmp[empty + 2] = a;
				childs.add(tmp);
			}
		}
		if (empty - 1 >= 0) {
			if (state[empty - 1] == ">") {
				String[] tmp = copyState(state, state.length);
				String a = tmp[empty];
				tmp[empty] = tmp[empty - 1];
				tmp[empty - 1] = a;
				childs.add(tmp);
			}
		}
		if (empty - 2 >= 0) {
			if (state[empty - 2] == ">") {
				String[] tmp = copyState(state, state.length);
				String a = tmp[empty];
				tmp[empty] = tmp[empty - 2];
				tmp[empty - 2] = a;
				childs.add(tmp);
			}
		}

		return childs;
	}

	/**
	 * DFS that put pair(child, parent) into hashMap, which later is going to be
	 * used to find the path from root to target node
	 * 
	 * @param node
	 *            - the node from which the dfs algorithm to start
	 */
	public static void dfs(String[] node) {
		Stack<String[]> stack = new Stack<>();
		stack.add(node);
		visited.add(node);
		pathMap.put(Collections.unmodifiableList(Arrays.asList(node)),
				Collections.unmodifiableList(Arrays.asList("ROOT")));

		while (!stack.isEmpty()) {
			String[] element = stack.pop();

			if (isTarget(element, element.length)) {
				break;
			}
			List<String[]> neighbours = generateChildren(element);
			for (int i = 0; i < neighbours.size(); i++) {
				String[] child = neighbours.get(i);
				if (!isVisited(child)) {
					stack.add(child);
					visited.add(child);
					pathMap.put(Collections.unmodifiableList(Arrays.asList(child)),
							Collections.unmodifiableList(Arrays.asList(element)));
				}
			}
		}
	}

	public static void main(String[] args) {
		int k;
		try (Scanner reader = new Scanner(System.in)) {
			System.out.println("Enter value for k: ");
			k = reader.nextInt();
		}
		/*
		 * This part is responsible for initializing the initial and final state
		 */
		String[] initialState = new String[2 * k + 1];
		String[] target = new String[2 * k + 1];
		for (int i = 0; i < k; i++) {
			initialState[i] = ">";
			target[i] = "<";
		}
		initialState[k] = "_";
		target[k] = "_";
		for (int i = k + 1; i < 2 * k + 1; i++) {
			initialState[i] = "<";
			target[i] = ">";
		}

		dfs(initialState);

		/*
		 * This part is responsible for printing the result
		 */
		Stack<List<String>> stack = new Stack<>();
		List<String> currentParent = Arrays.asList(target);
		while (!isRootNode(currentParent)) {
			stack.push(currentParent);
			List<String> tmp = pathMap.get(currentParent);
			currentParent = tmp;
		}
		while (!stack.isEmpty()) {
			List<String> node = stack.pop();
			printState(node);
			System.out.println();
		}
	}

}
