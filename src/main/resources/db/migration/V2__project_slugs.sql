alter table projects add column slug varchar(150);
alter table projects alter column slug set not null;
alter table projects add constraint uq_projects_slug unique (slug);
