package org.jboss.set.mjolnir.archive;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.jboss.set.mjolnir.archive.domain.RemovalStatus;
import org.jboss.set.mjolnir.archive.domain.UserRemoval;
import org.jboss.set.mjolnir.archive.mail.MailBodyMessageProducer;
import org.jboss.set.mjolnir.archive.mail.MailingService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(CdiTestRunner.class)
public class ArchivingReportGeneratorTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Inject
    private EntityManager em;

    @Inject
    private ArchivingReportGenerator archivingReportGenerator;

    @Inject
    private MailingService mailingService;

    @Inject
    private MailBodyMessageProducer mailBodyMessageProducer;

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
        userRemoval.setStatus(RemovalStatus.COMPLETED);
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("user2");
        userRemoval.setStarted(now);
        userRemoval.setStatus(RemovalStatus.COMPLETED);
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("user3");
        userRemoval.setStarted(now);
        userRemoval.setStatus(RemovalStatus.FAILED);
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("user4");
        userRemoval.setStarted(now);
        userRemoval.setStatus(RemovalStatus.STARTED);
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("user5");
        userRemoval.setStarted(moreThanWeekAgo);
        userRemoval.setStatus(RemovalStatus.FAILED);
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("user6");
        userRemoval.setStarted(moreThanWeekAgo);
        userRemoval.setStatus(RemovalStatus.COMPLETED);
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("user7");
        userRemoval.setStarted(now);
        userRemoval.setStatus(RemovalStatus.UNKNOWN_USER);
        em.persist(userRemoval);

        em.getTransaction().commit();
    }

    @Test
    public void testGetRemovalsToReport() {
        TypedQuery<UserRemoval> findRemovalsQuery = em.createNamedQuery(UserRemoval.FIND_REMOVALS_TO_PROCESS, UserRemoval.class);
        List<UserRemoval> removals = findRemovalsQuery.getResultList();

        List<UserRemoval> removalsByReportGenerator = archivingReportGenerator.getLastFinishedRemovals();
        assertThat(removalsByReportGenerator.size()).isEqualTo(5);
    }

    @Test
    public void testMailingService() {
        mailingService.setFrom("helpsystem@seznam.cz");
        mailingService.setTo("lvydra@redhat.com");
        mailingService.setSubject("test");

        mailingService.setBody(mailBodyMessageProducer.composeMessageBody());

        try {
            mailingService.send();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
