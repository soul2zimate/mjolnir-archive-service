package org.jboss.set.mjolnir.archive.mail.report;

import j2html.tags.DomContent;
import org.jboss.set.mjolnir.archive.domain.RemovalStatus;
import org.jboss.set.mjolnir.archive.domain.UserRemoval;
import org.jboss.set.mjolnir.archive.mail.RemovalsReportBean;

import javax.inject.Inject;
import java.util.List;

import static j2html.TagCreator.*;
import static j2html.TagCreator.th;

public class RemovalsReportTable implements ReportTable {

    private static final String NAME_LABEL = "Username";
    private static final String CREATED_LABEL = "Created";
    private static final String STARTED_LABEL = "Started";
    private static final String STATUS_LABEL = "Status";

    @Inject
    private RemovalsReportBean removalsReportBean;

    @Override
    public String composeTable() {
        String html = div().with(
                h2("User Removals").withStyle(Styles.H2_STYLE),
                p("...performed during the last week").withStyle(Styles.SUB_HEADING_STYLE),
                table().withStyle(Styles.TABLE_STYLE + Styles.TD_STYLE).with(
                        tr().with(
                                th(NAME_LABEL).withStyle(Styles.TH_STYLE),
                                th(CREATED_LABEL).withStyle(Styles.TH_STYLE),
                                th(STARTED_LABEL).withStyle(Styles.TH_STYLE),
                                th(STATUS_LABEL).withStyle(Styles.TH_STYLE)
                        ),
                        addUserRemovalRows(getRemovals())
                )).render();
        return html;
    }

    private <T> DomContent addUserRemovalRows(List<UserRemoval> removals) {
        return each(removals, removal -> tr(
                td(removal.getUsername()).withStyle(Styles.TD_STYLE),
                td(removal.getCreated().toString()).withStyle(Styles.TD_STYLE),
                td(removal.getStarted().toString()).withStyle(Styles.TD_STYLE),
                RemovalStatus.COMPLETED.equals(removal.getStatus()) ?
                        td(removal.getStatus().toString()).withStyle(Styles.TD_STYLE + Styles.FONT_SUCCESS_STYLE) :
                        td(removal.getStatus().toString()).withStyle(Styles.TD_STYLE + Styles.FONT_ERROR_STYLE)
        ));
    }

    private List<UserRemoval> getRemovals() {
        return removalsReportBean.getLastFinishedRemovals();
    }
}
