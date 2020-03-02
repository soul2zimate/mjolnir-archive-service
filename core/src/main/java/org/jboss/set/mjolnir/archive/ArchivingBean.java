package org.jboss.set.mjolnir.archive;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jboss.logging.Logger;
import org.jboss.set.mjolnir.archive.batch.GitArchiveRepository;

import javax.inject.Inject;
import java.io.*;
import java.net.URISyntaxException;

public class ArchivingBean {
  
    private Logger logger = Logger.getLogger(getClass());

    private UsernamePasswordCredentialsProvider credentialsProvider;

    @Inject
    public ArchivingBean(UsernamePasswordCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    /**
     * Create remote repository for given repository object
     *
     * @param repository object representing repository for archiving
     * @return void
     */
    public void createRepositoryMirror(String organization, Repository repository) throws GitAPIException, URISyntaxException {
        logger.infof("Archiving %s", repository.getCloneUrl());
        /** TODO
         * get parent directly from object
         */
        String parentUrl = repository.getCloneUrl().replace(repository.getOwner().getLogin(), organization);

        File organizationDirectory = new File(System.getProperty("user.home"), organization);
        File repositoryDirectory = new File(organizationDirectory, repository.getName());

        GitArchiveRepository gitArchive = new GitArchiveRepository(parentUrl, repositoryDirectory, credentialsProvider);

        gitArchive.gitAddRemote(repository.getOwner().getLogin(), repository.getCloneUrl());
        gitArchive.gitFetch(repository.getOwner().getLogin());
    }
}
