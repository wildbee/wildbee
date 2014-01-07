# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "packages" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"task_id" UUID NOT NULL,"creator_id" UUID NOT NULL,"assignee_id" UUID NOT NULL,"cc_list" VARCHAR(254) DEFAULT 'None' NOT NULL,"status" UUID NOT NULL,"os_version" VARCHAR(254) NOT NULL,"created" TIMESTAMP NOT NULL,"updated" TIMESTAMP NOT NULL);
create table "plugins" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"path" VARCHAR(254) NOT NULL,"pack" UUID NOT NULL);
create table "statuses" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL);
create unique index "idx_name_statuses" on "statuses" ("name");
create table "tasks" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"owner_id" UUID NOT NULL,"created" TIMESTAMP NOT NULL,"workflow_id" UUID NOT NULL,"updated" TIMESTAMP NOT NULL);
create unique index "idx_name_tasks" on "tasks" ("name");
create table "transitions" ("id" UUID NOT NULL PRIMARY KEY,"workflow" UUID NOT NULL,"state" UUID NOT NULL,"next_state" UUID NOT NULL);
create table "users" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL);
create unique index "idx_email" on "users" ("email");
create table "workflows" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"start_status" UUID NOT NULL);
create unique index "idx_name_workflows" on "workflows" ("name");
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
drop table "plugins";
drop table "statuses";
drop table "tasks";
drop table "transitions";
drop table "users";
drop table "workflows";

