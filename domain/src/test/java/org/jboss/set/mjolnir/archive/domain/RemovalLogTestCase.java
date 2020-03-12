package org.jboss.set.mjolnir.archive.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RemovalLogTestCase {

    @Test
    public void testSettingStackTrace() {
        RemovalLog log = new RemovalLog();

        try {
            try {
                throw new Exception("Cause");
            } catch (Exception e) {
                throw new Exception("Wrapping", e);
            }
        } catch (Exception e) {
            log.setStackTrace(e);
        }

        assertThat(log.getStackTrace())
                .contains("Caused by:")
                .contains("Exception: Wrapping")
                .contains("Exception: Cause");
    }
}
