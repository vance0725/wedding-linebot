pid=$(pgrep -f wedding.bot-0.0.1-SNAPSHOT.jar)
  
if [ -z "$pid" ]
then
        echo "process not found!"
else
        kill -9 $pid
        echo "pid $pid was killed successfully!"
fi
