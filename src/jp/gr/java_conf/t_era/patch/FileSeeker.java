package jp.gr.java_conf.t_era.patch;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

public class FileSeeker {
	private static final byte[] ANOTHER_SEPARATOR = "\r\n".getBytes();
	private final byte[] separator;
	private final RandomAccessFile ras;

	private int flushedIndex = 0;
	private int currentIndex = 0;

	/**
	 * 指定されたファイルにランダムアクセスするための準備をします。
	 * @param input
	 * @param sep
	 * @throws IOException
	 */
	FileSeeker(File input, byte[] sep) throws IOException {
		this.separator = sep;
		ras = new RandomAccessFile(input, "r");
	}

	/**
	 * 現在、ファイルポインタがEOFを指しているかどうかを返します
	 * @return
	 * @throws IOException
	 */
	boolean isEndOfFile() throws IOException{
		return currentIndex >= ras.length();
	}
	/**
	 * ファイルポインタを一行分進めて、その内容を返します。
	 * 読み進めた分は、内部バッファに溜め込みます。
	 * @return
	 * @throws IOException
	 */
	byte[] readLine() throws IOException {
		long start = currentIndex;

		long end = start + 1;
		int lengthOfSeparator = 0;
		while (true) {
			if (currentIndex >= ras.length()) {
				break;
			} else if (isStartOf(end, separator)) {
				lengthOfSeparator = separator.length;
				break;
			} else if (isStartOf(end, ANOTHER_SEPARATOR)) {
				lengthOfSeparator = ANOTHER_SEPARATOR.length;
				break;
			}
			end ++;
		}
		while (currentIndex < ras.length()
				&& ! isStartOfSeparator(end)) {
			end ++;
		}
		byte[] buffer = new byte[(int)(end - start)];
		ras.seek(start);
		ras.read(buffer);
		currentIndex = (int)end + lengthOfSeparator;
		return buffer;
	}
	void skipLines(int lineNum) throws IOException {
		for (int i = 0; i < lineNum; i ++) {
			readLine();
		}
	}

	/**
	 * ファイルポインタを変更することなく、一行分のデータを返します。
	 * 読み出しの開始ポイントを指示する必要があります。
	 * @param startIndex
	 * @return
	 * @throws IOException
	 */
	StaticReadLineResult staticReadLine(long offset) throws IOException {
		StaticReadLineResult ret  =readLineImpl(currentIndex + offset);
		ras.seek(currentIndex);
		return ret;
	}
	private StaticReadLineResult readLineImpl(long startIndex) throws IOException {
		long end = startIndex + 1;
		int lengthOfSeparator = 0;
		while (true) {
			if (end >= ras.length()) {
				break;
			} else if (isStartOf(end, separator)) {
				lengthOfSeparator = separator.length;
				break;
			} else if (isStartOf(end, ANOTHER_SEPARATOR)) {
				lengthOfSeparator = ANOTHER_SEPARATOR.length;
				break;
			}
			end ++;
		}
		while (end < ras.length()
				&& ! isStartOfSeparator(end)) {
			end ++;
		}
		int length = (int)(end - startIndex);
		byte[] buffer = new byte[length];
		ras.seek(startIndex);
		ras.read(buffer);
		return new StaticReadLineResult(length + lengthOfSeparator, buffer);
	}
	private boolean isStartOfSeparator(long index) throws IOException {
		return isStartOf(index, separator)
			|| isStartOf(index, ANOTHER_SEPARATOR);
	}
	private boolean isStartOf(long index, byte[] sep) throws IOException {
		int length = sep.length;
		byte[] buffer = new byte[length];
		ras.seek(index);
		ras.read(buffer);
		for (int i = 0; i < length; i ++) {
			if (buffer[i] != sep[i]) return false;
		}
		return true;
	}

	/**
	 * 内部バッファに溜め込まれた内容を、指定されたストリームに書き出します。
	 * @param ps
	 * @throws IOException
	 */
	void flush(PrintStream ps) throws IOException {
		int length = currentIndex - (flushedIndex + 1);
		byte[] buffer = new byte[length];
		ras.seek(flushedIndex);
		ras.read(buffer);
		ps.write(buffer);
		ps.flush();
		flushedIndex = currentIndex;
	}
	/**
	 * 内部バッファに溜め込まれた内容を破棄し、指定された内容をストリームに書き出します。
	 * @param ps
	 * @param substitute
	 * @throws IOException
	 */
	void flush(PrintStream ps, byte[] substitute) throws IOException {
		ps.write(substitute);
		ps.flush();
		flushedIndex = currentIndex;
	}
	static class StaticReadLineResult {
		public final long lengthAhead;
		public  final byte[] line;
		StaticReadLineResult(long length, byte[] content) {
			this.line = content;
			lengthAhead = length;
		}
	}
}
