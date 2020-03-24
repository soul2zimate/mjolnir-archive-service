package org.jboss.set.mjolnir.archive.mail.report;

import j2html.tags.DomContent;
import org.jboss.set.mjolnir.archive.ldap.LdapScanningBean;

import javax.inject.Inject;
import javax.naming.NamingException;
import java.util.Set;

import static j2html.TagCreator.*;

public class WhitelistedUsersWithoutLdapReportTable implements ReportTable {

    private static final String NAME_LABEL = "Name";

    private static final String TABLE_STYLE = "width:100%;";
    private static final String CAPTION_STYLE = "padding: 15px; font-size: 160%; text-align: left;";
    private static final String TD_STYLE = "border: 1px solid black; border-collapse: collapse; padding-left: 15px;";

    @Inject
    private LdapScanningBean ldapScanningBean;

    @Override
    public String composeTable() throws NamingException {
        String table = table().withStyle(TABLE_STYLE + TD_STYLE).with(
                caption("Whitelisted users without LDAP account").withStyle(TABLE_STYLE + CAPTION_STYLE),
                tr().with(
                        th(NAME_LABEL)
                ),
                addWhitelistedUserWithoutLdapRows(getWhitelistedUsersWithoutLdapReportTable())
        ).render();

        return table;
    }

    private <T> DomContent addWhitelistedUserWithoutLdapRows(Set<String> whitelistedUsersWithoutLdapAccount) {
        return each(whitelistedUsersWithoutLdapAccount, user -> tr(
                td(user).withStyle(TD_STYLE)
        ));
    }

    private Set<String> getWhitelistedUsersWithoutLdapReportTable() throws NamingException {
        return ldapScanningBean.getWhitelistedUsersWithoutLdapAccount();
    }
}
