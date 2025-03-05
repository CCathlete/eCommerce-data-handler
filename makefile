connect:
	source .env; sudo -E docker-compose exec db mysql -u ${DB_USER} -p"${DB_ROOT_PASS}"

# dbfile:
# 	source .env; docker cp /home/ccat/Repos/eCommerce-data-handler/migrations/down/@{1} db:/; docker-compose exec db mysql -U ${DB_USER} -d ${DB_NAME} -f @{1}

# migratedown:
# 	source .env; docker cp /home/ccat/Repos/eCommerce-data-handler/migrations/down/@{1} db:/; docker-compose exec db mysql -U ${DB_USER} -d ${DB_NAME} -f @{1}

migrateup:
	migrate -path migrations -database "mysql://${DB_USER}:${DB_ROOT_PASS}@tcp(${DB_HOST}:${DB_PORT})/${DB_NAME}" -verbose up


migrateup_1: # Up one migration.
	migrate -path migrations -database "mysql://${DB_USER}:${DB_ROOT_PASS}@tcp(${DB_HOST}:${DB_PORT})/${DB_NAME}" -verbose up 1

migratedown:
	migrate -path migrations -database "mysql://${DB_USER}:${DB_ROOT_PASS}@tcp(${DB_HOST}:${DB_PORT})/${DB_NAME}" -verbose down

migratedown_1: # Down one migration back.
	migrate -path migrations -database "mysql://${DB_USER}:${DB_ROOT_PASS}@tcp(${DB_HOST}:${DB_PORT})/${DB_NAME}" -verbose down 1

# sqlc:
# 	sqlc generate -f db/sqlc.yaml

# test:
# 	go test -v -cover ./...

# migraterestart:
# 	make migratedown; make migrateup

# server:
# 	go run cmd/main.go

# mock:
# 	mockgen -destination ./db/mock/${file_name} -package ${pkg_name}  /home/ccat/Repos/backend_masterclass/db/sqlc ${interfaces}

# proto:
# 	rm -f ./rpc/*.go ./rpc/openapi/*.json
# 	protoc \
# 	--proto_path=proto \
# 	--proto_path=/home/ccat/go/pkg/mod/\
# 	github.com/grpc-ecosystem/grpc-gateway/v2@v2.25.1 \
# 	--go_out=rpc --go_opt=paths=source_relative \
# 	--go-grpc_out=rpc --go-grpc_opt=paths=source_relative \
# 	--grpc-gateway_out=rpc --grpc-gateway_opt=paths=source_relative \
# 	--openapiv2_out=rpc/openapi --openapiv2_opt=logtostderr=true \
# 	--openapiv2_opt=allow_merge=true,merge_file_name=simple_bank \
# 	proto/*.proto
# 	statik -src=rpc/openapi -dest=doc

# evans:
# 	evans --host=localhost --port=9090 --reflection repl --package pb --service SimpleBank

# Command aliasing is considered a "phony target" so it's possible to run it repeatedly.
.PHONY: connect migrateup migratedown sqlc test migraterestart server mock migratedown_1 migrateup_1 proto evans #dbfile

