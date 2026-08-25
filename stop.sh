#!/bin/sh

echo "### MVA SP stop - $(date '+%Y-%m-%d %H:%M:%S')"

PID=`ps -ef | grep 'sp-with-sdk.war' | grep -v grep | awk '{print $2}'`

if [ "" != "$PID" ]; then
    echo "### Stopping process (PID: $PID)..."
    kill $PID

    # Graceful shutdown 대기 (10초)
    for i in $(seq 1 10); do
        sleep 1
        RUNNING=`ps -ef | grep 'sp-with-sdk.war' | grep -v grep | awk '{print $2}'`
        if [ "" = "$RUNNING" ]; then
            echo "### Process stopped gracefully."
            exit 0
        fi
        echo "### Waiting for shutdown... ($i/10)"
    done

    echo "### Force killing process (PID: $PID)..."
    kill -9 $PID
    echo "### Process force killed."
else
    echo "### Process not running."
fi