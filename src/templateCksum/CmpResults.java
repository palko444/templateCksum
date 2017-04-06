package templateCksum;

import java.util.Map;

public class CmpResults {
	final Map<String, String> missingFile;
	final Map<String, String> missingDb;
	final Map<String, String> wrongCksum;

	public CmpResults(Map<String, String> missingFile, Map<String, String> missingDb, Map<String, String> wrongCksum) {
		this.missingDb = missingDb;
		this.missingFile = missingFile;
		this.wrongCksum = wrongCksum;
	}

}
