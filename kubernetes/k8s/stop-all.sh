#!/bin/bash

echo "Stopping services and deployments..."

kubectl get pods --no-headers=true | awk '/spark/{print $1}' | xargs kubectl delete pod
kubectl delete clusterrolebinding spark-role
kubectl delete serviceaccount spark
kubectl delete -f api-gateway
kubectl delete -f spout
kubectl delete -f video-management
kubectl delete -f video-processing
kubectl delete -f storage-manager
kubectl delete -f apache-kafka
kubectl delete -f zookeeper
kubectl delete -f app-db
kubectl delete -f log-db
