package org.jboss.set.mjolnir.archive.batch;

import org.jboss.set.mjolnir.archive.ArchivingBean;

import javax.batch.api.AbstractBatchlet;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class MembershipRemovalBatchlet extends AbstractBatchlet {

    @Inject
    private ArchivingBean archivingBean;

    @Override
    public String process() throws Exception {

//        archivingBean.createRepositoryMirror("sample/repo/url");

        return "DONE";
    }

}
