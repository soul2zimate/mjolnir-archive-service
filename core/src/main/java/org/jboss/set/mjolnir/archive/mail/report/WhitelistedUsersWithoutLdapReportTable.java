package org.jboss.set.mjolnir.archive.mail.report;

import org.jboss.set.mjolnir.archive.domain.RegisteredUser;

import javax.naming.NamingException;
import java.util.Set;

public class WhitelistedUsersWithoutLdapReportTable extends WhitelistedUserReportTable {

    private static final String REPORT_TABLE_TITLE = "Whitelisted Users without an LDAP Account";

    @Override
    Set<RegisteredUser> getWhitelistedUsers() throws NamingException {
        return ldapScanningBean.getWhitelistedUsersWithoutLdapAccount();
    }

    @Override
    String getReportTableTitle() {
        return REPORT_TABLE_TITLE;
    }
}
