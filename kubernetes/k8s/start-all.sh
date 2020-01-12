#!/bin/bash

echo "Building Docker images..."

eval $(minikube docker-env)

docker build -t storage-manager:1.0.0 ../storage_manager
docker build -t video-management-service:2.0.0 ../video_management_service
docker build -t video-processing-service:2.0.0 ../video_processing_service

echo "Starting services and deployments..."

kubectl apply -f log-db
kubectl apply -f app-db
kubectl apply -f storage-manager
kubectl apply -f kafka
kubectl apply -f video-processing
kubectl apply -f video-management
kubectl apply -f api-gateway
