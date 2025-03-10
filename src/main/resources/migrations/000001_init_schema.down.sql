drop table if exists processed_data
;

-- drop table if exists raw_data
-- ;
-- -- Declare variable for SQL statement
-- SET @sql_statement = '';
-- -- Generate the SQL to drop the foreign key if it exists
-- SELECT 
--     CONCAT('ALTER TABLE processed_data DROP FOREIGN KEY fk_raw_data_id') 
-- INTO @sql_statement
-- FROM information_schema.TABLE_CONSTRAINTS 
-- WHERE CONSTRAINT_SCHEMA = DATABASE() 
--   AND TABLE_NAME = 'processed_data' 
--   AND CONSTRAINT_NAME = 'fk_raw_data_id'
-- LIMIT 1;
-- -- Prepare and execute if the statement is not empty
-- PREPARE stmt FROM @sql_statement;
-- EXECUTE stmt;
-- DEALLOCATE PREPARE stmt;