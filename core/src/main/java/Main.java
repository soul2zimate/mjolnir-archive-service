import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.jboss.set.mjolnir.archive.GitHubDiscoveryBean;

import java.io.IOException;
import java.util.Set;

/**
 * Sample main class that lists private repos of given GitHub user.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String oauthtoken = System.getenv("oauthtoken");
        if (oauthtoken == null) {
            throw new IllegalArgumentException("OAuth token is missing.");
        }

        GitHubClient gitHubClient = new GitHubClient();
        gitHubClient.setOAuth2Token(oauthtoken);
        GitHubDiscoveryBean discovery = new GitHubDiscoveryBean(gitHubClient);
//        Set<Repository> repositoriesToArchive = discovery.getRepositoriesToArchive("jbossas", "tomjenkinson");
        Set<Repository> repositoriesToArchive = discovery.getRepositoriesToArchive("jbossas", "TomasHofman");
        repositoriesToArchive.forEach(r -> System.out.println(r.getCloneUrl()));
    }
}
