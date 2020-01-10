#!/bin/bash

echo "Unmounting directories..."

if pgrep -x 'minikube' > /dev/null
then
  echo "$(pgrep -cx 'minikube') active mounts found!"
  pkill -x 'minikube'

  # Wait that directories have been successfully unmounted
  sleep 5
else
  echo "No directory to unmount!"
fi

echo "Stopping Minikube..."

minikube stop
