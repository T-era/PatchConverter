package jp.gr.java_conf.t_era.patch.patterning;

public interface BytePattern {
	boolean isMatch(byte[] line);
	byte[] remain(byte[] line);
	byte[] convert(byte[] fileName);
}
