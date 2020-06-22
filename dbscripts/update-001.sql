alter table user_removals add column ldap_username varchar(255);
alter table user_removals add column github_username varchar(255);
update user_removals set ldap_username = username;
