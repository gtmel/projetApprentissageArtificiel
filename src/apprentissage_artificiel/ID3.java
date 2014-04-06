package apprentissage_artificiel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class ID3 {	 

	public final static String INIT = "";
	public final static int INIT_I = 0;
	public final static String LEAF = "LEAF";
	public final static int LEAF_I = -1;
	public final static String ERROR = "ERROR";
	public final static int ERROR_I = -2;

	private Attribute attribute;
	private HashMap<String, ID3> sons;
	
	public ID3() {
		sons = new HashMap<String, ID3>();
	}

	/**
	 * Fonction permettant d'initialiser la fonction récursive.
	 * @param instances Instances sur laquelle s'effectue le traitement.
	 * @return Retourne une instance de la classe ID3 contenant l'arbre de décision.
	 */
	public ID3 compute(Instances instances, int maxDepth, int errorRate) {
		ArrayList<Integer> attributes = new ArrayList<Integer>();
		for (int i = 0; i < instances.getAttributes().size() - 1; i++) {
			attributes.add(i);
		}
		return recursive(instances, attributes, 0, maxDepth, errorRate);
	}
	
	/**
	 * Fonction récursive principale permettant la construction de l'arbre de décision.
	 * @param instances Instances sur laquelle s'effectue le traitement.
	 * @param attributes Liste des index des attributs non traités.
	 * @return Retourne un noeud (une instance de la classe ID3).
	 */
	public ID3 recursive(Instances instances, ArrayList<Integer> attributes, int depth, int maxDepth, int errorRate) {
		if (instances.getInstances().size() == 0) { /* Nœud terminal */
			/* Retourner un noeuf erreur */
			ID3 newId3 = new ID3();
			Attribute attTemp = new Attribute(ERROR, INIT, ERROR_I, 0, 0);
			newId3.setAttribute(attTemp);
			return newId3;
		} else if (attributes.size() == 0) { /* Nœud terminal */
			/* Retourner un nœud ayant la valeur de classe la plus représentée */
			HashMap<String, Integer> nbInstanceClass = new HashMap<String, Integer>();
			for (Instance instance : instances.getInstances()) {
				if (!nbInstanceClass.containsKey(instance.getInstanceClass().getValue())) {
					nbInstanceClass.put(instance.getInstanceClass().getValue(), 1);
				} else {
					int toModify = nbInstanceClass.get(instance.getInstanceClass().getValue());
					toModify++;
					nbInstanceClass.put(instance.getInstanceClass().getValue(), toModify);
				}
			}
			String topClass = INIT;
			int nbTopClass = INIT_I;
			for (Entry<String, Integer> entry : nbInstanceClass.entrySet()) {
				if (entry.getValue() > nbTopClass) {
					topClass = entry.getKey();
					nbTopClass = entry.getValue();
				}
			}
			ID3 newId3 = new ID3();
			Attribute attTemp = new Attribute(LEAF, topClass, LEAF_I, 0, 0);
			newId3.setAttribute(attTemp);
			return newId3;
		} else {
			HashMap<String, Integer> instanceClassValues = new HashMap<String, Integer>();
			for (Instance instance : instances.getInstances()) {	
				if (instanceClassValues.containsKey(instance.getInstanceClass().getValue())) {
					int toModify = instanceClassValues.get(instance.getInstanceClass().getValue());
					toModify++;
					instanceClassValues.put(instance.getInstanceClass().getValue(), toModify);
				} else {
					instanceClassValues.put(instance.getInstanceClass().getValue(), 1);
				}
			}
			if (instanceClassValues.size() == 1) { /* Une seule valeur de classe représentée */
				/* Retourner un noeud ayant cette valeur */
				ID3 newId3 = new ID3();
				Attribute attTemp = new Attribute(LEAF, instanceClassValues.keySet().toArray()[0].toString(), LEAF_I, 0, 0);
				newId3.setAttribute(attTemp);
				return newId3;
			} else { /* Plusieurs valeurs de classe représentées */
				
				if (depth == maxDepth && depth > 0) { /* Si la profondeur maximale est atteinte */
					String topClass = INIT;
					int nbTopClass = INIT_I;
					int nbInstance = INIT_I;
					for (Entry<String, Integer> entry : instanceClassValues.entrySet()) {
						nbInstance += entry.getValue();
						if (entry.getValue() > nbTopClass) {
							topClass = entry.getKey();
							nbTopClass = entry.getValue();
						}
					}
					ID3 newId3 = new ID3();
					Attribute attTemp = new Attribute(LEAF, topClass, LEAF_I, nbInstance - nbTopClass, nbInstance);
					newId3.setAttribute(attTemp);
					return newId3;
				} else {
					
					/* On teste si une valeur de classe représente plus du taux admissible d'exemples */
					if (errorRate > 0 && depth > 0) {
						String topClass = INIT;
						int nbErrorExample = INIT_I;
						int nbInstance = INIT_I;
						for (Entry<String, Integer> entry : instanceClassValues.entrySet()) {
							nbInstance += entry.getValue();
						}
						for (Entry<String, Integer> entry : instanceClassValues.entrySet()) {
							double rate = ((double)entry.getValue() / (double)nbInstance) * 100;
							if (rate >= errorRate) {
								topClass = entry.getKey();
								nbErrorExample = nbInstance - entry.getValue();
							}
						}
						if (!INIT.equals(topClass)) { /* Une valeur satisfait la condition */
							ID3 newId3 = new ID3();
							Attribute attTemp = new Attribute(LEAF, topClass, LEAF_I, nbErrorExample, nbInstance);
							newId3.setAttribute(attTemp);
							return newId3;
						}	
					}
					
					/* selectedAttribute = attribut maximisant le gain d'information parmi les attributs restants */
					Attribute selectedAttribute = bestAttribute(instances, attributes);
					/* remainingAttributes = attributes - {selectedAttribute} */
					ArrayList<Integer> remainingAttributes = new ArrayList<Integer>(attributes);
					int i = 0;
					while (attributes.get(i) != selectedAttribute.getIndex()) {
						i++;
					}
					remainingAttributes.remove(i);
					/* newId3 = nœud étiqueté avec selectedAttribute */
					ID3 newId3 = new ID3();
					newId3.setAttribute(selectedAttribute);
					/* Création d'un fils pour chaque valeur possible de selectedAttribute */
			        /* newId3->addSon(attributeValue, ID3(filterInstance, selectedAttribute, attributeValue), remainingAttributes) */
					for (String attributeValue : instances.getAttributes().get(selectedAttribute.getName())) {
						newId3.addSon(attributeValue, recursive(filterInstance(instances, selectedAttribute, attributeValue), remainingAttributes, depth + 1, maxDepth, errorRate));
					}
					
					/* Retourne le nouveau noeud */
					return newId3;	
				}
			}
		}
	}

	/**
	 * Fonction permettant de chercher le meilleur attribut (celui qui obtient le meilleur gain).
	 * @param instances Instances sur laquelle s'effectue le traitement.
	 * @param attributes Liste des index d'attribut non traités.
	 * @return Retourne le meilleur attribut.
	 */
	public Attribute bestAttribute(Instances instances, ArrayList<Integer> attributes) {	
		double topGain = 0;
		Attribute topAttribute = null;
		
		HashMap<String, Integer> examplesPerClass = new HashMap<String, Integer>();
		for (Instance instance : instances.getInstances()) {
			String value = instance.getInstanceClass().getValue();
			if (!examplesPerClass.containsKey(value)) {
				examplesPerClass.put(value, 1);
			} else {
				Integer temp = examplesPerClass.get(value);
				temp++;
				examplesPerClass.put(value, temp);
			}
		}

		// Calcul de l'entropie de l'instances
		ArrayList<Integer> values = new ArrayList<Integer>();
		for (Entry<String, Integer> entry : examplesPerClass.entrySet()) {
			values.add(entry.getValue());
		}
		double entropyS = calculateEntropy(instances.getInstances().size(),values);
		 
		for (Integer i : attributes) {
			HashMap<String, HashMap<String, Integer>> examplesPerClassPerAttributs = new HashMap<String, HashMap<String, Integer>>();
			for (Instance instance : instances.getInstances()) {
				if (!examplesPerClassPerAttributs.containsKey(instance.getAttributes().get(i).getValue())) {
					HashMap<String, Integer> newEntry = new HashMap<String, Integer>();
					newEntry.put(instance.getInstanceClass().getValue(), 1);
					examplesPerClassPerAttributs.put(instance.getAttributes().get(i).getValue(), newEntry);
				} else {
					HashMap<String, Integer> test = examplesPerClassPerAttributs.get(instance.getAttributes().get(i).getValue());
					if (!test.containsKey(instance.getInstanceClass().getValue())) {
						test.put(instance.getInstanceClass().getValue(), 1);
					} else {
						int toModify = test.get(instance.getInstanceClass().getValue());
						toModify++;
						test.put(instance.getInstanceClass().getValue(), toModify);
						examplesPerClassPerAttributs.put(instance.getAttributes().get(i).getValue(), test);
					}
				}
			}
			double gain = entropyS;
			for (Entry<String, HashMap<String, Integer>> entry : examplesPerClassPerAttributs.entrySet()) {
				ArrayList<Integer> val = new ArrayList<Integer>();
				int ratio = 0;
				for (Entry<String, Integer> value : entry.getValue().entrySet()) {
					ratio += value.getValue();
					val.add(value.getValue());
				}
				gain -= ((double) ratio / (double) instances.getInstances().size()) * calculateEntropy(ratio, val);
				val.clear();
			}
			if (gain > topGain) {
				topGain = gain;
				topAttribute = instances.getInstances().get(0).getAttributes().get(i);
			}
		}
		return topAttribute;
	}
	
	/**
	 * Fonction permettant de filtrer les exemples en fonction de la valeur d'un attribut.
	 * @param instances Instances sur laquelle s'effectue le traitement.
	 * @param attribute Attribut qui doit-être filtré.
	 * @param value Valeur de l'attribut qui doit-être conservée.
	 * @return Retourne une nouvelle instances.
	 */
	public Instances filterInstance(Instances instances, Attribute attribute, String value) {
		Instances newInstances = new Instances();
		newInstances.setRelationName(instances.getRelationName());
		newInstances.setAttributes(instances.getAttributes());
		ArrayList<Instance> arrayInstance = new ArrayList<Instance>();
		for (int i = 0; i < instances.getInstances().size(); i++) {
			if (instances.getInstances().get(i).getAttributes().get(attribute.getIndex()).getValue().equals(value)) {
				Instance newInstance = new Instance();
				for (Attribute att : instances.getInstances().get(i).getAttributes()) {
					newInstance.addAttribute(new Attribute(att.getName(), att.getValue(), att.getIndex(), 0, 0));
				}
				InstanceClass newInstanceClass = new InstanceClass(instances.getInstances().get(i).getInstanceClass().getValue());
				newInstance.setInstanceClass(newInstanceClass);
				arrayInstance.add(newInstance);
			}
		}
		newInstances.setInstances(arrayInstance);		
		return newInstances;
	}

	/**
	 * Fonction permettant de calculer l'entropie.
	 * @param nbExamples Entier correspondant au nombre d'exemples.
	 * @param values Liste des entiers à traiter.
	 * @return Retourne la valeur de l'entropie.
	 */
	private double calculateEntropy(int nbExamples, ArrayList<Integer> values) {
		double entropy = 0;
		for (Integer value : values) {
			double entropyPart = (double) value / (double) nbExamples;
			entropy += -(entropyPart * log2(entropyPart));
		}
		return entropy;
	}

	private double log2(double a) {
		return logb(a, 2);
	}

	private double logb(double a, double b) {
		return Math.log(a) / Math.log(b);
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public HashMap<String, ID3> getSons() {
		return sons;
	}

	public void setSons(HashMap<String, ID3> sons) {
		this.sons = sons;
	}

	public void addSon(String value, ID3 id3) {
		sons.put(value, id3);
	}
	
	/**
	 * Méthode permettant d'afficher l'arbre de décision.
	 * @param inc Entier représentant le niveau d'imbrication.
	 * @return Retourne l'arbre de décision sous forme de chaîne de caractères.
	 */
	public String display(int inc) {
		
		String display = INIT;
		
		if (sons.size() > 0) {
			for (Entry<String, ID3> entry : sons.entrySet()) {
				for (int i = 0; i < inc; i++) {
					display += "|   ";
				}
				display += attribute.getName() + " = " + entry.getKey();
				if (!LEAF.equals(entry.getValue().getAttribute().getName())) {
					display += "\n";
					display += entry.getValue().display(inc + 1);
				} else {
					display += entry.getValue().display(inc + 1);
					display += "\n";
				}
			}
		}
		if (LEAF.equals(attribute.getName())) {
			if (attribute.getNbErrors() > 0) {
				display += ": " + attribute.getValue() + " (" + attribute.getNbErrors() + " errors / " + attribute.getNbInstance() + " examples)";
			} else {
				display += ": " + attribute.getValue();
			}
			
		}
		
		return display;
	}

}
