package templateCksum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CksumCmp {

	public static CmpResults cmpHashMaps(HashMap<String, String> fromFile, HashMap<String, String> fromDb) {

		final List<String> wrongCksum = new ArrayList<>();
		final List<String> missingFile = new ArrayList<>();
		final List<String> missingDb = new ArrayList<>();

		for (Map.Entry<String, String> entry : fromFile.entrySet()) {
			if (!fromDb.containsKey(entry.getKey())) {
				missingDb.add(entry.getKey());
			} else {
				if (!fromFile.get(entry.getKey()).equals(fromDb.get(entry.getKey()))) {
					wrongCksum.add(entry.getKey());
				}
			}
		}

		for (Map.Entry<String, String> entry : fromDb.entrySet()) {
			if (!fromFile.containsKey(entry.getKey())) {
				missingFile.add(entry.getKey());
			}
		}

		return new CmpResults(missingFile, missingDb, wrongCksum);
	}

}
