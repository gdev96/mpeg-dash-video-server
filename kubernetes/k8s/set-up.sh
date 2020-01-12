#!/bin/bash

echo "Starting Minikube..."

minikube start --memory=4096 --vm-driver=kvm2

# Wait that Minikube is ready
sleep 5

echo "Mounting directories..."

if ! pgrep -x 'minikube' > /dev/null
then
  start-stop-daemon -b -S -n minikube-1 -x ~/minikube mount -- ~/video-server/kubernetes/storage_manager:/storage-manager
  start-stop-daemon -b -S -n minikube-2 -x ~/minikube mount -- ~/video-server/kubernetes/video_processing_service:/video-processing
  echo "Directories successfully mounted!"
else
  echo "No directory to mount!"
fi
