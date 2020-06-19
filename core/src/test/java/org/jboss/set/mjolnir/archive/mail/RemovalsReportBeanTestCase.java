package org.jboss.set.mjolnir.archive.mail;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.jboss.set.mjolnir.archive.domain.RemovalStatus;
import org.jboss.set.mjolnir.archive.domain.UserRemoval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.jboss.set.mjolnir.archive.util.TestUtils.createUserRemoval;

@RunWith(CdiTestRunner.class)
public class RemovalsReportBeanTestCase {

    @Inject
    private EntityManager em;

    @Inject
    private RemovalsReportBean removalsReportBean;

    @Before
    public void setup() throws IllegalAccessException, NoSuchFieldException {
        em.getTransaction().begin();

        Timestamp now = new Timestamp(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_WEEK, -7);

        Timestamp moreThanWeekAgo = new Timestamp(calendar.getTime().getTime());

        UserRemoval userRemoval = createUserRemoval("lvydra", now, now, RemovalStatus.COMPLETED);
        em.persist(userRemoval);

        //started before week, but ended, in current interval
        userRemoval = createUserRemoval("user1", moreThanWeekAgo, now, RemovalStatus.COMPLETED);
        em.persist(userRemoval);

        //started and completed in previous interval should be excluded
        userRemoval = createUserRemoval("user2", moreThanWeekAgo, moreThanWeekAgo, RemovalStatus.COMPLETED);
        em.persist(userRemoval);

        //examples ending with failure
        userRemoval = createUserRemoval("user3", now, now, RemovalStatus.FAILED);
        em.persist(userRemoval);

        userRemoval = createUserRemoval("user4", moreThanWeekAgo, now, RemovalStatus.FAILED);
        em.persist(userRemoval);

        //should be excluded
        userRemoval = createUserRemoval("user5", moreThanWeekAgo, moreThanWeekAgo, RemovalStatus.FAILED);
        em.persist(userRemoval);

        userRemoval = createUserRemoval("user6", now, now, RemovalStatus.UNKNOWN_USER);
        em.persist(userRemoval);

        userRemoval = createUserRemoval("user7", moreThanWeekAgo, now, RemovalStatus.UNKNOWN_USER);
        em.persist(userRemoval);

        //excluded
        userRemoval = createUserRemoval("user8", moreThanWeekAgo, moreThanWeekAgo, RemovalStatus.UNKNOWN_USER);
        em.persist(userRemoval);

        //still running should be excluded
        userRemoval = createUserRemoval("user9", now, now, RemovalStatus.STARTED);
        em.persist(userRemoval);

        userRemoval = createUserRemoval("user10", moreThanWeekAgo, moreThanWeekAgo, RemovalStatus.STARTED);
        em.persist(userRemoval);

        em.getTransaction().commit();
    }

    @Test
    public void testGetRemovalsToReport() {
        List<UserRemoval> lastFinishedRemovals = removalsReportBean.getLastFinishedRemovals();

        assertThat(lastFinishedRemovals.size()).isEqualTo(6);
        assertThat(lastFinishedRemovals)
                .extracting("ldapUsername", "status")
                .containsOnly(
                        tuple("user6", RemovalStatus.UNKNOWN_USER),
                        tuple("user7", RemovalStatus.UNKNOWN_USER),
                        tuple("user3", RemovalStatus.FAILED),
                        tuple("user4", RemovalStatus.FAILED),
                        tuple("lvydra", RemovalStatus.COMPLETED),
                        tuple("user1", RemovalStatus.COMPLETED));
    }
}
