!# /bin/bash

. .env

# Create a webhook
curl -X POST "http://<MINIO_HOST>:9000/minio/admin/v3/add/webhook" \
  -H "Content-Type: application/json" \
  --user "$MINIO_ACCESS_KEY:$MINIO_SECRET_KEY" \
  -d '{
    "config": {
      "endpoint": "http://<YOUR_APP_HOST>:<YOUR_PORT>/etl/webhook",
      "queueDir": "/tmp/minio-events",
      "queueLimit": 100000
    },
    "comment": "Send events to ETL service"
  }'
