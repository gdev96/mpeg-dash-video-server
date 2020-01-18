#!/bin/bash

eval $(minikube docker-env)

/usr/local/spark/bin/spark-submit \
    --master k8s://https://$(minikube ip):8443 \
    --deploy-mode cluster \
    --name spark \
    --class com.unict.dieei.pr20.spark.App \
    --conf spark.executor.instances=2 \
    --conf spark.kubernetes.executor.request.cores=0.1 \
    --conf spark.kubernetes.authenticate.driver.serviceAccountName=spark \
    --conf spark.kubernetes.container.image=spark:1.0.0 \
    --conf spark.kubernetes.driverEnv.KAFKA_ADDRESS=apache-kafka:9092 \
    --conf spark.kubernetes.driverEnv.KAFKA_GROUP_ID=spark-group \
    --conf spark.kubernetes.driverEnv.KAFKA_MAIN_TOPIC=logs \
    --conf spark.kubernetes.driverEnv.BATCH_SIZE=30 \
    --conf spark.kubernetes.driverEnv.LAST_BATCHES=3 \
    --conf spark.kubernetes.driverEnv.CHAT_ID=-375918085 \
    --conf spark.kubernetes.driverEnv.BOT_TOKEN=927700725:AAEVZvHCiWvZRdrkj8whRaYxbm7jpOyGyqA \
    local:///opt/spark/work-dir/spark-1.0-SNAPSHOT-jar-with-dependencies.jar
