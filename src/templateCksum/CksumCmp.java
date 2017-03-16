package templateCksum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CksumCmp {

	public static List<String> cmpHashMaps(HashMap<String, String> fromFile, HashMap<String, String> fromDb) {

		List<String> wrongFile = new ArrayList<>();
		for (Map.Entry<String, String> entry : fromFile.entrySet()) {
			if (!fromFile.get(entry.getKey()).equals(fromDb.get(entry.getKey()))) {
				wrongFile.add(entry.getKey());
			}
		}

		return wrongFile;
	}

}
