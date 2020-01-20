#!/bin/bash

echo "Building Docker images and pushing them to OKD Registry..."

docker build -t docker-registry-default.master.particles.dieei.unict.it/video-server/api-gateway:1.0.0 -f api-gateway/nginx/Dockerfile.prod api-gateway/nginx
docker push docker-registry-default.master.particles.dieei.unict.it/video-server/api-gateway:1.0.0

cd ../..

docker build -t docker-registry-default.master.particles.dieei.unict.it/video-server/video-management-service:1.0.1 -f video_management_service/Dockerfile.prod video_management_service
docker push docker-registry-default.master.particles.dieei.unict.it/video-server/video-management-service:1.0.1

docker build -t docker-registry-default.master.particles.dieei.unict.it/video-server/video-processing-service:1.0.0 -f video_processing_service/Dockerfile.prod video_processing_service
docker push docker-registry-default.master.particles.dieei.unict.it/video-server/video-processing-service:1.0.0

docker build -t docker-registry-default.master.particles.dieei.unict.it/video-server/spout:1.0.0 -f spout/Dockerfile.prod spout
docker push docker-registry-default.master.particles.dieei.unict.it/video-server/spout:1.0.0

docker build -t docker-registry-default.master.particles.dieei.unict.it/video-server/spark:1.0.2 -f spark/Dockerfile.prod spark
docker push docker-registry-default.master.particles.dieei.unict.it/video-server/spark:1.0.2

docker build -t docker-registry-default.master.particles.dieei.unict.it/video-server/kafka-client:1.0.0 -f kafka_client/Dockerfile.prod kafka_client
docker push docker-registry-default.master.particles.dieei.unict.it/video-server/kafka-client:1.0.0
