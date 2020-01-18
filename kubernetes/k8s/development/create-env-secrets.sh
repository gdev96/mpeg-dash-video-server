#!/bin/bash

kubectl create configmap app-db-env --from-env-file=./app-db/app-db.env.properties --save-config
kubectl get configmap app-db-env -o yaml > app-db/app-db.configmap.yml

kubectl create secret generic app-db-secret --from-env-file=./app-db/app-db.secret.properties --save-config
kubectl get secret app-db-secret -o yaml > app-db/app-db.secret.yml

kubectl create configmap log-db-env --from-env-file=./log-db/log-db.env.properties --save-config
kubectl get configmap log-db-env -o yaml > log-db/log-db.configmap.yml

kubectl create secret generic log-db-secret --from-env-file=./log-db/log-db.secret.properties --save-config
kubectl get secret log-db-secret -o yaml > log-db/log-db.secret.yml

kubectl create configmap video-management-env --from-env-file=./video-management/video-management.env.properties --save-config
kubectl get configmap video-management-env -o yaml > video-management/video-management.configmap.yml

kubectl create secret generic video-management-secret --from-env-file=./video-management/video-management.secret.properties --save-config
kubectl get secret video-management-secret -o yaml > video-management/video-management.secret.yml

kubectl create configmap video-processing-env --from-env-file=./video-processing/video-processing.env.properties --save-config
kubectl get configmap video-processing-env -o yaml > video-processing/video-processing.configmap.yml

kubectl create secret generic video-processing-secret --from-env-file=./video-processing/video-processing.secret.properties --save-config
kubectl get secret video-processing-secret -o yaml > video-processing/video-processing.secret.yml

kubectl create configmap spout-env --from-env-file=./spout/spout.env.properties --save-config
kubectl get configmap spout-env -o yaml > spout/spout.configmap.yml

kubectl create secret generic spout-secret --from-env-file=./spout/spout.secret.properties --save-config
kubectl get secret spout-secret -o yaml > spout/spout.secret.yml
