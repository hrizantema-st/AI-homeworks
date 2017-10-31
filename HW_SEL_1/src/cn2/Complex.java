package cn2;

import java.util.ArrayList;
import java.util.List;

public class Complex {
	private List<Selector> attributes;

	public Complex() {
		super();
		this.attributes = new ArrayList<>();
	}
	public Complex(List<Selector> attributes) {
		super();
		this.attributes = attributes;
	}

	public boolean doesComplexCoverExample(final List<String> data) {
		if (this.attributes == null)
			return false;
		// WTF
		// or return true because :the empty complex (a conjunct of zero
		// attribute tests) covers all examples and
		// the empty cover (a disjunct of zero complexes) covers no examples
		for (Selector s : this.attributes) {
			if (!s.getValue().equals(data.get(s.getAttributeIndex()))) {
				return false;
			}
		}
		return true;
	}

	public List<Selector> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(List<Selector> attributes) {
		this.attributes = attributes;
	}

	public void addSelector(final Selector selector) {
		this.attributes.add(selector);
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		if(this.attributes.isEmpty()) {
			return "∅ ";
		}
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < this.attributes.size() - 1; i++) {
			str.append(this.attributes.get(i).toString());
			str.append(" & ");
		}
		str.append(this.attributes.get(this.attributes.size() - 1));
		return str.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
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
		Complex other = (Complex) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		return true;
	}
	
	

}
