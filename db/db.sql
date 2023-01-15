create DATEBASE quarkus-social;

CREATE TABLE public.users (
	id bigserial NOT NULL,
	"name" varchar(100) NOT NULL,
	age int4 NOT NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE public.POSTS (
    id bigserial NOT NULL,
    post_text varchar(140) NOT NULL,
    dateTime timestamp not null,
    user_id bigint NOT NULL references public.users(id),
    CONSTRAINT POSTS_pkey PRIMARY KEY (id));

CREATE TABLE public.FOLLOWERS (
    id bigserial NOT NULL,
    user_id bigint NOT NULL references public.users(id),
    follower_id bigint NOT NULL references public.users(id),
    CONSTRAINT FOLLOWERS_pkey PRIMARY KEY (id));