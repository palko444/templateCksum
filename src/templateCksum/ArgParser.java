package templateCksum;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

public class ArgParser {

	public static Namespace parse(String[] args) {
		ArgumentParser parser = ArgumentParsers.newArgumentParser("pol")
				.description("Check if templates were correctly uploaded").version("00.02");
		parser.addArgument("-V", "--version").help("print version of OMC").action(Arguments.version());
		parser.addArgument("-k", "--keep").help("Keep temporary files.").action(Arguments.storeTrue())
				.setDefault(false);

		Subparsers sp = parser.addSubparsers().dest("subcommand");
		Subparser spGenerate = sp.addParser("generate").help("Generate cksum file");
		Subparser spVerify = sp.addParser("verify").help("Verify cksum");

		spGenerate.addArgument("-p", "--policyGroup").help("Pg to download").type(String.class).required(true);
		spGenerate.addArgument("-f", "--file").help("Generate cksum file from pg.").type(String.class).required(true);
		spVerify.addArgument("-f", "--file").help("File to generate from.").type(String.class).required(true);

		try {
			return parser.parseArgs(args);
		} catch (ArgumentParserException e) {
			e.getParser().printHelp();
			// parser.printHelp();
			System.exit(1);
			return null;
		}
	}
}
