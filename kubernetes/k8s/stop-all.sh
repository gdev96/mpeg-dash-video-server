#!/bin/bash

echo "Stopping services and deployments..."

kubectl delete -f api-gateway
kubectl delete -f video-management
kubectl delete -f video-processing
kubectl delete -f kafka
kubectl delete -f storage-manager
kubectl delete -f app-db
kubectl delete -f log-db
