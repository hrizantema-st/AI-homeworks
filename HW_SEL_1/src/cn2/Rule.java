package cn2;

/**
 * This class is representing a rule for the CN2 rule based classifier. A rule
 * consist of a complex and a corresponding class name.
 * 
 * @author hstancheva
 *
 */
public class Rule {
	private Complex attributes;
	private String className;

	public Rule(final Complex attributes, final String className) {
		super();
		this.attributes = attributes;
		this.className = className;
	}

	public Complex getAttributes() {
		return this.attributes;
	}

	public String getClassName() {
		return this.className;
	}

	public void setAttributes(Complex attributes) {
		this.attributes = attributes;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public String toString() {
		return this.attributes.toString() + "  =>  " + this.className;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.attributes == null) ? 0 : this.attributes.hashCode());
		result = prime * result + ((this.className == null) ? 0 : this.className.hashCode());
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
		Rule other = (Rule) obj;
		if (this.attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!this.attributes.equals(other.attributes))
			return false;
		if (this.className == null) {
			if (other.className != null)
				return false;
		} else if (!this.className.equals(other.className))
			return false;
		return true;
	}

}
