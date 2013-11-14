# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "tasks" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"owner_id" UUID NOT NULL);
create unique index "idx_name" on "tasks" ("name");
create table "users" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL);
create unique index "idx_email" on "users" ("email");
alter table "tasks" add constraint "owner_fk" foreign key("owner_id") references "users"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "tasks" drop constraint "owner_fk";
drop table "tasks";
drop table "users";

