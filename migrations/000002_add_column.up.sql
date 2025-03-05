alter table processed_data
add column quality_score decimal(5, 2) not null default 0.00
;