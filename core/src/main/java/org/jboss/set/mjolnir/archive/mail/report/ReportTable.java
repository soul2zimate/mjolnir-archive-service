package org.jboss.set.mjolnir.archive.mail.report;

import javax.naming.NamingException;
import java.io.IOException;

public interface ReportTable {

    public String composeTable() throws IOException, NamingException;
}
