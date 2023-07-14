create sequence global_seq start 100000 increment 1;

    create table meal (
       id int4 not null,
        calories int4 not null,
        date_time timestamp not null,
        description varchar(255) not null,
        user_id int4 not null,
        primary key (id)
    );

    create table user_role (
       user_id int4 not null,
        role varchar(255)
    );

    create table users (
       id int4 not null,
        name varchar(255) not null,
        calories_per_day int default 2000 not null,
        email varchar(255) not null,
        enabled bool default true not null,
        password varchar(255) not null,
        registered timestamp default now() not null,
        primary key (id)
    );

    alter table if exists meal 
       add constraint meals_unique_user_datetime_idx unique (user_id, date_time);

    alter table if exists user_role 
       add constraint uk_user_role unique (user_id, role);

    alter table if exists users 
       add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email);

    alter table if exists meal 
       add constraint FK9t9sjb13rhel3nw6uqk9jd0c7 
       foreign key (user_id) 
       references users 
       on delete cascade;

    alter table if exists user_role 
       add constraint FKj345gk1bovqvfame88rcx7yyx 
       foreign key (user_id) 
       references users 
       on delete cascade;
