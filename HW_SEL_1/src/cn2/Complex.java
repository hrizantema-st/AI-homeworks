package cn2;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is representing a complex for the CN2 rule based classifier. A
 * complex consist of list of selectors.
 * 
 * @author hstancheva
 *
 */
public class Complex {
	private List<Selector> attributes;

	public Complex() {
		super();
		this.attributes = new ArrayList<>();
	}

	public Complex(final List<Selector> attributes) {
		super();
		this.attributes = attributes;
	}

	/**
	 * This method is responsible for finding if a complex covers a given data
	 * instance. A complex covers a data instance if all of its selectors have
	 * the same value in the given data.
	 * 
	 * @param data
	 * @return true is the complex covers the given data or false otherwise
	 */
	public boolean doesComplexCoverExample(final List<String> data) {
		if (this.attributes == null)
			return true;
		// the empty complex (a conjunct of zero attribute tests) covers all examples
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

	public void setAttributes(final List<Selector> attributes) {
		this.attributes = attributes;
	}

	public void addSelector(final Selector selector) {
		this.attributes.add(selector);
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		if (this.attributes.isEmpty()) {
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
		result = prime * result + ((this.attributes == null) ? 0 : this.attributes.hashCode());
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
		if (this.attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!this.attributes.equals(other.attributes))
			return false;
		return true;
	}

}
