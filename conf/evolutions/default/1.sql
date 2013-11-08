# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "allowed_statuses" ("uuid" SERIAL NOT NULL PRIMARY KEY,"task" VARCHAR(254) NOT NULL);
create table "statuses" ("uuid" SERIAL NOT NULL PRIMARY KEY,"status" VARCHAR(254) NOT NULL);
create table "tasks" ("id" SERIAL NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"status" VARCHAR(254) NOT NULL,"creation_time" TIMESTAMP NOT NULL,"last_updated" TIMESTAMP NOT NULL);
create table "users" ("id" SERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL,"admin" BOOLEAN DEFAULT false NOT NULL);
create table "workflows" ("uuid" SERIAL NOT NULL PRIMARY KEY,"task" VARCHAR(254) NOT NULL);
alter table "tasks" add constraint "fk_owner" foreign key("owner") references "users"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "tasks" drop constraint "fk_owner";
drop table "allowed_statuses";
drop table "statuses";
drop table "tasks";
drop table "users";
drop table "workflows";

