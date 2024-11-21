CREATE TABLE orders
(

    id BIGINT primary key generated by default as identity,
    status varchar(255) not null,
    address varchar(1000) not null,
    cancellable boolean default true,
    user_name varchar(50) not null,
    comment varchar(500),
    total_cost DOUBLE PRECISION not null,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE

);
