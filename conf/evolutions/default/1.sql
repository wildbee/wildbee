# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "STATUSES" ("ID" SERIAL NOT NULL PRIMARY KEY,"STATUS" VARCHAR(254) NOT NULL);
create table "TASKS" ("ID" SERIAL NOT NULL PRIMARY KEY,"OWNER" BIGINT NOT NULL,"STATUS" VARCHAR(254) NOT NULL,"CREATION_TIME" TIMESTAMP NOT NULL,"LAST_UPDATED" TIMESTAMP NOT NULL);
create table "USERS" ("ID" SERIAL NOT NULL PRIMARY KEY,"NAME" VARCHAR(254) NOT NULL,"EMAIL" VARCHAR(254) NOT NULL,"ADMIN" BOOLEAN DEFAULT false NOT NULL);
alter table "TASKS" add constraint "fk_usr_location" foreign key("OWNER") references "USERS"("ID") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "TASKS" drop constraint "fk_usr_location";
drop table "STATUSES";
drop table "TASKS";
drop table "USERS";

