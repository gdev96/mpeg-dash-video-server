#!/bin/bash

echo "Stopping Spark..."

oc get pods --no-headers=true | awk '/spark/{print $1}' | xargs oc delete pod
