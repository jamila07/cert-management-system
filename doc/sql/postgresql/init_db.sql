
create database toy;

create user camanager with encrypted password 'camanager';
create schema minica;

GRANT CONNECT ON DATABASE toy TO camanager;
alter schema minica owner to camanager;

select current_schema();
show search_path();

alter role camanager set search_path to minica

-- 사용자 저장 테이블
create table user_info (
	id character varying(24), -- 사용자 ID
	name character varying(16), -- 사용자 이름
	add_date timestamp without time zone, -- 가입 일자
	depart_team character varying(32), -- 부서/팀
	job_level character varying(24), -- 직위/직무
	e_mail character varying(80), -- email 주소
	password character varying(512), -- hashed 비밀번호	
	state integer, --0: 최고관리자, 1: 유저, 10: 관리자 삭제, 11: 유저 삭제
	primary key (id)
);

CREATE SEQUENCE applied_user_info_id_seq START 1;
create table applied_user_info (
	seq_id integer default nextval('applied_user_info_id_seq'),
	user_id character varying(24),
	password character varying(512),
	add_date timestamp without time zone,
	name character varying(16),
	depart_team character varying(32), 
	job_level character varying(24),
	e_mail character varying(80),
	group_creator boolean,
	group_id integer,
	group_name character varying(40),
	solution_name character varying(40),
	group_description character varying(128),
	state integer, -- 0:대기, 1:성공, 2:거절, 3: 수락, 4: 만료
	primary key(seq_id)
);

-- 사용자 메일 인증 관련
create sequence applied_user_mail_id_seq start 1;
create table applied_user_mail(
	seq_id integer default nextval('applied_user_mail_id_seq'),
	send_date timestamp without time zone,
	expired_date timestamp without time zone,
	auth_uri character varying(256) unique,
	applied_user_info_seq_id integer,
	activated_state integer, -- 0:activated 1: not activated 2: expired
	state integer-- 0: 정상적인
);


-- 인증서 저장 테이블
CREATE SEQUENCE cert_id_seq START 1;
create table cert (
	id integer DEFAULT nextval('cert_id_seq'::regclass),
	serial_number integer,
	file bytea,
	issuing_request_date timestamp without time zone,
	start_date timestamp without time zone,
	end_date timestamp without time zone,
	issuer character varying(24),
	subject character varying(24),
	subject_dn bytea,
	description character varying(256),
	key_id integer,
	type integer, -- 0: RootCA, 1:intermediateCA 2: EE Certificate
	ou_type integer,
	primary key (id)
);

--암호키 테이블
CREATE SEQUENCE keypair_id_seq START 1;
create table keypair( 
	id integer DEFAULT nextval('keypair_id_seq'::regclass),
	key_identifier bytea,
	public_key bytea,
	private_key bytea,
	primary key (id)
);

-- 인증서 감사로그 테이블
CREATE SEQUENCE certaudit_id_seq START 1;
create table cert_audit( 
	id integer DEFAULT nextval('certaudit_id_seq'::regclass),
	user_id character varying(24),
	request_param character varying(512),
	date timestamp without time zone,
	action integer,
	client_ip character varying(48),
	server_ip character varying(48),
	result integer,
	err_msg character varying(256),
	hash bytea,
	primary key (id)
);

-- 웹사이트 감사로그 테이블
CREATE SEQUENCE webaudit_id_seq START 1;
create table web_audit(
	id integer DEFAULT nextval('webaudit_id_seq'::regclass),
	user_id character varying(24),
	date timestamp without time zone,
	url character varying(512),
	param character varying(1024),
	rep_code integer,
	err_msg character varying(1024),
	client_ip character varying(48),
	server_ip character varying(48),
	hash bytea,
	primary key (id)
);

-- 그룹
create sequence group_id_seq start 1;
create table group_info(
	id integer default nextval('group_id_seq'),
	name character varying(40),
	alt_name character varying(56),
	create_date timestamp without time zone,
	creator character varying(24),
	state integer, -- 0: 유효 1: 정지 2: 폐기 3: 신청
	description character varying(128),
	primary key(id)
);

--유저 - 그룹
create table user_group(	
	user_id character varying(24),
	group_id integer,
	join_date timestamp without time zone,
	user_authority integer, -- 0: 마스터, 1: 서브마스터 2: 유저
	state integer, -- 0:가입신청, 1: 가입함
	primary key(user_id, group_id)
);

-- 그룹의 솔루션
CREATE SEQUENCE group_info_solution_seq START 1;
create table group_info_solution(
	seq_id integer default nextval('group_info_solution_seq'),
	group_id integer,
	solution_name character varying(40),
	create_date timestamp without time zone,
	creator character varying(24),
	state integer, -- 0: 유효 1: 정지 2: 폐기  3: 솔루션만 신청 4: 그룹과 같이 신청 
	primary key(seq_id)
);


-- 고민
--alter table cert add constraint FK13F4AF379D2ADF1 foreign key (subject) references user_info(id);
--alter table cert add constraint FK13F4AF379D2ADF2 foreign key (issuer) references user_info(id);
--alter table group_info add constraint FK13F4AF379D2ADF6 foreign key (creator) references user_info(id);


alter table cert add constraint FK13F4AF379D2ADF3 foreign key (key_id) references keypair(id);
alter table cert add constraint FK13F4AF379D2ADF8 foreign key (ou_type) references group_info(id);
alter table user_group add constraint FK13F4AF379D2ADF4 foreign key (user_id) references user_info(id);
alter table user_group add constraint FK13F4AF379D2ADF5 foreign key (group_id) references group_info(id);

alter table group_info_solution add constraint FK13F4AF379D2ADF7 foreign key (group_id) references group_info(id);

alter table applied_user_mail add constraint FK13F4AF379D2ADF8 foreign key (applied_user_info_seq_id) references  applied_user_info(seq_id);