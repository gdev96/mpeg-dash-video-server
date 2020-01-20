#!/bin/bash

echo "Building Docker images..."

eval $(minikube docker-env)

cd ../..

docker build -t storage-management-service:1.0.0 storage_management_service
docker build -t video-management-service:2.0.0 video_management_service
docker build -t video-processing-service:2.0.0 video_processing_service
docker build -t spout:1.0.0 spout
docker build -t spark:1.0.2 spark
docker build -t kafka-client:1.0.0 kafka_client
