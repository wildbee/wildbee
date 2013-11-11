# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "package" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"creator_id" UUID NOT NULL,"assignee_id" UUID NOT NULL,"cc_list" VARCHAR(254) NOT NULL,"status" VARCHAR(254) NOT NULL,"os_version" VARCHAR(254) NOT NULL,"creation_time" TIMESTAMP NOT NULL,"last_updated" TIMESTAMP NOT NULL);
create table "users" ("ID" SERIAL NOT NULL PRIMARY KEY,"NAME" VARCHAR(254) NOT NULL,"EMAIL" VARCHAR(254) NOT NULL);

# --- !Downs

drop table "package";
drop table "users";

