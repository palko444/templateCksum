package templateCksum;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Main {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ArgumentParserException {

		Namespace ns = ArgParser.parse(args);
		boolean ret = false;

		switch (ns.getString("subcommand")) {
		case "generate":
			String pg = ns.getString("policyGroup");
			ret = generate(ns.getString("file"), pg, createCksumFromDb(pg));
			break;
		case "verify":
			DataFromFile df = FileOperations.readFile(ns.getString("file"));
			ret = verify(df.polCksum, createCksumFromDb(df.pg));
			break;
		default:
		}

		if (ret) {
			Template.deleteTempFolder(Template.tempFolder);
			System.out.println("all OK");
		} else {
			System.out.println("FAIL");
		}
	}

	public static boolean generate(String file, String pg, HashMap<String, String> fromDb)
			throws NoSuchAlgorithmException, IOException {

		FileOperations.writeFile(file, pg, fromDb);
		return true;
	}

	public static HashMap<String, String> createCksumFromDb(String pg) throws IOException, NoSuchAlgorithmException {
		HashMap<String, String> fromDb = new HashMap<>();
		Template.createTempFolder();
		System.out.println(Template.tempFolder);
		Template.downloadPolicies(pg);
		File[] dataFiles = Template.getDataFiles(Template.tempFolder);
		fromDb = Template.processFiles(dataFiles);
		return fromDb;
	}

	public static boolean verify(HashMap<String, String> fromFile, HashMap<String, String> fromDb)
			throws IOException, NoSuchAlgorithmException {

		List<String> wrong = new ArrayList<>();
		wrong = CksumCmp.cmpHashMaps(fromFile, fromDb);

		if (!wrong.isEmpty()) {
			System.out.println("These files have different cksum");
			for (String dataFile : wrong) {
				System.out.println(dataFile);
			}
			return false;
		}
		return true;
	}

}
