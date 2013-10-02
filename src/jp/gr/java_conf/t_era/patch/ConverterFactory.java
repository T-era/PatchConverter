package jp.gr.java_conf.t_era.patch;

public interface ConverterFactory {
	int isStartLine(byte[] line);
	Converter matchPattern(byte[][] lines);
}
