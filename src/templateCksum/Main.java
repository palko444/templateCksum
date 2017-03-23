package templateCksum;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Main {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ArgumentParserException {

		Namespace ns = ArgParser.parse(args);
		boolean ret = false;
		final String tempFolder = Template.createTempFolder();

		switch (ns.getString("subcommand")) {
		case "generate":
			String pg = ns.getString("policyGroup");
			ret = generate(ns.getString("file"), pg, createCksumFromDb(pg, tempFolder));
			break;
		case "verify":
			DataFromFile df = FileOperations.readFile(ns.getString("file"));
			ret = verify(df.polAttrs, createCksumFromDb(df.pg, tempFolder));
			break;
		default:
		}

		if (ret) {
			Template.deleteTempFolder(tempFolder);
			System.out.println("Final status: OK");
		} else {
			System.out.println("\nFinal status: FAIL");
			System.out.printf("All donwloaded policies are located here: %s\n", tempFolder);
		}
	}

	public static boolean generate(String file, String pg, Map<String, PolicyAttributes> fromDb)
			throws NoSuchAlgorithmException, IOException {

		FileOperations.writeFile(file, pg, fromDb);
		return true;
	}

	public static Map<String, PolicyAttributes> createCksumFromDb(String pg, String tempFolder)
			throws IOException, NoSuchAlgorithmException {
		HashMap<String, PolicyAttributes> fromDb = new HashMap<>();
		Template.createTempFolder();
		// System.out.println(Template.tempFolder);
		Template.downloadPolicies(pg, tempFolder);
		final File[] dataFiles = Template.getDataFiles(tempFolder);
		try {
			fromDb = Template.processFiles(dataFiles);
		} catch (Exception e) {
			System.out.println(e);
		}
		return fromDb;
	}

	public static boolean verify(Map<String, PolicyAttributes> fromFile, Map<String, PolicyAttributes> fromDb)
			throws IOException, NoSuchAlgorithmException {

		boolean isOk = true;
		final CmpResults results = CksumCmp.cmpHashMaps(fromFile, fromDb);
		final Map<String, PolicyAttributes> wrongCksum = results.wrongCksum;
		final Map<String, PolicyAttributes> missingFile = results.missingFile;
		final Map<String, PolicyAttributes> missingDb = results.missingDb;

		isOk = printResults("These templates are additional on current system.", missingFile);
		isOk = printResults("These templates are missing on current system.", missingDb);
		isOk = printResults("These templates have different cksum.", wrongCksum);
		return isOk;
	}

	public static boolean printResults(String message, Map<String, PolicyAttributes> issue) {

		if (issue.isEmpty()) {
			return true;
		} else {
			System.out.println("\n## " + message);
			for (Map.Entry<String, PolicyAttributes> entry : issue.entrySet()) {
				String dataFile = entry.getKey();
				String cksum = entry.getValue().cksum;
				String name = entry.getValue().name;
				String version = entry.getValue().version;
				String type = entry.getValue().type;
				System.out.printf("%s %s %s %s %s \n", dataFile, cksum, version, type, name);
			}
			return false;
		}
	}

}
