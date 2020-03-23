package org.jboss.set.mjolnir.archive.mail;

import org.jboss.set.mjolnir.archive.mail.report.ReportTable;

import javax.naming.NamingException;
import java.io.IOException;
import java.util.List;

public class MailBodyMessageProducer {

    public String composeMessageBody(List<ReportTable> reportTables) throws IOException, NamingException {
        StringBuilder bodyBuilder = new StringBuilder();

        for (ReportTable reportTable : reportTables) {
            bodyBuilder.append(reportTable.composeTable());
        }

        return bodyBuilder.toString();
    }
}
