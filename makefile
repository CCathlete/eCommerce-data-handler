connect:
	source .env; sudo -E docker-compose exec db mysql -u ${DB_USER} -p"${DB_ROOT_PASS}"

# dbfile:
# 	source .env; docker cp /home/ccat/Repos/eCommerce-data-handler/migrations/down/$(file) db:/; docker-compose exec db mysql -U ${DB_USER} -d ${DB_NAME} -f $(file)

# migratedown:
# 	source .env; docker cp /home/ccat/Repos/eCommerce-data-handler/migrations/down/$(file) db:/; docker-compose exec db mysql -U ${DB_USER} -d ${DB_NAME} -f $(file)

migrateup:
	migrate -path src/main/resources/migrations -database "mysql://${DB_USER}:${DB_ROOT_PASS}@tcp(${DB_HOST}:${DB_PORT})/${DB_NAME}" -verbose up


migrateup_1: # Up one migration.
	migrate -path src/main/resources/migrations -database "mysql://${DB_USER}:${DB_ROOT_PASS}@tcp(${DB_HOST}:${DB_PORT})/${DB_NAME}" -verbose up 1

migratedown:
	migrate -path src/main/resources/migrations -database "mysql://${DB_USER}:${DB_ROOT_PASS}@tcp(${DB_HOST}:${DB_PORT})/${DB_NAME}" -verbose down

migratedown_1: # Down one migration back.
	migrate -path src/main/resources/migrations -database "mysql://${DB_USER}:${DB_ROOT_PASS}@tcp(${DB_HOST}:${DB_PORT})/${DB_NAME}" -verbose down 1

mc:
	sudo docker-compose exec minio mc $(args)

# Command aliasing is considered a "phony target" so it's possible to run it repeatedly.
.PHONY: connect mc migrateup migratedown migratedown_1 migrateup_1 #dbfile

