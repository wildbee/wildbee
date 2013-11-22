# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "allowed_statuses" ("id" UUID NOT NULL PRIMARY KEY,"workflow" VARCHAR(254) NOT NULL,"state" VARCHAR(254) NOT NULL,"next_state" VARCHAR(254) NOT NULL);
create table "package_statuses" ("id" UUID NOT NULL PRIMARY KEY,"task_id" UUID NOT NULL,"task" VARCHAR(254) NOT NULL,"status" VARCHAR(254) NOT NULL);
create table "packages" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"task_id" UUID NOT NULL,"creator_id" UUID NOT NULL,"assignee_id" UUID NOT NULL,"cc_list" VARCHAR(254) DEFAULT 'None' NOT NULL,"status" VARCHAR(254) NOT NULL,"os_version" VARCHAR(254) NOT NULL,"creation_time" TIMESTAMP NOT NULL,"last_updated" TIMESTAMP NOT NULL);
create table "tasks" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"owner_id" UUID NOT NULL,"creation_time" TIMESTAMP NOT NULL,"last_updated" TIMESTAMP NOT NULL);
create unique index "idx_name" on "tasks" ("name");
create table "users" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL);
create unique index "idx_email" on "users" ("email");
create table "workflows" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL);
create unique index "idx_name" on "workflows" ("name");
alter table "packages" add constraint "creator_fk" foreign key("creator_id") references "users"("id") on update NO ACTION on delete NO ACTION;
alter table "packages" add constraint "assignee_fk" foreign key("assignee_id") references "users"("id") on update NO ACTION on delete NO ACTION;
alter table "packages" add constraint "task_fk" foreign key("task_id") references "tasks"("id") on update NO ACTION on delete NO ACTION;
alter table "tasks" add constraint "owner_fk" foreign key("owner_id") references "users"("id") on update NO ACTION on delete NO ACTION;
alter table "workflows" add constraint "status_fk" foreign key("name") references "allowed_statuses"("workflow") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "packages" drop constraint "creator_fk";
alter table "packages" drop constraint "assignee_fk";
alter table "packages" drop constraint "task_fk";
alter table "tasks" drop constraint "owner_fk";
alter table "workflows" drop constraint "status_fk";
drop table "allowed_statuses";
drop table "package_statuses";
drop table "packages";
drop table "tasks";
drop table "users";
drop table "workflows";

