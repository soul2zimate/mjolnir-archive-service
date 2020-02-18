package org.jboss.set.mjolnir.archive.batch;

import javax.batch.api.chunk.AbstractItemReader;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Sample development item reader that reads returns usernames predefined if private array.
 *
 * Eventually we would extract usernames from a mailbox.
 */
@Named
public class ArrayItemReader extends AbstractItemReader {

    private static final String[] USERS = new String[] {
            "thofman",
            "bob",
            "fred"
    };

    private Integer idx;

    @Override
    public Object readItem() throws Exception {
        if (idx >= USERS.length) {
            return null;
        }
        return USERS[idx++];
    }

    @Override
    public void open(Serializable checkpoint) throws Exception {
        idx = 0;
    }
}
