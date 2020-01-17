while true
do
  TIMES_GET=$(((RANDOM % 30) + 1))
  INDEX=0

  echo "Making $TIMES_GET GET videos..."

  while [ $INDEX -lt $TIMES_GET ]
  do
    curl video-server.localhost/vms/videos
    echo
    ((INDEX++))
    sleep 1
  done

  TIMES_GET=$(((RANDOM % 30) + 1))
  INDEX=0

  echo "Making $TIMES_GET GET videos/1..."

  while [ $INDEX -lt $TIMES_GET ]
  do
    curl video-server.localhost/vms/videos/1
    echo
    ((INDEX++))
    sleep 1
  done
done
