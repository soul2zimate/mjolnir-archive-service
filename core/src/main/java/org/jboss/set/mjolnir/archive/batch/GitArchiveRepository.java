package org.jboss.set.mjolnir.archive.batch;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class GitArchiveRepository {
    Git git;
    CredentialsProvider credentialsProvider;

    public GitArchiveRepository(String parentRepositoryUrl, File repositoryDirectory, CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;

        try {
            this.git = repositoryDirectory.exists() ?  gitOpen(repositoryDirectory) : gitClone(repositoryDirectory, parentRepositoryUrl);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
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
