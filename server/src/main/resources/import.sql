
CREATE SCHEMA if not exists chat;
set search_path to chat;
insert into users (username,password,nick) values ('admin','123','super-admin');