# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "allowed_statuses" ("id" SERIAL NOT NULL PRIMARY KEY,"task" VARCHAR(254) NOT NULL,"status" VARCHAR(254) NOT NULL);
create table "tasks" ("id" SERIAL NOT NULL PRIMARY KEY,"owner_id" BIGINT NOT NULL,"task" VARCHAR(254) NOT NULL,"creation_time" TIMESTAMP NOT NULL,"last_updated" TIMESTAMP NOT NULL);
create table "users" ("id" SERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL,"admin" BOOLEAN DEFAULT false NOT NULL);
create table "workflows" ("id" SERIAL NOT NULL PRIMARY KEY,"task_id" BIGINT NOT NULL,"state" VARCHAR(254) NOT NULL,"next_state" VARCHAR(254) NOT NULL);
alter table "tasks" add constraint "fk_owner" foreign key("owner_id") references "users"("id") on update NO ACTION on delete NO ACTION;
alter table "tasks" add constraint "fk_status" foreign key("task") references "allowed_statuses"("task") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "tasks" drop constraint "fk_owner";
alter table "tasks" drop constraint "fk_status";
drop table "allowed_statuses";
drop table "tasks";
drop table "users";
drop table "workflows";

