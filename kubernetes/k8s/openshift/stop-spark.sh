#!/bin/bash

echo "Stopping services and deployments..."

oc get pods --no-headers=true | awk '/spark/{print $1}' | xargs oc delete pod
oc delete clusterrolebinding spark-role
oc delete serviceaccount spark
oc delete -f spark
oc delete -f api-gateway
oc delete -f spout
oc delete -f video-management
oc delete -f video-processing
oc delete -f apache-kafka
oc delete -f zookeeper
