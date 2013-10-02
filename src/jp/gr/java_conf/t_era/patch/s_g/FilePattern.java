package jp.gr.java_conf.t_era.patch.s_g;

import jp.gr.java_conf.t_era.patch.Converter;
import jp.gr.java_conf.t_era.patch.ConverterFactory;
import jp.gr.java_conf.t_era.patch.patterning.BytePattern;
import jp.gr.java_conf.t_era.patch.patterning.PrefixPattern;

public class FilePattern implements ConverterFactory {
	private static final BytePattern[] SVN_PATTERN
		= new BytePattern[] {
			  new PrefixPattern("Index: ".getBytes()
					, "diff --git a/", " b/", "\n")
			, new PrefixPattern("===================================================================".getBytes()
					, "")
			, new PrefixPattern("--- ".getBytes()
					, "--- a/", "\n")
			, new PrefixPattern("+++ ".getBytes()
					, "+++ b/", "\n") };

	@Override
	public int isStartLine(byte[] line) {
		if (SVN_PATTERN[0].isMatch(line)) {
			return SVN_PATTERN.length;
		}
		return 0;
	}

	@Override
	public Converter matchPattern(byte[][] lines) {
		if (lines.length != SVN_PATTERN.length) throw new RuntimeException("ミスった");
		for (int i = 0; i < SVN_PATTERN.length; i ++) {
			if (! SVN_PATTERN[i].isMatch(lines[i])) {
				System.err.println("なんか変？？");
				return null;
			}
		}
		byte[] fileName = filterFileName(lines);
		return new FileConverter(fileName);
	}
	private byte[] filterFileName(byte[][] lines) {
		byte[] first = lines[0];
		return SVN_PATTERN[0].remain(first);
	}

	static class FileConverter implements Converter {
		private final byte[] fileName;

		FileConverter(byte[] fileName) {
			this.fileName = fileName;
		}

		@Override
		public byte[] getConvertResult() {
			byte[][] buffer = new byte[SVN_PATTERN.length][];
			for (int i = 0; i < SVN_PATTERN.length; i ++) {
				buffer[i] = SVN_PATTERN[i].convert(fileName);
			}
			return concatAll(buffer);
		}
		private byte[] concatAll(byte[][] origin) {
			int lengthAll = 0;
			for (byte[] parts : origin) {
				lengthAll += parts.length;
			}
			byte[] buffer = new byte[lengthAll];
			int index = 0;
			for (byte[] parts : origin) {
				for (byte b : parts) {
					buffer[index ++] = b;
				}
			}
			return buffer;
		}
	}
}
