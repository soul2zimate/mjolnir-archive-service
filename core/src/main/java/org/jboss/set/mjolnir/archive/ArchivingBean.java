package org.jboss.set.mjolnir.archive;

import org.jboss.logging.Logger;

public class ArchivingBean {

    private Logger logger = Logger.getLogger(getClass());

    public void createRepositoryMirror(String repoUrl) {
        logger.infof("Archiving %s", repoUrl);
    }
}
