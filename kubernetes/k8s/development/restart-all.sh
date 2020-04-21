#!/bin/bash

echo "Stopping services and deployments..."

kubectl get pods --no-headers=true | awk '/spark/{print $1}' | xargs kubectl delete pod
kubectl delete clusterrolebinding spark-role
kubectl delete serviceaccount spark
kubectl delete -f spark
kubectl delete -f api-gateway
kubectl delete -f spout
kubectl delete -f video-management
kubectl delete -f video-processing
kubectl delete -f storage-management
kubectl delete -f apache-kafka
kubectl delete -f zookeeper
kubectl delete -f app-db
kubectl delete -f log-db

echo "Waiting that resources are released..."

while true
do
  if kubectl get pods 2>&1 | grep "No resources found" > /dev/null
  then
    echo "Resources successfully released!"
    break
  else
    sleep 2
  fi
done

echo "Mounting directories..."

if ! pgrep -x 'minikube' > /dev/null
then
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
kubectl apply -f spark
kubectl create serviceaccount spark
kubectl create clusterrolebinding spark-role --clusterrole=edit --serviceaccount=default:spark --namespace=default
