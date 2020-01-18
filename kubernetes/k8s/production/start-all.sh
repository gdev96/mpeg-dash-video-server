#!/bin/bash

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
