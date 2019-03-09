-- key owner
create table ext_kms_key_owner(
    id integer,
	key_uid character varying(48),
    user_uid character varying(48),
    create_date timestamp without time zone,
    state integer,
    primary key(id)
);

-- 그룹
create sequence ext_kms_group_id_seq start 1;
create table ext_kms_group_info(
	id integer default nextval('ext_kms_group_id_seq'),
	name character varying(40),
	alt_name character varying(56),
	create_date timestamp without time zone,
	creator character varying(24),
	state integer,
	description character varying(128),
	primary key(id)
);

--유저 그룹
create table ext_kms_user_group(	
	user_id character varying(24),
	group_id integer,
	join_date timestamp without time zone,
	user_authority integer,
	state integer,
	primary key(user_id, group_id)
);

alter table ext_kms_user_group add constraint FK13F4AF379D3ADF1 foreign key (group_id) references ext_kms_group_info(id);