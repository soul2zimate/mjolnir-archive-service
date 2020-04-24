package org.jboss.set.mjolnir.archive.mail.report;

import j2html.tags.DomContent;
import org.jboss.set.mjolnir.archive.domain.RegisteredUser;
import org.jboss.set.mjolnir.archive.ldap.LdapScanningBean;

import javax.inject.Inject;
import javax.naming.NamingException;
import java.util.Set;

import static j2html.TagCreator.*;

public class WhitelistedUsersWithLdapReportTable implements ReportTable {

    private static final String NAME_LABEL = "Name";
    private static final String RESPONSIBLE_PERSON_LABEL = "Responsible person";

    @Inject
    private LdapScanningBean ldapScanningBean;

    @Override
    public String composeTable() throws NamingException {
        String html = div().with(
                h2("Whitelisted Users with an LDAP Account").withStyle(Styles.H2_STYLE),
                table().withStyle(Styles.TABLE_STYLE + Styles.TD_STYLE).with(
                        tr().with(
                                th(NAME_LABEL).withStyle(Styles.TH_STYLE),
                                th(RESPONSIBLE_PERSON_LABEL).withStyle(Styles.TH_STYLE)
                        ),
                        addWhitelistedUserWithLdapRows(getWhitelistedUsersWithLdapReportTable())
                ))
                .render();
        return html;
    }

    private <T> DomContent addWhitelistedUserWithLdapRows(Set<RegisteredUser> whitelistedUsersWithoutLdapAccount) {
        return each(whitelistedUsersWithoutLdapAccount, user -> tr(
                td(user.getGithubName()).withStyle(Styles.TD_STYLE),
                td(user.getResponsiblePerson()).withStyle(Styles.TD_STYLE)
        ));
    }

    private Set<RegisteredUser> getWhitelistedUsersWithLdapReportTable() throws NamingException {
        return ldapScanningBean.getWhitelistedUsersWithLdapAccount();
    }
}
