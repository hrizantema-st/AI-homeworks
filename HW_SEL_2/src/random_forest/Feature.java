package random_forest;

import java.util.List;

public class Feature {
	private String name;
	private List<String> values;
	private int columnPosition;

	public Feature(final String name, final List<String> values, final int columnPosition) {
		super();
		this.name = name;
		this.values = values;
		this.columnPosition = columnPosition;
	}

	public String getName() {
		return this.name;
	}

	public List<String> getValues() {
		return this.values;
	}

	public int getColumnPosition() {
		return this.columnPosition;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setValues(final List<String> values) {
		this.values = values;
	}

	public void setColumnPosition(final int columnPosition) {
		this.columnPosition = columnPosition;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("name: " + this.name);
		str.append("; column: " + this.columnPosition + "; ");
		for (int i = 0; i < this.values.size() ; i++) {
			str.append(this.values.get(i).toString());
			str.append(", ");
		}
		//str.append(this.values.get(this.values.size() - 1));
		return str.toString();
	}

}
