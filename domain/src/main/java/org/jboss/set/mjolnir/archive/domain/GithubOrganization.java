package org.jboss.set.mjolnir.archive.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Martin Stefanko (mstefank@redhat.com)
 */
@Entity
@Table(name = "github_orgs")
public class GithubOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "github_orgs_generator")
    @SequenceGenerator(name = "github_orgs_generator", sequenceName = "sq_github_orgs", allocationSize = 1)
    private Long id;

    private String name;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
