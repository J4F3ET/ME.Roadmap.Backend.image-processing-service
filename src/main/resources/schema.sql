create table if not exists users (
    id serial primary key,
    username varchar(255) not null unique,
    password varchar(255) not null,
    is_enabled boolean not null,
    is_account_no_expired boolean not null,
    is_account_no_locked boolean not null,
    is_credentials_no_expired boolean not null
);

create table if not exists image (
    id serial primary key,
    imageName varchar(255) not null,
    imagePath varchar(255) not null,
    format varchar(4) not null,
    userId int not null references users(id),
    createdAt timestamp not null,
    updatedAt timestamp not null
);