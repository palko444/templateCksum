package templateCksum;

public class PolicyAttributes {

	final String name;
	final String version;
	final String type;
	final String cksum;

	public PolicyAttributes(String name, String version, String type, String cksum) {
		this.name = name;
		this.version = version;
		this.type = type;
		this.cksum = cksum;
	}
}
