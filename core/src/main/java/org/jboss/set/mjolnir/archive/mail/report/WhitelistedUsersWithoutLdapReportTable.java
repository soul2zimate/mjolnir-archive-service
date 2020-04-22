package org.jboss.set.mjolnir.archive.mail.report;

import j2html.tags.DomContent;
import org.jboss.set.mjolnir.archive.ldap.LdapScanningBean;

import javax.inject.Inject;
import javax.naming.NamingException;
import java.util.Set;

import static j2html.TagCreator.*;

public class WhitelistedUsersWithoutLdapReportTable implements ReportTable {

    private static final String NAME_LABEL = "Name";

    @Inject
    private LdapScanningBean ldapScanningBean;

    @Override
    public String composeTable() throws NamingException {
        String html = div().with(
                h2("Whitelisted Users without an LDAP Account").withStyle(Styles.H2_STYLE),
                table().withStyle(Styles.TABLE_STYLE + Styles.TD_STYLE).with(
                        tr().with(
                                th(NAME_LABEL).withStyle(Styles.TH_STYLE)
                        ),
                        addWhitelistedUserWithoutLdapRows(getWhitelistedUsersWithoutLdapReportTable())
                ))
                .render();
        return html;
    }

    private <T> DomContent addWhitelistedUserWithoutLdapRows(Set<String> whitelistedUsersWithoutLdapAccount) {
        return each(whitelistedUsersWithoutLdapAccount, user -> tr(
                td(user).withStyle(Styles.TD_STYLE)
        ));
    }

    private Set<String> getWhitelistedUsersWithoutLdapReportTable() throws NamingException {
        return ldapScanningBean.getWhitelistedUsersWithoutLdapAccount();
    }
}
