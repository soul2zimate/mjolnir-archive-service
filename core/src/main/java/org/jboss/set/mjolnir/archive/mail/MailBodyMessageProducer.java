package org.jboss.set.mjolnir.archive.mail;

import j2html.tags.DomContent;
import org.eclipse.egit.github.core.Team;
import org.jboss.set.mjolnir.archive.domain.RemovalStatus;
import org.jboss.set.mjolnir.archive.domain.UserRemoval;
import org.jboss.set.mjolnir.archive.ldap.LdapScanningBean;

import javax.inject.Inject;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static j2html.TagCreator.*;
import static j2html.TagCreator.td;

public class MailBodyMessageProducer {

    private static final String NAME_LABEL = "Name";
    private static final String CREATED_LABEL = "Created";
    private static final String STARTED_LABEL = "Started";
    private static final String STATUS_LABEL = "Status";
    private static final String TEAM_LABEL = "Team";

    private static final String TABLE_STYLE = "width:100%;";
    private static final String CAPTION_STYLE = "padding: 15px; font-size: 160%; text-align: left;";
    private static final String BORDER_STYLE = "border: 1px solid black; border-collapse: collapse;";
    private static final String TD_STYLE = "border: 1px solid black; border-collapse: collapse; padding-left: 15px;";
    private static final String FONT_SUCCESS_STYLE = "color:green;";
    private static final String FONT_ERROR_STYLE = "color:red;";

    @Inject
    private RemovalsReportBean removalsReportBean;

    @Inject
    private LdapScanningBean ldapScanningBean;

    public String composeMessageBody() throws NamingException, IOException {
        List<UserRemoval> lastStartedRemovals = removalsReportBean.getLastFinishedRemovals();
        String body = composeRemovalsTable(lastStartedRemovals);

        Set<String> whitelistedUsersWithoutLdapAccount = ldapScanningBean.getWhitelistedUsersWithoutLdapAccount();
        body = body + composeWhitelistedWithoutLdapTable(whitelistedUsersWithoutLdapAccount);


        Set<String> unregisteredOrganizationMembers = ldapScanningBean.getUnregisteredOrganizationMembers();

        Map<String, List<Team>> userTeams = new HashMap<>();
        for (String member : unregisteredOrganizationMembers) {
            userTeams.put(member, ldapScanningBean.getAllUsersTeams(member));
        }

        body = body + composeUnregisteredOrganizationMembersTable(userTeams);

        return body;
    }

    private String composeUnregisteredOrganizationMembersTable(Map<String, List<Team>> userTeams) {
        String table = table().withStyle(TABLE_STYLE + TD_STYLE).with(
                caption("Unregistered users and theirs memberships").withStyle(TABLE_STYLE + CAPTION_STYLE),
                tr().with(
                        th(NAME_LABEL),
                        th(TEAM_LABEL)
                ),
                addUnregisteredOrganizationMembersRows(userTeams)
        ).render();

        return table;
    }

    private <T> DomContent addUnregisteredOrganizationMembersRows(Map<String, List<Team>> userTeams) {
        return each(userTeams, userTeam -> tr(
                td(userTeam.getKey()).withStyle(TD_STYLE),
                td(
                        table().withStyle(TABLE_STYLE + BORDER_STYLE).with(
                                each(userTeam.getValue(), team -> tr(
                                        td(team.getName()).withStyle(TD_STYLE)
                                ))
                        )
                ).withStyle(BORDER_STYLE)
        ));
    }

    private String composeWhitelistedWithoutLdapTable(Set<String> whitelistedUsersWithoutLdapAccount) {
        String table = table().withStyle(TABLE_STYLE + TD_STYLE).with(
                caption("Whitelisted users without LDAP account").withStyle(TABLE_STYLE + CAPTION_STYLE),
                tr().with(
                        th(NAME_LABEL)
                ),
                addWhitelistedUserWithoutLdapRows(whitelistedUsersWithoutLdapAccount)
        ).render();

        return table;
    }

    private <T> DomContent addWhitelistedUserWithoutLdapRows(Set<String> whitelistedUsersWithoutLdapAccount) {
        return each(whitelistedUsersWithoutLdapAccount, user -> tr(
                td(user).withStyle(TD_STYLE)
        ));
    }

    private String composeRemovalsTable(List<UserRemoval> lastStartedRemovals) {
        String table = table().withStyle(TABLE_STYLE + TD_STYLE).with(
                caption("User removals").withStyle(TABLE_STYLE + CAPTION_STYLE),
                tr().with(
                        th(NAME_LABEL),
                        th(CREATED_LABEL),
                        th(STARTED_LABEL),
                        th(STATUS_LABEL)
                ),
                addUserRemovalRows(lastStartedRemovals)
        ).render();

        return table;
    }

    private <T> DomContent addUserRemovalRows(List<UserRemoval> removals) {
        return each(removals, removal -> tr(
                td(removal.getUsername()).withStyle(TD_STYLE),
                td(removal.getCreated().toString()).withStyle(TD_STYLE),
                td(removal.getStarted().toString()).withStyle(TD_STYLE),
                RemovalStatus.COMPLETED.equals(removal.getStatus()) ?
                        td(removal.getStatus().toString()).withStyle(TD_STYLE + FONT_SUCCESS_STYLE) :
                        td(removal.getStatus().toString()).withStyle(TD_STYLE + FONT_ERROR_STYLE)
        ));
    }
}
