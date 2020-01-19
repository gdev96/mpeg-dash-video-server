#!/bin/bash

echo "Starting services and deployments..."

oc apply -f zookeeper
oc apply -f apache-kafka
oc apply -f video-processing
oc apply -f video-management
oc apply -f spout
oc apply -f api-gateway
oc apply -f spark
