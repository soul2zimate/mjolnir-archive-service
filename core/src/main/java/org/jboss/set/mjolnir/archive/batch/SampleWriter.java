package org.jboss.set.mjolnir.archive.batch;

import org.jboss.logging.Logger;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Named;
import java.util.List;

@Named
public class SampleWriter extends AbstractItemWriter {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public void writeItems(List<Object> items) throws Exception {
        for (Object item: items) {
            @SuppressWarnings("unchecked") List<String> repositories = (List<String>) item;

            for (String cloneUrl: repositories) {
                logger.infof("Writing %s", cloneUrl);
            }

        }
    }
}
