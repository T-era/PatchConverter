package jp.gr.java_conf.t_era.patch;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;

public class GitInfo {
	public final String mailAddress;
	public final String subject;
	public final Date date;

	public GitInfo(final String mailAddress, final String subject, final Date date) {
		this.mailAddress = mailAddress;
		this.subject = subject;
		this.date = date;
	}
	public GitInfo(final String mailAddress, final String subject) {
		this(mailAddress, subject, new Date());
	}

	private static final MessageFormat PATCH_HEADER_FORMAT
		= new MessageFormat("From: {0}\n"
				+ "Date: {1,date,E',' dd MMM yyyy HH:mm:ss '+0900'}\n"
				+ "Subject: {2}\n"
				+ "\n"
				+ "---\n", Locale.ENGLISH);
	public String toPatchHeader() {
		return PATCH_HEADER_FORMAT.format(new Object[] { mailAddress, date, subject });
	}
}
