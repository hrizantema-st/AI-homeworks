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
		str.append("feature: " + this.name);
		//str.append("; column: " + this.columnPosition + "; ");
		str.append("   values: ");
		for (int i = 0; i < this.values.size() ; i++) {
			str.append(this.values.get(i).toString());
			str.append(", ");
		}
		//str.append(this.values.get(this.values.size() - 1));
		return str.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.columnPosition;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + ((this.values == null) ? 0 : this.values.hashCode());
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
		Feature other = (Feature) obj;
		if (this.columnPosition != other.columnPosition)
			return false;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		if (this.values == null) {
			if (other.values != null)
				return false;
		} else if (!this.values.equals(other.values))
			return false;
		return true;
	}
	

}
