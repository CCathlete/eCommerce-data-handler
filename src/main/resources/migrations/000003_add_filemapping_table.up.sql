create table
  file_mapping (
    id serial primary key,
    original_name varchar(255) not null,
    new_name varchar(255) not null
  )
;