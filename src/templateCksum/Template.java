package templateCksum;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.Duration;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.dxc.command_executor.CommandExecutor;
import com.dxc.command_executor.StreamGobbler.QueueFullException;

public class Template {

	public static String createTempFolder() throws IOException {
		return Files.createTempDirectory(Paths.get("/tmp"), "polDown").toString();

	}

	public static void downloadPolicies(String pg, String tempFolder) throws QueueFullException, IOException {
		CommandExecutor.create("/opt/OV/bin/OpC/utils/opctempl", "-download", "pol_group=" + pg, "dir=" + tempFolder)
				.exec(Duration.standardSeconds(30));
	}

	public static void deleteTempFolder(String tmpFolder) {

		File tmp = new File(tmpFolder);
		if (tmp.exists()) {
			File[] files = tmp.listFiles();
			if (null != files) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteTempFolder(file.toString());
					} else {
						file.delete();
					}
				}
			}
			tmp.delete();
		}
	}

	public static File[] getDataFiles(String folder) {
		File f = new File(folder);
		return f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith("_data");
			}
		});
	}

	public static HashMap<String, String> processFiles(File[] dataFiles) throws NoSuchAlgorithmException, IOException {
		HashMap<String, String> cksumFile = new HashMap<String, String>();
		for (File file : dataFiles) {
			cksumFile.put(file.getName(), calculateSha256(file));
		}
		return cksumFile;
	}

	public static HashMap<String, String> readXml(File[] dataFiles) throws ParserConfigurationException, SAXException, IOException {

		for (File file : dataFiles) {
			File xmlFile = new File(file.toString().replace("_data", "_header.xml"));

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

		}

		return null;
	}

	public static String calculateSha256(File file) throws IOException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		try (InputStream is = Files.newInputStream(file.toPath())) {
			byte[] dataBytes = new byte[8192];
			int nread = 0;
			while ((nread = is.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
		}
		byte[] digest = md.digest();
		return bytesToHex(digest);

	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

}
