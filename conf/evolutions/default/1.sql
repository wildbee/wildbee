# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "tasks" ("ID" SERIAL NOT NULL PRIMARY KEY,"OWNER" BIGINT NOT NULL,"STATUS" VARCHAR(254) NOT NULL,"CREATION_TIME" TIMESTAMP NOT NULL,"LAST_UPDATED" TIMESTAMP NOT NULL);
create table "users" ("id" SERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL);
alter table "tasks" add constraint "fk_usr_location" foreign key("OWNER") references "users"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "tasks" drop constraint "fk_usr_location";
drop table "tasks";
drop table "users";

