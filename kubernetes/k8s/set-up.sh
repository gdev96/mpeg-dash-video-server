#!/bin/bash

echo "Starting Minikube..."

minikube start --memory=4096 --vm-driver=kvm2

# Wait that Minikube is ready
sleep 5

echo "Mounting directories..."

if ! pgrep -x 'minikube' > /dev/null
then
  minikube mount ../storage_manager:/storage-manager &
  sleep 1
  minikube mount ../video_processing_service:/video-processing &
  sleep 1
else
  echo "No directory to mount!"
fi
