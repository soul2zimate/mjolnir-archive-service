package org.jboss.mjolnir.archive.service.webapp.servlet;

import org.jboss.set.mjolnir.archive.mail.ReportScheduler;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Generates an email report containing a listing of performed user removals and information about unknown GH members.
 */
@WebServlet("/generate-email-report")
public class GenerateEmailReportServlet extends HttpServlet {

    @Inject
    private ReportScheduler reportBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            reportBean.sendMail();
            resp.setContentType("text/plain");
            try (PrintWriter writer = resp.getWriter()) {
                writer.println("OK");
            }
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
}
