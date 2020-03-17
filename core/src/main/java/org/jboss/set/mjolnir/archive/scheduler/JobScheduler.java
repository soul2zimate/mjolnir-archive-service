package org.jboss.set.mjolnir.archive.scheduler;


import org.jboss.logging.Logger;
import org.jboss.set.mjolnir.archive.ldap.LdapScanningBean;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class JobScheduler {

    private final Logger logger = Logger.getLogger(getClass());

    @Inject
    private LdapScanningBean ldapScanningBean;


    @Schedule(hour = "3", persistent = false)
    public void ldapScan() {
        logger.infof("Starting scheduled job ldapScan");
        ldapScanningBean.createRemovalsForUsersWithoutLdapAccount();
    }
}
