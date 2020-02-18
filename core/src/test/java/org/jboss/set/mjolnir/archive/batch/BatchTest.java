package org.jboss.set.mjolnir.archive.batch;

import org.junit.Test;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class BatchTest {

    private static final int MAX_TRIES = 40;
    private static final int THREAD_SLEEP = 1000;

    @Test
    public void givenChunk_thenBatch_completesWithSuccess() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        long executionId = jobOperator.start("simpleChunk", new Properties());
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
    }
}
