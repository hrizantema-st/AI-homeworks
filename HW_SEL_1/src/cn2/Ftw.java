package cn2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Ftw {
	public static void main(String[] args) {
		List<String> a = Arrays.asList("a", "b");
		List<String> b = Arrays.asList("c", "d");
		List<List<String>> source = Arrays.asList(a, b);
		List<List<String>> dest = deepCopyOfData(source);
		for (int i = 0; i < source.size(); i++) {
			dest.add(source.get(i));
		}
		dest.get(0).set(0, "lala");
		System.out.println(source.get(0).get(0));
		System.out.println(0/10);

	}

	public static List<List<String>> deepCopyOfData(final List<List<String>> source) {
		List<List<String>> resultSet = new ArrayList<>();
		for (int i = 0; i < source.size(); i++) {
			List<String> currentEl = source.get(i);
			List<String> coppiedEl = new ArrayList<>();
			for (int j = 0; j < currentEl.size(); j++) {
				coppiedEl.add(new String(currentEl.get(j)));
			}
			resultSet.add(coppiedEl);
		}
		return resultSet;
	}
}
