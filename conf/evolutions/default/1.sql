# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "task" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"owner_id" UUID NOT NULL);
create table "wildbee_user" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL);
alter table "task" add constraint "owner_fk" foreign key("owner_id") references "wildbee_user"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "task" drop constraint "owner_fk";
drop table "task";
drop table "wildbee_user";

