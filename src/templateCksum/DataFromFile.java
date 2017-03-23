package templateCksum;

import java.util.HashMap;
import java.util.Map;

public class DataFromFile {

	final String pg;
	final Map<String, PolicyAttributes> polAttrs = new HashMap<>();

	public DataFromFile(String pg) {
		this.pg = pg;
	}

	public void addPolicyAttrs(String dataFile, PolicyAttributes attr) {
		this.polAttrs.put(dataFile, attr);
	}
}
