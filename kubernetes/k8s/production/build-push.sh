#!/bin/bash

echo "Building Docker images and pushing them to Docker Hub..."

cd ../..

docker build -t gdev96/storage-management-service:1.0.0 -t gdev96/storage-management-service:1 -t gdev96/storage-management-service:latest -f storage_management_service/Dockerfile.prod storage_management_service
docker push gdev96/storage-management-service:1.0.0
docker push gdev96/storage-management-service:1
docker push gdev96/storage-management-service:latest

docker build -t gdev96/video-management-service:2.0.0 -t gdev96/video-management-service:2 -t gdev96/video-management-service:latest -f video_management_service/Dockerfile.prod video_management_service
docker push gdev96/video-management-service:2.0.0
docker push gdev96/video-management-service:2
docker push gdev96/video-management-service:latest

docker build -t gdev96/video-processing-service:2.0.0 -t gdev96/video-processing-service:2 -t gdev96/video-processing-service:latest -f video_processing_service/Dockerfile.prod video_processing_service
docker push gdev96/video-processing-service:2.0.0
docker push gdev96/video-processing-service:2
docker push gdev96/video-processing-service:latest

docker build -t gdev96/spout:1.0.0 -t gdev96/spout:1 -t gdev96/spout:latest -f spout/Dockerfile.prod spout
docker push gdev96/spout:1.0.0
docker push gdev96/spout:1
docker push gdev96/spout:latest

docker build -t gdev96/spark:1.0.1 -t gdev96/spark:1 -t gdev96/spark:latest -f spark/Dockerfile.prod spark
docker push gdev96/spark:1.0.1
docker push gdev96/spark:1
docker push gdev96/spark:latest

docker build -t gdev96/kafka-client:1.0.0 -t gdev96/kafka-client:1 -t gdev96/kafka-client:latest -f kafka_client/Dockerfile.prod kafka_client
docker push gdev96/kafka-client:1.0.0
docker push gdev96/kafka-client:1
docker push gdev96/kafka-client:latest
