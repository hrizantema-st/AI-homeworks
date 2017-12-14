package random_forest;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent a node in a decision tree
 * @author hstancheva
 *
 */
public class Node {
	/**
	 * The name of the Node
	 * if it's Leaf it will be class value 
	 * otherwise an attribute name
	 */
	private String label;
	private Feature feature;
	private List<Node> children = new ArrayList<>();
	private List<String> branchValues = new ArrayList<>();
	/**
	 * Boolean Value to determine if current node is a leaf or intermediate node
	 */
	private boolean leaf;

	public Node() {
		super();
	}

	public Node(final String classValue, final boolean isLeaf) {
		this.label = classValue;
		this.leaf = isLeaf;
		this.feature = null;
	}

	public Node(final Feature feature) {
		this.label = feature.getName();
		this.feature = feature;
		this.leaf = false;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLeaf(final boolean leaf) {
		this.leaf = leaf;
	}

	public boolean isLeaf() {
		return this.leaf;
	}

	public void addChild(final Node child) {
		this.children.add(child);
	}
	
	public void addBranchValue(final String val) {
		this.branchValues.add(val);
	}
	
	public List<String> getBranchValues() {
		return this.branchValues;
	}

	public List<Node> getChildren() {
		return this.children;
	}

	public Feature getFeature() {
		return this.feature;
	}

	public void setFeature(Feature feature) {
		this.label = feature.getName();
		this.feature = feature;
		this.leaf = false;
	}
	

	public void print() {
		System.out.println("Label: " + this.label);
		/*System.out.println("Feature:     ");
		System.out.println(this.feature);
		System.out.println("-------------");*/
	}
}
