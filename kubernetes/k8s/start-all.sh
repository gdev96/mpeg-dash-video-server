#!/bin/bash

echo "Building Docker images..."

eval $(minikube docker-env)

docker build -t storage-manager:1.0.0 ../storage_manager
docker build -t video-management-service:2.0.0 ../video_management_service
docker build -t video-processing-service:2.0.0 ../video_processing_service
docker build -t spout:2.0.0 ../spout
docker build -t spark:2.0.0 ../spark
docker build -t kafka-client:1.0.0 ../kafka-client

echo "Mounting directories..."

if ! pgrep -x 'minikube' > /dev/null
then
  minikube mount ../storage_manager:/storage-manager > /dev/null &
  minikube mount ../video_processing_service:/video-processing > /dev/null &
  minikube mount ../spout:/spout > /dev/null &

  # Wait that directories have been mounted
  sleep 2

  echo "Directories successfully mounted!"
else
  echo "No directory to mount!"
fi

echo "Starting services and deployments..."

kubectl apply -f log-db
kubectl apply -f app-db
kubectl apply -f zookeeper
kubectl apply -f apache-kafka
kubectl apply -f storage-manager
kubectl apply -f video-processing
kubectl apply -f video-management
kubectl apply -f spout
kubectl apply -f api-gateway
kubectl create serviceaccount spark
kubectl create clusterrolebinding spark-role --clusterrole=edit --serviceaccount=default:spark
