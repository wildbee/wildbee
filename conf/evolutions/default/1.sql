# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "tasks" ("uuid" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"owner_uuid" UUID NOT NULL);
create table "users" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL);
alter table "tasks" add constraint "owner_fk" foreign key("owner_uuid") references "users"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "tasks" drop constraint "owner_fk";
drop table "tasks";
drop table "users";

