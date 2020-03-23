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

    private static final String NAME_LABEL = "Name";
    private static final String CREATED_LABEL = "Created";
    private static final String STARTED_LABEL = "Started";
    private static final String STATUS_LABEL = "Status";

    private static final String TABLE_STYLE = "width:100%;";
    private static final String CAPTION_STYLE = "padding: 15px; font-size: 160%; text-align: left;";
    private static final String TD_STYLE = "border: 1px solid black; border-collapse: collapse; padding-left: 15px;";
    private static final String FONT_SUCCESS_STYLE = "color:green;";
    private static final String FONT_ERROR_STYLE = "color:red;";

    @Inject
    private RemovalsReportBean removalsReportBean;

    @Override
    public String composeTable() {
        String table = table().withStyle(TABLE_STYLE + TD_STYLE).with(
                caption("User removals").withStyle(TABLE_STYLE + CAPTION_STYLE),
                tr().with(
                        th(NAME_LABEL),
                        th(CREATED_LABEL),
                        th(STARTED_LABEL),
                        th(STATUS_LABEL)
                ),
                addUserRemovalRows(getRemovals())
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

    private List<UserRemoval> getRemovals() {
        return removalsReportBean.getLastFinishedRemovals();
    }
}
