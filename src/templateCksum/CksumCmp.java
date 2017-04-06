package templateCksum;

import java.util.HashMap;
import java.util.Map;

public class CksumCmp {

	public static CmpResults cmpHashMaps(Map<String, String> fromFile, Map<String, String> fromDb) {

		final Map<String, String> wrongCksum = new HashMap<>();
		final Map<String, String> missingFile = new HashMap<>();
		final Map<String, String> missingDb = new HashMap<>();

		for (Map.Entry<String, String> entry : fromFile.entrySet()) {
			if (!fromDb.containsValue(entry.getValue())) {
				missingDb.put(entry.getKey(), entry.getValue());
			} else {
				for (Map.Entry<String, String> entry1 : fromDb.entrySet()) {
					if (entry.getValue().equals(entry1.getValue())) {
						if (!entry.getKey().equals(entry1.getKey())) {
							wrongCksum.put(entry.getKey(), entry.getValue());
						}
					}
					break;
				}
			}
		}

		for (

		Map.Entry<String, String> entry : fromDb.entrySet()) {
			if (!fromFile.containsValue(entry.getValue())) {
				missingFile.put(entry.getKey(), entry.getValue());
			}
		}

		return new CmpResults(missingFile, missingDb, wrongCksum);
	}

}
