alter table cert drop constraint FK13F4AF379D2ADF1;
alter table cert drop constraint FK13F4AF379D2ADF2;
alter table cert drop constraint FK13F4AF379D2ADF3;
alter table user_group drop constraint FK13F4AF379D2ADF4;
alter table user_group drop constraint FK13F4AF379D2ADF5;
alter table group_info drop constraint FK13F4AF379D2ADF6;
alter table group_info_solution drop constraint FK13F4AF379D2ADF7;
alter table applied_user_mail drop constraint FK13F4AF379D2ADF8;

drop table user_info;

drop table applied_user_info;
drop sequence applied_user_info_id_seq;

drop table applied_user_mail;
drop sequence applied_user_mail_id_seq;

drop table cert;
drop sequence cert_id_seq;

drop table keypair;
drop sequence keypair_id_seq;

drop table cert_audit;
drop sequence certaudit_id_seq;

drop table web_audit;
drop sequence webaudit_id_seq;

drop table group_info;
drop sequence group_id_seq;

drop table user_group;

drop table group_info_solution;
drop sequence group_info_solution_seq;