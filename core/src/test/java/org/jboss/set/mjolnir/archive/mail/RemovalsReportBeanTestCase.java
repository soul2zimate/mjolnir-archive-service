package org.jboss.set.mjolnir.archive.mail;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.jboss.set.mjolnir.archive.domain.RemovalStatus;
import org.jboss.set.mjolnir.archive.domain.UserRemoval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@RunWith(CdiTestRunner.class)
public class RemovalsReportBeanTestCase {

    @Inject
    private EntityManager em;

    @Inject
    private RemovalsReportBean removalsReportBean;

    @Before
    public void setup() {
        em.getTransaction().begin();

        Timestamp now = new Timestamp(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_WEEK, -7);

        Timestamp moreThanWeekAgo = new Timestamp(calendar.getTime().getTime());

        UserRemoval userRemoval = new UserRemoval();
        userRemoval.setUsername("thofman");
        userRemoval.setStarted(now);
        userRemoval.setStatus(RemovalStatus.COMPLETED);
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("lvydra");
        userRemoval.setStarted(now);
        userRemoval.setStatus(RemovalStatus.COMPLETED);
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("user1");
        userRemoval.setStarted(now);
        userRemoval.setStatus(RemovalStatus.FAILED);
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("user2");
        userRemoval.setStarted(now);
        userRemoval.setStatus(RemovalStatus.UNKNOWN_USER);
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("user3");
        userRemoval.setStarted(now);
        userRemoval.setStatus(RemovalStatus.STARTED);
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("user4");
        userRemoval.setStarted(moreThanWeekAgo);
        userRemoval.setStatus(RemovalStatus.FAILED);
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("user5");
        userRemoval.setStarted(moreThanWeekAgo);
        userRemoval.setStatus(RemovalStatus.COMPLETED);
        em.persist(userRemoval);

        em.getTransaction().commit();
    }

    @Test
    public void testGetRemovalsToReport() {
        List<UserRemoval> lastFinishedRemovals = removalsReportBean.getLastFinishedRemovals();

        assertThat(lastFinishedRemovals.size()).isEqualTo(4);
        assertThat(lastFinishedRemovals)
                .extracting("username", "status")
                .containsOnly(
                        tuple("user2", RemovalStatus.UNKNOWN_USER),
                        tuple("user1", RemovalStatus.FAILED),
                        tuple("lvydra", RemovalStatus.COMPLETED),
                        tuple("thofman", RemovalStatus.COMPLETED));
    }
}
