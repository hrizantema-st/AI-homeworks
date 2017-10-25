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
		return "Selector [attribute=" + attribute + ", value=" + value + "]";
	}
}
