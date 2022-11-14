drop schema if exists fucking_great_advice cascade;
create schema fucking_great_advice;

alter schema fucking_great_advice owner to postgres;

SET search_path TO fucking_great_advice;

create table fucking_great_advice.great_advice
(
    id          serial not null primary key,
    text        varchar(255),
    html        varchar(500),
    tags        text[],
    conclusions text
);

alter table fucking_great_advice.great_advice
    owner to postgres;


SELECT setval('fucking_great_advice.great_advice_id_seq', 25000, true);  -- next value will be right after 25k

/* create unique index advice_id_uindex	on fucking_great_advice.great_advice (id); */
