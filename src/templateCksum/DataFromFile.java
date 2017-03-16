package templateCksum;

import java.util.HashMap;

public class DataFromFile {

	String pg;
	HashMap<String, String> polCksum = new HashMap<String, String>();

	public DataFromFile(String pg, HashMap<String, String> polCksum) {
		this.pg = pg;
		this.polCksum = polCksum;
	}
}
