# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "tasks" ("ID" SERIAL NOT NULL PRIMARY KEY,"OWNER" VARCHAR(254) NOT NULL,"CREATION_TIME" TIMESTAMP NOT NULL,"LAST_UPDATED" TIMESTAMP NOT NULL);
create table "users" ("ID" SERIAL NOT NULL PRIMARY KEY,"NAME" VARCHAR(254) NOT NULL,"EMAIL" VARCHAR(254) NOT NULL,"ADMIN" BOOLEAN DEFAULT false NOT NULL);

# --- !Downs

drop table "tasks";
drop table "users";

