package org.jboss.set.mjolnir.archive;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Defines basic operations for fetching remote repositories.
 */
public class GitArchiveRepository {

    private Git git;

    private CredentialsProvider credentialsProvider;

    public GitArchiveRepository(String parentRepositoryUrl, File repositoryDirectory, CredentialsProvider credentialsProvider)
            throws IOException, GitAPIException {
        this.credentialsProvider = credentialsProvider;
        this.git = repositoryDirectory.exists() ?  gitOpen(repositoryDirectory) : gitClone(repositoryDirectory, parentRepositoryUrl);
    }

    private Git gitClone(File directory, String originUrl) throws GitAPIException {
         return Git.cloneRepository()
                .setCredentialsProvider(credentialsProvider)
                .setURI(originUrl)
                .setMirror(true)
                .setDirectory(directory)
                .call();
    }

    private Git gitOpen(File directory) throws IOException {
        return Git.open(directory);
    }

    public void gitAddRemote(String userName, String originUrl) throws URISyntaxException, GitAPIException {
        git.remoteAdd()
                .setName(userName)
                .setUri(new URIish(originUrl))
                .call();
    }

    public void gitFetch(String userName) throws GitAPIException {
        git.fetch()
                .setCredentialsProvider(credentialsProvider)
                .setRemote(userName)
                .setTagOpt(TagOpt.FETCH_TAGS)
                .call();
    }
}
