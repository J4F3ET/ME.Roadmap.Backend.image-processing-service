create table if not exists users (
    id serial primary key,
    username varchar(255) not null,
    password varchar(255) not null,
    is_enabled boolean not null,
    is_account_no_expired boolean not null,
    is_account_no_locked boolean not null,
    is_credentials_no_expired boolean not null
);

create table if not exists image (
    id serial primary key,
    image_name varchar(255) not null,
    image_path varchar(255) not null,
    user_id int not null references users(id),
    created_at timestamp not null,
    updated_at timestamp not null
);