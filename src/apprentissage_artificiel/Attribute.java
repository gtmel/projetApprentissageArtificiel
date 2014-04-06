package apprentissage_artificiel;

public class Attribute {
	
	private String name;
	private String value;
	private int index;
	private int nbErrors;
	private int nbInstance;
	
	public Attribute(String name, String value, int index, int nbErrors, int nbInstance) {
		this.name = name;
		this.value = value;
		this.index = index;
		this.nbErrors = nbErrors;
		this.nbInstance = nbInstance;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getNbErrors() {
		return nbErrors;
	}

	public void setNbErrors(int nbErrors) {
		this.nbErrors = nbErrors;
	}

	public int getNbInstance() {
		return nbInstance;
	}

	public void setNbInstance(int nbInstance) {
		this.nbInstance = nbInstance;
	}
	
}
