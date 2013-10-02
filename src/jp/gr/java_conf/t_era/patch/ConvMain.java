package jp.gr.java_conf.t_era.patch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

import jp.gr.java_conf.t_era.patch.FileSeeker.StaticReadLineResult;
import jp.gr.java_conf.t_era.patch.s_g.FilePattern;
import jp.gr.java_conf.t_era.patch.s_g.NewFilePattern;

public class ConvMain {
	public static void main(String[] args) throws IOException {
		boolean hasError = false;
		PrintStream output = System.out;
		String inputFileName = null;
		String patchAuthor = null;
		String patchSubject = null;

		for (int i = 0; i < args.length; i ++) {
			String opt = args[i].toLowerCase();
			if (opt.equals("-o")) {
				output = getPrintStream(args[++i]);
			} else if (opt.equals("-m")) {
				patchAuthor = args[++i];
			} else if (opt.equals("-s")) {
				patchSubject = args[++i];
			} else if(opt.startsWith("-")) {
				hasError = true;
			} else {
				inputFileName = args[i];
			}
		}
		if (hasError
				|| inputFileName == null
				|| patchAuthor == null
				|| patchSubject == null) {
			usage();
			System.exit(1);
		}
		File inputFile = new File(inputFileName);
		if (! inputFile.exists()
				|| ! inputFile.isFile()) {
			usage();
			System.exit(1);
		} else {
			GitInfo gitInfo = new GitInfo(patchAuthor, patchSubject);
			output.write(gitInfo.toPatchHeader().getBytes());
			FileSeeker fs = new FileSeeker(inputFile, "\n".getBytes());
			new ConvMain().seek(fs, output, new NewFilePattern(), new FilePattern());
		}
	}
	private static PrintStream getPrintStream(String path) throws IOException{
		return new PrintStream(new FileOutputStream(path));
	}
	private static void usage() {
		System.out.println("Usage: ConvMain [option] {git info} {source file path}");
		System.out.println("\tsource file path:");
		System.out.println("\t\t\tSVN patch file");
		System.out.println("\tgit info:");
		System.out.println("\t\t-m {mail-address}:");
		System.out.println("\t\t\t(Commit author) Patch mail from");
		System.out.println("\t\t-s {subject}:");
		System.out.println("\t\t\t(Commit comment) Patch mail subject");
		System.out.println("\toption:");
		System.out.println("\t\t-o {filePath}:");
		System.out.println("\t\t\toutput to filePath");
		System.out.println("");
		System.out.println("\tREMARKS");
		System.out.println("\t\t\tGIT上のコミット日時は、このコマンドを動かしたタイミングになります。");
	}

	void seek(FileSeeker fs, PrintStream ps, ConverterFactory... converters) throws IOException {
		while (! fs.isEndOfFile()) {
			byte[] line = fs.readLine();
			boolean converted = false;
			for (ConverterFactory converter : converters) {
				int lineNum = converter.isStartLine(line);
				if (lineNum > 0) {
					byte[][] lines = new byte[lineNum][];
					lines[0] = line;
					long offset = 0;
					for (int i = 1; i < lineNum; i ++) {
						StaticReadLineResult srr = fs.staticReadLine(offset);
						lines[i] = srr.line;;
						offset += srr.lengthAhead;
					}
					Converter conv = converter.matchPattern(lines);
					if (conv != null) {
						fs.skipLines(lineNum - 1);
						fs.flush(ps, conv.getConvertResult());
						converted = true;
						break;
					}
				}
			}
			if (!converted) {
				fs.flush(ps);
			}
		}
	}
}
