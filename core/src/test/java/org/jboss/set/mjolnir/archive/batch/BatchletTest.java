package org.jboss.set.mjolnir.archive.batch;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.jboss.set.mjolnir.archive.domain.UserRemoval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CdiTestRunner.class)
public class BatchletTest {

    @Inject
    private EntityManager em;

    @Inject
    private MembershipRemovalBatchlet batchlet;

    @Before
    public void setup() throws Exception {
        // create two sample removals

        // use transaction to enforce flush, just calling em.flush() throws an exception...
        em.getTransaction().begin();

        UserRemoval userRemoval = new UserRemoval();
        userRemoval.setUsername("thofman");
        em.persist(userRemoval);

        userRemoval = new UserRemoval();
        userRemoval.setUsername("lvydra");
        em.persist(userRemoval);

        em.getTransaction().commit();
    }

    @Test
    public void testRemovalsMarked() throws Exception {
        Query findRemovalsQuery = em.createNamedQuery(UserRemoval.FIND_REMOVALS_TO_PROCESS);
        //noinspection unchecked
        List<UserRemoval> removals = findRemovalsQuery.getResultList();
        assertThat(removals.size()).isEqualTo(2);

        batchlet.process();

        //noinspection unchecked
        removals = findRemovalsQuery.getResultList();
        assertThat(removals).isEmpty();
    }

    // TODO: running the whole batch would probably need to be tested by an arquillian test, as we would need
    //  the CDI functionality as well as the batch processing environment.

//    private static final int MAX_TRIES = 40;
//    private static final int THREAD_SLEEP = 1000;

    /*@Test
    public void testBatch() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        long executionId = jobOperator.start("membershipRemovalJob", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = keepTestAlive(jobExecution);
        assertEquals(jobExecution.getBatchStatus(), BatchStatus.COMPLETED);
    }

    public static JobExecution keepTestAlive(JobExecution jobExecution) throws InterruptedException {
        int maxTries = 0;
        while (!jobExecution.getBatchStatus()
                .equals(BatchStatus.COMPLETED)) {
            if (maxTries < MAX_TRIES) {
                maxTries++;
                Thread.sleep(THREAD_SLEEP);
                jobExecution = BatchRuntime.getJobOperator()
                        .getJobExecution(jobExecution.getExecutionId());
            } else {
                break;
            }
        }
        Thread.sleep(THREAD_SLEEP);
        return jobExecution;
    }*/
}
