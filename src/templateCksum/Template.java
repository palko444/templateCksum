package templateCksum;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ArrayBlockingQueue;

import org.joda.time.Duration;

import com.dxc.command_executor.CommandExecutor;
import com.dxc.command_executor.StreamGobbler.QueueFullException;

public class Template {

	String tempFolder;

	public void createTempFolder() throws IOException {
		tempFolder = Files.createTempDirectory(Paths.get("/tmp"), "polDown").toString();

	}

	public void downloadPolicies(String pg) throws QueueFullException, IOException {
		CommandExecutor.create("/opt/OV/bin/OpC/utils/opctempl", "-download", "pol_group=" + pg, "dir=" + tempFolder)
				.exec(Duration.standardSeconds(30));
	}

	public void deleteTempFolder(String tmpFolder) {

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

	public File[] getDataFiles(String folder) {
		File f = new File(folder);
		return f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith("_data");
			}
		});
	}

	public void calculateMd5(File[] dataFiles) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		for (File file : dataFiles) {
			try (InputStream is = Files.newInputStream(file.toPath());
					DigestInputStream dis = new DigestInputStream(is, md)) {
				/* Read decorated stream (dis) to EOF as normal... */
			}
			byte[] digest = md.digest();
			System.out.println("file: " + file + " cksum: " + bytesToHex(digest));
		}
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
}
