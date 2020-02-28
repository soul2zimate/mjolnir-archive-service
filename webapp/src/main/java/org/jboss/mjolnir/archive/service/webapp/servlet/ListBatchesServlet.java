package org.jboss.mjolnir.archive.service.webapp.servlet;

import org.jboss.logging.Logger;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/list-batches")
public class ListBatchesServlet extends HttpServlet {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        ServletOutputStream os = resp.getOutputStream();

        JobOperator jobOperator = BatchRuntime.getJobOperator();
        for (JobInstance jobInstance : jobOperator.getJobInstances(StartBatchServlet.JOB_NAME, 1, 40)) {
            os.println("JobInstance: " + jobInstance.getJobName() + " " + jobInstance.getInstanceId());
            for (JobExecution jobExecution : jobOperator.getJobExecutions(jobInstance)) {
                os.println("JobExecution: " + jobExecution.getJobName()
                        + "\t" + jobExecution.getExecutionId()
                        + "\t" + jobExecution.getBatchStatus()
                        + "\t" + jobExecution.getExitStatus()
                        + "\t" + jobExecution.getCreateTime()
                        + "\t" + jobExecution.getStartTime()
                        + "\t" + jobExecution.getEndTime()
                );
            }
        }
    }
}
