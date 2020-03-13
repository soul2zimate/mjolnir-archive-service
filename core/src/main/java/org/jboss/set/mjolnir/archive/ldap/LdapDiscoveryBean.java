package org.jboss.set.mjolnir.archive.ldap;

import org.jboss.set.mjolnir.archive.configuration.Configuration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides querying of LDAP directory server.
 */
public class LdapDiscoveryBean {

    private static final int GROUPING_FACTOR = 50; // query for that many users at a time

    @Inject
    private Configuration configuration;

    private LdapClient ldapClient;

    @PostConstruct
    public void init() {
        // fetch ldap url
        final String ldapUrl = configuration.getLdapUrl();
        ldapClient = new LdapClient(ldapUrl);
    }

    /**
     * Verifies that given UID exists in LDAP database.
     */
    public boolean checkUserExists(String uid) throws NamingException {
        final NamingEnumeration<SearchResult> results =
                ldapClient.search(configuration.getLdapSearchContext(), "uid=" + uid);
        final boolean found = results.hasMore();
        results.close();
        return found;
    }

    /**
     * Verifies which of given UIDs exists in LDAP database.
     *
     * @param users list of users to check
     * @return map where keys are UIDs and values are booleans indicating if given UID exists or not
     */
    public Map<String, Boolean> checkUsersExists(Set<String> users) throws NamingException {
        final Map<String, Boolean> result = new HashMap<>();
        final Iterator<String> iterator = users.iterator();
        final List<String> tempUserList = new ArrayList<>(GROUPING_FACTOR);
        while (iterator.hasNext()) {
            tempUserList.add(iterator.next());
            if (tempUserList.size() >= GROUPING_FACTOR || !iterator.hasNext()) {
                final Map<String, Boolean> tempResultMap = checkUsersSubsetExists(tempUserList);
                result.putAll(tempResultMap);
                tempUserList.clear();
            }
        }
        return result;
    }

    private Map<String, Boolean> checkUsersSubsetExists(List<String> users) throws NamingException {
        // build a query
        final StringBuilder query = new StringBuilder("(|");
        for (String uid : users) {
            query.append("(uid=")
                    .append(uid)
                    .append(")");
        }
        query.append(")");

        final NamingEnumeration<SearchResult> searchResults =
                ldapClient.search(configuration.getLdapSearchContext(), query.toString());

        // fill the result map with found users
        final Map<String, Boolean> result = new HashMap<>();
        while (searchResults.hasMore()) {
            final SearchResult next = searchResults.next();
            String uid = (String) next.getAttributes().get("uid").get();
            result.put(uid, true);
        }
        searchResults.close();

        // fill the result map with users that weren't found
        for (String uid : users) {
            if (!result.containsKey(uid)) {
                result.put(uid, false);
            }
        }

        return result;
    }

}
