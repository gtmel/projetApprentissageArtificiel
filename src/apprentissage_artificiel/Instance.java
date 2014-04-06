package apprentissage_artificiel;

import java.util.ArrayList;

public class Instance {
	
	private ArrayList<Attribute> attributes;
	private InstanceClass instanceClass;
	
	public Instance() {
		this.attributes = new ArrayList<Attribute>();
		this.instanceClass = null;
	}

	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<Attribute> attributes) {
		this.attributes = attributes;
	}

	public void addAttribute(Attribute attribute) {
		this.attributes.add(attribute);
	}
	
	public InstanceClass getInstanceClass() {
		return instanceClass;
	}

	public void setInstanceClass(InstanceClass instanceClass) {
		this.instanceClass = instanceClass;
	}
	
}
