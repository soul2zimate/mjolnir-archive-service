package org.jboss.set.mjolnir.archive.domain;

public enum RemovalStatus {

    /**
     * Request for user removal has been recorded.
     */
    NEW,

    /**
     * Picked up for processing.
     */
    STARTED,

    /**
     * User's repository forks has been discovered.
     */
    REPOSITORIES_DISCOVERED,

    /**
     * User's repository forks has been archived.
     */
    REPOSITORIES_ARCHIVED,

    /**
     * User's membership has been removed.
     */
    COMPLETED,

    /**
     * The removal process failed.
     */
    FAILED
}
