package templateCksum;

import java.util.List;

public class CmpResults {
	final List<String> missingFile;
	final List<String> missingDb;
	final List<String> wrongCksum;

	public CmpResults(List<String> missingFile, List<String> missingDb, List<String> wrongCksum) {
		this.missingDb = missingDb;
		this.missingFile = missingFile;
		this.wrongCksum = wrongCksum;
	}

}
