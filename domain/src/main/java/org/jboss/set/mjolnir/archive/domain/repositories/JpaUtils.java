package org.jboss.set.mjolnir.archive.domain.repositories;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class JpaUtils {

    /**
     * Converts JPA query result to Optional
     */
    public static <T> Optional<T> findSingleResult(TypedQuery<T> query) {
        List<T> resultList = query.getResultList();
        if (resultList.size() == 0) {
            return Optional.empty();
        } else if (resultList.size() == 1) {
            return Optional.of(resultList.get(0));
        } else {
            throw new IllegalStateException("Expected single result but got several.");
        }
    }
}
