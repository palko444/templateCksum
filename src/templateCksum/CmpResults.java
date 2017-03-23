package templateCksum;

import java.util.Map;

public class CmpResults {
	final Map<String, PolicyAttributes> missingFile;
	final Map<String, PolicyAttributes> missingDb;
	final Map<String, PolicyAttributes> wrongCksum;

	public CmpResults(Map<String, PolicyAttributes> missingFile, Map<String, PolicyAttributes> missingDb,
			Map<String, PolicyAttributes> wrongCksum) {
		this.missingDb = missingDb;
		this.missingFile = missingFile;
		this.wrongCksum = wrongCksum;
	}

}
