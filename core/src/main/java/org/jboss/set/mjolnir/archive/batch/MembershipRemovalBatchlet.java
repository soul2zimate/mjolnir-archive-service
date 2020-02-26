package org.jboss.set.mjolnir.archive.batch;

import jdk.net.SocketFlow;
import org.eclipse.egit.github.core.Repository;
import org.jboss.logging.Logger;
import org.jboss.set.mjolnir.archive.ArchivingBean;
import org.jboss.set.mjolnir.archive.GitHubDiscoveryBean;
import org.jboss.set.mjolnir.archive.domain.RemovalStatus;
import org.jboss.set.mjolnir.archive.domain.UserRemoval;

import javax.batch.api.AbstractBatchlet;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Named
public class MembershipRemovalBatchlet extends AbstractBatchlet {

    private Logger logger = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @Inject
    private GitHubDiscoveryBean discoveryBean;

    @Inject
    private ArchivingBean archivingBean;

    @Override
    public String process() throws Exception {
        // obtain list of users we want to remove the access rights from
        List<UserRemoval> removals = loadRemovalsToProcess();

        if (removals.size() > 0) {
            // obtain list of monitored GitHub organizations & teams

//        Set<Repository> repositoriesToArchive = discoveryBean.getRepositoriesToArchive("jbossas", "TomasHofman");
//        logger.infof("Found repositories to archive: %s", repositoriesToArchive);


//        archivingBean.createRepositoryMirror("sample/repo/url");
        }

        return "DONE";
    }

    List<UserRemoval> loadRemovalsToProcess() {
        // perform in transaction to avoid removals being loaded by two parallel executions
        em.getTransaction().begin();

        Query findRemovalsQuery = em.createNamedQuery(UserRemoval.FIND_REMOVALS_TO_PROCESS);
        //noinspection unchecked
        List<UserRemoval> removals = findRemovalsQuery.getResultList();
        List<Long> removalIds = removals.stream().map(UserRemoval::getId).collect(Collectors.toList());

        /*Query markRemovalsQuery = em.createNamedQuery(UserRemoval.MARK_STARTED_REMOVALS);
        markRemovalsQuery.setParameter("removalIds", removalIds);
        int updatedRows = markRemovalsQuery.executeUpdate();
        logger.infof("Updated rows: %d", updatedRows);*/

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        for (UserRemoval removal : removals) {
            removal.setStarted(timestamp);
            removal.setStatus(RemovalStatus.STARTED);
            em.persist(removal);
        }

        em.getTransaction().commit();

        return removals;
    }

}
