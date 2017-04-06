package templateCksum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

public class FileOperations {

	public static void writeFile(String file, String pg, Map<String, String> map) throws IOException {

		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file), charset)) {
			writer.write(pg + "\n");
			for (Map.Entry<String, String> h : map.entrySet()) {
				String polIdentifier = h.getValue();
				String cksum = h.getKey();
				writer.write(cksum + " " + polIdentifier + "\n");
			}
		}
	}

	public static DataFromFile readFile(String file) throws IOException {

		final DataFromFile df;
		try (Scanner sc = new Scanner(new File(file))) {
			String pg = sc.nextLine();
			df = new DataFromFile(pg);
			while (sc.hasNextLine()) {
				String[] kv = sc.nextLine().split(" ", 2);
				String cksum = kv[0];
				String polIdentifier = kv[1];
				df.addPolicyAttrs(cksum, polIdentifier);
			}
		}
		return df;
	}

}
