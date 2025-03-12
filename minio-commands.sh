!# /bin/bash

source .env

# Create a webhook
curl -X POST "http://$MINIO_HOST:$MINIO_API_PORT/minio/admin/v3/add/webhook" \
  -H "Content-Type: application/json" \
  --user "$MINIO_ACCESS_KEY:$MINIO_SECRET_KEY" \
  -d '{
    "config": {
      "endpoint": "http://127.0.0.1:8080/etl/webhook",
      "queueDir": "/tmp/minio-events",
      "queueLimit": 100000
    },
    "comment": "Send events to ETL service"
  }'
