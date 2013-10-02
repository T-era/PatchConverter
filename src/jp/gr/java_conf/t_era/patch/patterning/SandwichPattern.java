package jp.gr.java_conf.t_era.patch.patterning;

public class SandwichPattern implements BytePattern{
	private final byte[] prefix;
	private final byte[] suffix;
	private final byte[][] gitPattern;

	public SandwichPattern(byte[] prefix, byte[] suffix, String... gitPattern) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.gitPattern = new byte[gitPattern.length][];
		for (int i = 0; i < gitPattern.length; i ++) {
			this.gitPattern[i] = gitPattern[i].getBytes();
		}
	}
	public boolean isMatch(byte[] line) {
		if (line.length < prefix.length + suffix.length) return false;
		for (int i = 0; i < prefix.length; i ++) {
			if (line[i] != prefix[i]) return false;
		}
		for (int i = 1; i <= suffix.length; i ++) {
			if (line[line.length - i] != suffix[suffix.length - i]) return false;
		}
		return true;
	}
	public byte[] remain(byte[] line) {
		throw new UnsupportedOperationException("すまん！");
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
