#!/bin/bash

oc create configmap video-management --from-env-file=./video-management/video-management.env.properties --save-config
oc get configmap video-management -o yaml > video-management/video-management.configmap.yml

oc create configmap video-processing --from-env-file=./video-processing/video-processing.env.properties --save-config
oc get configmap video-processing -o yaml > video-processing/video-processing.configmap.yml

oc create configmap spout --from-env-file=./spout/spout.env.properties --save-config
oc get configmap spout -o yaml > spout/spout.configmap.yml

oc create secret generic apache-kafka --from-env-file=./apache-kafka/apache-kafka.secret.properties --save-config
oc get secret apache-kafka -o yaml > apache-kafka/apache-kafka.secret.yml

oc create secret generic spark --from-env-file=./spark/spark.secret.properties --save-config
oc get secret spark -o yaml > spark/spark.secret.yml
