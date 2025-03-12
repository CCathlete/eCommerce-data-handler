#! /bin/bash

export $(cat .env | xargs)

cat /home/ccat/Repos/eCommerce-data-handler/src/test/java/org/webcat/ecommerce/datahandler/shared/test-doc-1.json

printf "\n\n$MINIO_HOST:$MINIO_API_PORT\n\n"

# Won't work, it needs encryption to pass the keys.
curl -X PUT --upload-file /home/ccat/Repos/eCommerce-data-handler/src/test/java/org/webcat/ecommerce/datahandler/shared/test-doc-1.json \
    http://$MINIO_ACCESS_KEY:$MINIO_SECRET_KEY@$MINIO_HOST:$MINIO_API_PORT/$CURRENT_BUCKET/test-doc-1.json

