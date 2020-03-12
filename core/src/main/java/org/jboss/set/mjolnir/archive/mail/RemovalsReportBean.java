package org.jboss.set.mjolnir.archive.mail;

import org.jboss.set.mjolnir.archive.domain.UserRemoval;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class RemovalsReportBean {

    @Inject
    private EntityManager em;

    public List<UserRemoval> getLastFinishedRemovals() {
        TypedQuery<UserRemoval> findRemovalsQuery = em.createNamedQuery(UserRemoval.FIND_FINISHED_REMOVALS, UserRemoval.class);
        findRemovalsQuery.setParameter("jobStart", getLastJobStart());

        return findRemovalsQuery.getResultList();
    }

    private Timestamp getLastJobStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Timestamp(System.currentTimeMillis()));
        calendar.add(Calendar.DAY_OF_WEEK, -7);

        return new Timestamp(calendar.getTime().getTime());
    }
}
