package org.jboss.mjolnir.archive.service.webapp.servlet;

import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Checks database connection.
 */
@WebServlet("/database-check")
public class DatabaseCheckServlet extends HttpServlet {

    private Logger logger = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        try (PrintWriter writer = resp.getWriter()) {
            try {
                Query query = em.createNativeQuery("select count(*) from application_parameters");
                query.getSingleResult();
                writer.println("OK");
            } catch (Exception e) {
                writer.println("ERROR: " + e.getMessage());
                logger.error(e);
            }
        }
    }
}
