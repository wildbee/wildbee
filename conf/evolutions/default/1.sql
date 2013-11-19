# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

<<<<<<< HEAD
create table "allowed_statuses" ("id" UUID NOT NULL PRIMARY KEY,"task_id" UUID NOT NULL,"task" VARCHAR(254) NOT NULL,"status" VARCHAR(254) NOT NULL);
create table "tasks" ("id" UUID NOT NULL PRIMARY KEY,"owner" VARCHAR(254) NOT NULL,"name" VARCHAR(254) NOT NULL,"creation_time" TIMESTAMP NOT NULL,"last_updated" TIMESTAMP NOT NULL);
create unique index "idx_name" on "tasks" ("name");
create table "users" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL);
create table "workflows" ("id" UUID NOT NULL PRIMARY KEY,"task_id" UUID NOT NULL,"state" VARCHAR(254) NOT NULL,"next_state" VARCHAR(254) NOT NULL);
alter table "tasks" add constraint "owner_fk" foreign key("owner") references "users"("name") on update NO ACTION on delete NO ACTION;
alter table "tasks" add constraint "fk_status" foreign key("name") references "allowed_statuses"("task") on update NO ACTION on delete NO ACTION;
||||||| merged common ancestors
create table "task" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"owner_id" UUID NOT NULL);
create table "wildbee_user" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL);
alter table "task" add constraint "owner_fk" foreign key("owner_id") references "wildbee_user"("id") on update NO ACTION on delete NO ACTION;
=======
create table "packages" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"task_id" UUID NOT NULL,"creator_id" UUID NOT NULL,"assignee_id" UUID NOT NULL,"cc_list" VARCHAR(254) DEFAULT 'None' NOT NULL,"status" VARCHAR(254) NOT NULL,"os_version" VARCHAR(254) NOT NULL,"creation_time" TIMESTAMP NOT NULL,"last_updated" TIMESTAMP NOT NULL);
create table "tasks" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"owner_id" UUID NOT NULL);
create unique index "idx_name" on "tasks" ("name");
create table "users" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL);
create unique index "idx_email" on "users" ("email");
alter table "packages" add constraint "assignee_fk" foreign key("assignee_id") references "users"("id") on update NO ACTION on delete NO ACTION;
alter table "packages" add constraint "creator_fk" foreign key("creator_id") references "users"("id") on update NO ACTION on delete NO ACTION;
alter table "packages" add constraint "task_fk" foreign key("task_id") references "tasks"("id") on update NO ACTION on delete NO ACTION;
alter table "tasks" add constraint "owner_fk" foreign key("owner_id") references "users"("id") on update NO ACTION on delete NO ACTION;
>>>>>>> origin/master

# --- !Downs

<<<<<<< HEAD
alter table "tasks" drop constraint "owner_fk";
alter table "tasks" drop constraint "fk_status";
drop table "allowed_statuses";
drop table "tasks";
drop table "users";
drop table "workflows";
||||||| merged common ancestors
alter table "task" drop constraint "owner_fk";
drop table "task";
drop table "wildbee_user";
=======
alter table "packages" drop constraint "assignee_fk";
alter table "packages" drop constraint "creator_fk";
alter table "packages" drop constraint "task_fk";
alter table "tasks" drop constraint "owner_fk";
drop table "packages";
drop table "tasks";
drop table "users";
>>>>>>> origin/master

