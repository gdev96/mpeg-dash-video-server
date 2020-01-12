#!/bin/bash

docker build -t gdev96/api-gateway:1.0.1 -t gdev96/api-gateway:1 -t gdev96/api-gateway:latest -f api_gateway/Dockerfile.prod api_gateway
docker push gdev96/api-gateway:1.0.1
docker push gdev96/api-gateway:1
docker push gdev96/api-gateway:latest

docker build -t gdev96/video-management-service:1.1.1 -t gdev96/video-management-service:1 -t gdev96/video-management-service:latest -f video_management_service/Dockerfile.prod video_management_service
docker push gdev96/video-management-service:1.1.1
docker push gdev96/video-management-service:1
docker push gdev96/video-management-service:latest

docker build -t gdev96/video-processing-service:1.1.1 -t gdev96/video-processing-service:1 -t gdev96/video-processing-service:latest -f video_processing_service/Dockerfile.prod video_processing_service
docker push gdev96/video-processing-service:1.1.1
docker push gdev96/video-processing-service:1
docker push gdev96/video-processing-service:latest
