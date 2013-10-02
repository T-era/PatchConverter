package jp.gr.java_conf.t_era.patch.patterning;

public class PrefixPattern implements BytePattern{
	private final byte[] prefix;
	private final byte[][] gitPattern;

	public PrefixPattern(byte[] prefix, String... gitPattern) {
		this.prefix = prefix;
		this.gitPattern = new byte[gitPattern.length][];
		for (int i = 0; i < gitPattern.length; i ++) {
			this.gitPattern[i] = gitPattern[i].getBytes();
		}
	}
	public boolean isMatch(byte[] line) {
		if (line.length < prefix.length) return false;
		for (int i = 0; i < prefix.length; i ++) {
			if (line[i] != prefix[i]) return false;
		}
		return true;
	}
	public byte[] remain(byte[] line) {
		int length = line.length - prefix.length;
		byte[] remain = new byte[length];
		for (int i = 0; i < length; i ++) {
			remain[i] = line[i + prefix.length];
		}
		return remain;
	}
	public byte[] convert(byte[] fileName) {
		int length = 0;
		for (byte[] span : gitPattern) {
			length += span.length;
		}
		length += fileName.length * (gitPattern.length - 1);
		byte[] buffer = new byte[length];

		int index = 0;
		for (int i = 0; i < gitPattern.length; i ++) {
			if (i != 0) {
				for (int j = 0; j < fileName.length; j ++) {
					buffer[index ++] = fileName[j];
				}
			}
			byte[] span = gitPattern[i];
			for (int j = 0; j < span.length; j ++) {
				buffer[index ++] = span[j];
			}
		}
		return buffer;
	}
}
