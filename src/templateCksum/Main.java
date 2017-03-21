package templateCksum;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

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
			ret = verify(df.polCksum, createCksumFromDb(df.pg, tempFolder));
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

	public static boolean generate(String file, String pg, HashMap<String, String> fromDb)
			throws NoSuchAlgorithmException, IOException {

		FileOperations.writeFile(file, pg, fromDb);
		return true;
	}

	public static HashMap<String, String> createCksumFromDb(String pg, String tempFolder)
			throws IOException, NoSuchAlgorithmException {
		HashMap<String, String> fromDb = new HashMap<>();
		Template.createTempFolder();
		// System.out.println(Template.tempFolder);
		Template.downloadPolicies(pg, tempFolder);
		final File[] dataFiles = Template.getDataFiles(tempFolder);
		fromDb = Template.processFiles(dataFiles);
		return fromDb;
	}

	public static boolean verify(HashMap<String, String> fromFile, HashMap<String, String> fromDb)
			throws IOException, NoSuchAlgorithmException {

		boolean isOk = true;
		final CmpResults results = CksumCmp.cmpHashMaps(fromFile, fromDb);
		final List<String> wrongCksum = results.wrongCksum;
		final List<String> missingFile = results.missingFile;
		final List<String> missingDb = results.missingDb;

		if (!missingFile.isEmpty()) {
			System.out.println("\n## These templates are additional on current system.");
			for (String record : missingFile) {
				System.out.println(record);
			}
			isOk = false;
		}

		if (!missingDb.isEmpty()) {
			System.out.println("\n## These templates are missing on current system.");
			for (String record : missingDb) {
				System.out.println(record);
			}
			isOk = false;
		}

		if (!wrongCksum.isEmpty()) {
			System.out.println("\n## These templates have different cksum.");
			for (String record : wrongCksum) {
				System.out.println(record);
			}
			isOk = false;
		}
		return isOk;
	}

}
