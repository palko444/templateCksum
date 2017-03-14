package templateCksum;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		// TODO Auto-generated method stub

		
		Template t = new Template();
		t.createTempFolder();
		System.out.println(t.tempFolder);
		t.downloadPolicies(args[0]);
		File[] dataFiles = t.getDataFiles(t.tempFolder);
		t.calculateMd5(dataFiles);
		
	}

}
