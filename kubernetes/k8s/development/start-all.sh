#!/bin/bash

echo "Mounting directories..."

if ! pgrep -x 'minikube' > /dev/null
then
  minikube mount ../../storage_management_service:/storage-management > /dev/null &
  minikube mount ../../video_processing_service:/video-processing > /dev/null &
  minikube mount ../../spout:/spout > /dev/null &

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
kubectl apply -f storage-management
kubectl apply -f video-processing
kubectl apply -f video-management
kubectl apply -f spout
kubectl apply -f api-gateway
kubectl create serviceaccount spark
kubectl create clusterrolebinding spark-role --clusterrole=edit --serviceaccount=default:spark
