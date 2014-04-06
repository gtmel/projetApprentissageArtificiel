package apprentissage_artificiel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Reader {

	public final static String INIT = "";
	
	public final static String RELATION = "@relation";
	public final static String ATTRIBUTE = "@attribute";
	public final static String DATA = "@data";
	public final static String COMMENT = "%";
	
	private String filePath;
	private Instances instances;
	
	public Reader(String filePath, Instances instances) {
		this.filePath = filePath;
		this.instances = instances;
	}

	public void read() {
		try {
			InputStream ips = new FileInputStream(this.filePath);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			
			String line = INIT;
			boolean isData = false;
			
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()) {
					line = line.trim();
					line = line.replaceAll("\\s", " ");
					line = line.replaceAll("\t", " ");
					String lineSplit[] = line.split(" ", 3);
					if (!lineSplit[0].equals(COMMENT)) {
						if (lineSplit[0].equals(RELATION)) {
							this.instances.setRelationName(lineSplit[1]);
						}
						if (lineSplit[0].equals(ATTRIBUTE)) {
							String name = lineSplit[1];
							String values = lineSplit[2];
							
							values = values.replaceAll(" ", "");
							values = values.replaceAll("\\{", "");
							values = values.replaceAll("\\}", "");
							
							ArrayList<String> possibleValues = new ArrayList<String>();
							for (String value : values.split(",")) {
								possibleValues.add(value);
							}
							this.instances.addAttribute(name, possibleValues);
						}
						if (lineSplit[0].equals(DATA)) {
							isData = true;
						} else if (isData) {
							Instance newInstance = new Instance();
							line = line.trim();
							if (line.contains(COMMENT)) {
								line = line.substring(0, line.indexOf(COMMENT));
							}
							line = line.replaceAll(" ", "");
							line = line.replaceAll("\t", "");
							line = line.replaceAll("'", "");
							String attributes[] = line.split(",");
							for (int i = 0; i < attributes.length - 1; i++) {
								newInstance.addAttribute(new Attribute(this.instances.getAttributes().keySet().toArray()[i].toString(), attributes[i], i, 0, 0));
							}
							newInstance.setInstanceClass(new InstanceClass(attributes[attributes.length - 1]));
							this.instances.addInstance(newInstance);
						}	
					}
				}
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
