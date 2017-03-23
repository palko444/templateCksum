package templateCksum;

import java.util.HashMap;
import java.util.Map;

public class CksumCmp {

	public static CmpResults cmpHashMaps(Map<String, PolicyAttributes> fromFile, Map<String, PolicyAttributes> fromDb) {

		final Map<String, PolicyAttributes> wrongCksum = new HashMap<>();
		final Map<String, PolicyAttributes> missingFile = new HashMap<>();
		final Map<String, PolicyAttributes> missingDb = new HashMap<>();

		for (Map.Entry<String, PolicyAttributes> entry : fromFile.entrySet()) {
			if (!fromDb.containsKey(entry.getKey())) {
				missingDb.put(entry.getKey(), entry.getValue());
			} else {
				for (Map.Entry<String, PolicyAttributes> entry1 : fromDb.entrySet()) {
					if (entry.getKey().equals(entry1.getKey())) {
						if (!entry.getValue().cksum.equals(entry1.getValue().cksum)) {
							wrongCksum.put(entry.getKey(), entry.getValue());
						}
					}
					break;
				}
			}
		}

		for (

		Map.Entry<String, PolicyAttributes> entry : fromDb.entrySet()) {
			if (!fromFile.containsKey(entry.getKey())) {
				missingFile.put(entry.getKey(), entry.getValue());
			}
		}

		return new CmpResults(missingFile, missingDb, wrongCksum);
	}

}
