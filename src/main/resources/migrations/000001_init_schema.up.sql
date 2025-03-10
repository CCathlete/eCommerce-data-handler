-- create table
--   raw_data (
--     id int auto_increment primary KEY,
--     source varchar(255) not null,
--     data json not null,
--     created_at timestamp default current_timestamp
--   )
-- ;
create table
  processed_data (
    id int auto_increment primary key,
    raw_data_id int not null,
    transformed_data json not null,
    status enum ('pending', 'success', 'failed') not null,
    processed_at timestamp default current_timestamp
  )
;