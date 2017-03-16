package templateCksum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

		List<String> fileRed;
		HashMap<String, String> polCksum = new HashMap<>();
		try (Scanner sc = new Scanner(new File(file))) {
			fileRed = new ArrayList<String>();
			while (sc.hasNextLine()) {
				fileRed.add(sc.nextLine());
			}
			for (int i = 1; i < fileRed.size(); ++i) {
				String[] kv = fileRed.get(i).split(" ");
				polCksum.put(kv[0], kv[1]);
			}
		}
		return new DataFromFile(fileRed.get(0), polCksum);
	}

}
