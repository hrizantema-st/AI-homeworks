package cn2;

public class Selector {
	private String attribute;
	private int attributeIndex;
	private String value;

	public Selector(final String attribute, final int attributeIndex, final String value) {
		super();
		this.attribute = attribute;
		this.attributeIndex = attributeIndex;
		this.value = value;
	}

	public String getAttribute() {
		return this.attribute;
	}
	
	public int getAttributeIndex() {
		return this.attributeIndex;
	}

	public String getValue() {
		return this.value;
	}

	public void setAttribute(final String attribute) {
		this.attribute = attribute;
	}
	
	public void setAttributeIndex(final int attributeIndex) {
		this.attributeIndex = attributeIndex;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return this.attribute + "=" + this.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.attribute == null) ? 0 : this.attribute.hashCode());
		result = prime * result + this.attributeIndex;
		result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
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
		Selector other = (Selector) obj;
		if (this.attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!this.attribute.equals(other.attribute))
			return false;
		if (this.attributeIndex != other.attributeIndex)
			return false;
		if (this.value == null) {
			if (other.value != null)
				return false;
		} else if (!this.value.equals(other.value))
			return false;
		return true;
	}
	
	
}
