package org.jboss.set.mjolnir.archive.batch;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Named;
import java.util.Arrays;

/**
 * Reads LDAP username and returns list of git repositories URLs that needs to be backed up.
 *
 * // TODO
 */
@Named
public class LookupUserRepositoriesProcessor implements ItemProcessor {
    @Override
    public Object processItem(Object item) throws Exception {
        String username = (String) item;

        return Arrays.asList(
                "http://localhost/" + username + "/repo1",
                "http://localhost/" + username + "/repo2",
                "http://localhost/" + username + "/repo3"
        );
    }
}
