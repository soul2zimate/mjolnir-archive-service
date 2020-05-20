package org.jboss.mjolnir.archive.service.webapp.servlet;

import org.jboss.set.mjolnir.archive.ldap.LdapScanningBean;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Retrieves all GH team members and creates removal records for members without active LDAP account.
 */
@WebServlet("/ldap-scan")
public class LdapScanningServlet extends HttpServlet {

    @Inject
    private LdapScanningBean ldapScanningBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ldapScanningBean.createRemovalsForUsersWithoutLdapAccount();

        resp.setContentType("text/plain");
        try (PrintWriter writer = resp.getWriter()) {
            writer.println("OK");
        }
    }
}
