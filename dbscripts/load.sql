insert into github_orgs values (1, 'testorg');

insert into github_teams values (1, 1, 'Test Team', 3624929);
insert into github_teams values (2, 1, 'Other Team', 3624949);

insert into application_parameters values ('github,token', '');

insert into users (krb_name, github_name) values ('thofman', 'TomasHofman');
