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

	public static void writeFile(String file, String pg, HashMap<String, String> map) throws IOException {

		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file), charset)) {
			writer.write(pg + "\n");
			for (Map.Entry<String, String> h : map.entrySet()) {
				writer.write(h.getKey() + " " + h.getValue() + "\n");
			}
		}
	}

	public static DataFromFile readFile(String file) throws IOException {

		try (Scanner sc = new Scanner(new File(file))) {
			HashMap<String, String> polCksum = new HashMap<>();
			String pg = sc.nextLine();
			while (sc.hasNextLine()) {
				String[] kv = sc.nextLine().split(" ");
				polCksum.put(kv[0], kv[1]);
			}
			return new DataFromFile(pg, polCksum);
		}
	}

}
