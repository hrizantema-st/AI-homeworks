package cn2;

import java.util.List;

public class Complex {
	private List<Selector> attributes;
	
	public Complex(List<Selector> attributes) {
		super();
		this.attributes = attributes;
	}

	public boolean doesComplexCoverExample(final List<String> data) {
		for(Selector s: this.attributes) {
			if(!s.getValue().equals(data.get(s.getAttributeIndex()))) {
				return false;
			}
		}
		return true;
	}
}
