package org.jboss.set.mjolnir.archive.mail.report;

import j2html.tags.DomContent;
import org.eclipse.egit.github.core.Team;
import org.jboss.set.mjolnir.archive.ldap.LdapScanningBean;
import org.jboss.set.mjolnir.archive.mail.report.ReportTable;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static j2html.TagCreator.*;
import static j2html.TagCreator.th;

public class UnregisteredMembersReportTable implements ReportTable {

    private static final String NAME_LABEL = "Name";
    private static final String TEAM_LABEL = "Team";

    private static final String TABLE_STYLE = "width:100%;";
    private static final String CAPTION_STYLE = "padding: 15px; font-size: 160%; text-align: left;";
    private static final String TD_STYLE = "border: 1px solid black; border-collapse: collapse; padding-left: 15px;";
    private static final String BORDER_STYLE = "border: 1px solid black; border-collapse: collapse;";

    @Inject
    private LdapScanningBean ldapScanningBean;

    @Override
    public String composeTable() throws IOException {
        String table = table().withStyle(TABLE_STYLE + TD_STYLE).with(
                caption("Unregistered users and theirs memberships").withStyle(TABLE_STYLE + CAPTION_STYLE),
                tr().with(
                        th(NAME_LABEL),
                        th(TEAM_LABEL)
                ),
                addUnregisteredOrganizationMembersRows(getUnregisteredMembersWithTeams())
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

    private Map<String, List<Team>> getUnregisteredMembersWithTeams() throws IOException {
        Set<String> unregisteredOrganizationMembers = ldapScanningBean.getUnregisteredOrganizationMembers();
        Map<String, List<Team>> userTeams = new HashMap<>();
        for (String member : unregisteredOrganizationMembers) {
            userTeams.put(member, ldapScanningBean.getAllUsersTeams(member));
        };

        return userTeams;
    }
}
