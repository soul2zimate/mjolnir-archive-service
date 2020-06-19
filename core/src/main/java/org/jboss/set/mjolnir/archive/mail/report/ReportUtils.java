package org.jboss.set.mjolnir.archive.mail.report;

public final class ReportUtils {

    private ReportUtils() {
    }

    public static String stringOrEmpty(String str) {
        return str == null ? "" : str;
    }

}
