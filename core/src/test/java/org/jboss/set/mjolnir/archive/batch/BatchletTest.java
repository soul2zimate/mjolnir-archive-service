package org.jboss.set.mjolnir.archive.batch;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@RunWith(CdiTestRunner.class)
public class BatchletTest {

    @Inject
    private EntityManager em;

    @Inject
    private MembershipRemovalBatchlet batchlet;

    @Test
    public void testBatchlet() throws Exception {
        // TODO: this is just showcase

        Assert.assertNotNull(em);
        Assert.assertNotNull(batchlet);

        batchlet.process();

        Query query = em.createNativeQuery("select * from user_removals");
//        Query query = em.createNamedQuery(UserRemoval.FIND_REMOVALS_TO_PROCESS);
        Assert.assertEquals(0, query.getResultList().size());

        query = em.createQuery("SELECT r FROM UserRemoval r WHERE r.started IS NULL");
        Assert.assertEquals(0, query.getResultList().size());
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
