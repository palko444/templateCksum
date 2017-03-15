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

	public static void writeFile(String pg, String file, HashMap<String, String> map) {

		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file), charset)) {
			writer.write(pg + "\n");
			for (Map.Entry<String, String> h : map.entrySet()) {
				writer.write(h.getKey() + " " + h.getValue() + "\n");
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	public static List<String> readFile(String file) throws IOException {
		Scanner sc = new Scanner(new File(file));
		List<String> fileRed = new ArrayList<String>();
		while (sc.hasNextLine()) {
			fileRed.add(sc.nextLine());
		}
		sc.close();
		return fileRed;
	}
	
}
