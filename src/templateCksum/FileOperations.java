package templateCksum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileOperations {

	public static void writeFile(String file, String pg, HashMap<String, PolicyAttributes> map) throws IOException {

		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file), charset)) {
			writer.write(pg + "\n");
			for (Map.Entry<String, PolicyAttributes> h : map.entrySet()) {
				String name = h.getValue().name;
				String cksum = h.getValue().cksum;
				String version = h.getValue().version;
				String type = h.getValue().type;
				writer.write(h.getKey() + " " + cksum + " " + version + " " + type + " " + name + "\n");
			}
		}
	}

	public static DataFromFile readFile(String file) throws IOException {

		try (Scanner sc = new Scanner(new File(file))) {
			String pg = sc.nextLine();
			DataFromFile df = new DataFromFile(pg);
			while (sc.hasNextLine()) {
				String[] kv = sc.nextLine().split(" ", 5);
				String fileName = kv[0];
				String cksum = kv[1];
				String version = kv[2];
				String type = kv[3];
				String name = kv[4];
				PolicyAttributes attrs = new PolicyAttributes(name, version, type, cksum);
				df.addPolicyAttrs(fileName, attrs);
			}
			return df;
		}
	}

}
