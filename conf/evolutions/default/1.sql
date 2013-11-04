# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "cocktails" ("ID" BIGINT NOT NULL,"NAME" VARCHAR(254) NOT NULL,"beauty" VARCHAR(254) NOT NULL);

# --- !Downs

drop table "cocktails";

