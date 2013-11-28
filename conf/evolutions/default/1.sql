# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "packages" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"task_id" UUID NOT NULL,"creator_id" UUID NOT NULL,"assignee_id" UUID NOT NULL,"cc_list" VARCHAR(254) DEFAULT 'None' NOT NULL,"status" UUID NOT NULL,"os_version" VARCHAR(254) NOT NULL,"creation_time" TIMESTAMP NOT NULL,"last_updated" TIMESTAMP NOT NULL);
create table "statuses" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL);
create unique index "idx_status_name" on "statuses" ("name");
create table "tasks" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"owner_id" UUID NOT NULL,"creation_time" TIMESTAMP NOT NULL,"workflow_id" UUID NOT NULL,"last_updated" TIMESTAMP NOT NULL);
create unique index "idx_name" on "tasks" ("name");
create table "transitions" ("id" UUID NOT NULL PRIMARY KEY,"workflow" UUID NOT NULL,"state" VARCHAR(254) NOT NULL,"next_state" VARCHAR(254) NOT NULL);
create table "users" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL);
create unique index "idx_email" on "users" ("email");
create table "workflows" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"start_status" UUID NOT NULL);
create unique index "idx_workflow_name" on "workflows" ("name");
alter table "packages" add constraint "assignee_fk" foreign key("assignee_id") references "users"("id") on update NO ACTION on delete NO ACTION;
alter table "packages" add constraint "creator_fk" foreign key("creator_id") references "users"("id") on update NO ACTION on delete NO ACTION;
alter table "packages" add constraint "task_fk" foreign key("task_id") references "tasks"("id") on update NO ACTION on delete NO ACTION;
alter table "tasks" add constraint "owner_fk" foreign key("owner_id") references "users"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "packages" drop constraint "assignee_fk";
alter table "packages" drop constraint "creator_fk";
alter table "packages" drop constraint "task_fk";
alter table "tasks" drop constraint "owner_fk";
drop table "packages";
drop table "statuses";
drop table "tasks";
drop table "transitions";
drop table "users";
drop table "workflows";

