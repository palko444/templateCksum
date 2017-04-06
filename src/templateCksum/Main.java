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
//			if (!ns.getBoolean("k")) {
				Template.deleteTempFolder(tempFolder);
//			}
			System.out.println("Final status: OK");
		} else {
			System.out.println("\nFinal status: FAIL");
			System.out.printf("All donwloaded policies are located here: %s\n", tempFolder);
		}
	}

	public static boolean generate(String file, String pg, Map<String, String> fromDb)
			throws NoSuchAlgorithmException, IOException {

		FileOperations.writeFile(file, pg, fromDb);
		return true;
	}

	public static Map<String, String> createCksumFromDb(String pg, String tempFolder)
			throws IOException, NoSuchAlgorithmException {
		HashMap<String, String> fromDb = new HashMap<>();
		Template.downloadPolicies(pg, tempFolder);
		final File[] dataFiles = Template.getDataFiles(tempFolder);
		try {
			fromDb = Template.processFiles(dataFiles);
		} catch (Exception e) {
			System.out.println(e);
		}
		return fromDb;
	}

	public static boolean verify(Map<String, String> fromFile, Map<String, String> fromDb)
			throws IOException, NoSuchAlgorithmException {

		final CmpResults results = CksumCmp.cmpHashMaps(fromFile, fromDb);
		final Map<String, String> wrongCksum = results.wrongCksum;
		final Map<String, String> missingFile = results.missingFile;
		final Map<String, String> missingDb = results.missingDb;

		boolean noMissingTemplates = printResults("These templates are additional on current system.", missingFile);
		boolean noAditionalTemplates = printResults("These templates are missing on current system.", missingDb);
		boolean noWrongCksums = printResults("These templates have different cksum.", wrongCksum);
		return noMissingTemplates && noAditionalTemplates && noWrongCksums;
	}

	public static boolean printResults(String message, Map<String, String> issue) {

		if (issue.isEmpty()) {
			return true;
		} else {
			System.out.println("\n## " + message);
			for (Map.Entry<String, String> entry : issue.entrySet()) {
				String cksum = entry.getKey();
				String polIdentifier = entry.getValue();
				System.out.printf("%s %s \n", cksum, polIdentifier);
			}
			return false;
		}
	}

}
