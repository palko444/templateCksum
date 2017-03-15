package templateCksum;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Main {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ArgumentParserException {
		// TODO Auto-generated method stub

		Namespace ns = ArgParser.parse(args);
		switch (ns.getString("subcommand")) {
		case "generate":
			generate(ns, false);
			break;
		case "compare":
			generate(ns, true);
			compare(ns);
			break;
		default:
			throw new AssertionError("unhandled subcommand");
		}
	}

	public static void generate(Namespace ns, boolean cmp) throws IOException, NoSuchAlgorithmException {
		Template t = new Template();
		t.createTempFolder();
		System.out.println(t.tempFolder);
		t.downloadPolicies(ns.getString("file"));
		File[] dataFiles = Template.getDataFiles(t.tempFolder);
		HashMap<String, String> map = Template.processFiles(dataFiles);
		String pg;
		if (cmp) {
			//TODO
			pg = "read first line in file";
		} else {
			pg = ns.getString("policyGroup");
		}
		FileOperations.writeFile(ns.getString("file"), pg, map);
		Template.deleteTempFolder(t.tempFolder);
	}

	public static void compare(Namespace ns) throws IOException, NoSuchAlgorithmException {
		
		


	}

}
