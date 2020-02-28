package org.jboss.set.mjolnir.archive.batch;

import org.eclipse.egit.github.core.Repository;
import org.jboss.logging.Logger;
import org.jboss.set.mjolnir.archive.ArchivingBean;
import org.jboss.set.mjolnir.archive.GitHubDiscoveryBean;

import javax.batch.api.AbstractBatchlet;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.Set;

@Named
public class MembershipRemovalBatchlet extends AbstractBatchlet {

    private Logger logger = Logger.getLogger(getClass());

    @Inject
    private EntityManager entityManager;

    @Inject
    private GitHubDiscoveryBean discoveryBean;

    @Inject
    private ArchivingBean archivingBean;

    @Override
    public String process() throws Exception {

        // obtain list of monitored GitHub organizations & teams

        // obtain list of users we want to remove the access rights from


//        Set<Repository> repositoriesToArchive = discoveryBean.getRepositoriesToArchive("jbossas", "TomasHofman");
//        logger.infof("Found repositories to archive: %s", repositoriesToArchive);


//        archivingBean.createRepositoryMirror("sample/repo/url");

        return "DONE";
    }

}
