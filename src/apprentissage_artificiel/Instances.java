package apprentissage_artificiel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Instances {

	public final static String INIT = "";
	
	private String relationName;
	private LinkedHashMap<String, ArrayList<String>> attributes;
	ArrayList<Instance> instances;
	
	public Instances() {
		this.relationName = INIT;
		this.attributes = new LinkedHashMap<String, ArrayList<String>>();
		this.instances = new ArrayList<Instance>();
	}
	
	public void loadFile(String filePath) {
		Reader reader = new Reader(filePath, this);
		reader.read();
	}
	
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	
	public String getRelationName() {
		return relationName;
	}
	
	public void setAttributes(LinkedHashMap<String, ArrayList<String>> attributes) {
		this.attributes = attributes;
	}
	
	public LinkedHashMap<String, ArrayList<String>> getAttributes() {
		return attributes;
	}
	
	public void addAttribute(String name, ArrayList<String> values) {
		attributes.put(name, values);
	}
	
	public void setInstances(ArrayList<Instance> instances) {
		this.instances = instances;
	}
	
	public ArrayList<Instance> getInstances() {
		return instances;
	}
	
	public void addInstance(Instance instance) {
		this.instances.add(instance);
	}
}
