create table quarkus-social;

CREATE TABLE public.users (
	id bigserial NOT NULL,
	"name" varchar(100) NOT NULL,
	age int4 NOT NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id)
);