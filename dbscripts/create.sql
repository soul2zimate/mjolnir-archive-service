
create table user_removals (
    id bigint not null,
    completed timestamp,
    created timestamp,
    removeOn date,
    started timestamp,
    status varchar(255),
    username varchar(255),
    primary key (id)
);

create table repository_forks (
    id bigint not null,
    user_removal_id bigint not null,
    archived timestamp,
    created timestamp,
    repositoryName varchar(255),
    repositoryUrl varchar(255),
    sourceRepositoryName varchar(255),
    sourceRepositoryUrl varchar(255),
    primary key (id)
);

alter table repository_forks add constraint fk_repository_forks_user_removal_id foreign key (user_removal_id) references user_removals;
