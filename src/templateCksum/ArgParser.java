package templateCksum;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

public class ArgParser {

	public static Namespace parse(String[] args) throws ArgumentParserException {
		ArgumentParser parser = ArgumentParsers.newArgumentParser("pol")
				.description("Check if templates were correctly uploaded");
		parser.addArgument("-f", "--file").help("Generate cksum file from pg").type(String.class).required(true);

		Subparsers sp = parser.addSubparsers().dest("subcommand");
		Subparser spGenerate = sp.addParser("generate").help("generate cksum file");
		Subparser spCompare = sp.addParser("compare").help("Compare pg vs cksum file");

		spGenerate.addArgument("-p", "--policyGroup").help("Pg to download").type(String.class).required(true);

		parser.addArgument("-c", "--compare").help("Comapare pg from db accorting to file")
				.action(Arguments.storeTrue()).setDefault(false);

		return parser.parseArgs(args);
	}
}
