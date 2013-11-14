# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "package" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"task_id" UUID NOT NULL,"creator_id" UUID NOT NULL,"assignee_id" UUID NOT NULL,"cc_list" VARCHAR(254) DEFAULT 'None' NOT NULL,"status" VARCHAR(254) NOT NULL,"os_version" VARCHAR(254) NOT NULL,"creation_time" TIMESTAMP NOT NULL,"last_updated" TIMESTAMP NOT NULL);
create table "task" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"owner_id" UUID NOT NULL);
create table "wildbee_user" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL);
alter table "package" add constraint "creator_fk" foreign key("creator_id") references "wildbee_user"("id") on update NO ACTION on delete NO ACTION;
alter table "package" add constraint "assignee_fk" foreign key("assignee_id") references "wildbee_user"("id") on update NO ACTION on delete NO ACTION;
alter table "package" add constraint "task_fk" foreign key("task_id") references "task"("id") on update NO ACTION on delete NO ACTION;
alter table "task" add constraint "owner_fk" foreign key("owner_id") references "wildbee_user"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "package" drop constraint "creator_fk";
alter table "package" drop constraint "assignee_fk";
alter table "package" drop constraint "task_fk";
alter table "task" drop constraint "owner_fk";
drop table "package";
drop table "task";
drop table "wildbee_user";

