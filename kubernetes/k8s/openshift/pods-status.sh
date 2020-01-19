#!/bin/bash

while true
do
  STATUS_INFO=$(oc get pods 2>&1) && tput reset
  echo "$STATUS_INFO"
  sleep 1
done
