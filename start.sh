#!/bin/bash
echo "### MVA SP start - $(date '+%Y-%m-%d %H:%M:%S')"

# 중복 실행 방지
PID=`ps -ef | grep 'sp-with-sdk.war' | grep -v grep | awk '{print $2}'`
if [ "" != "$PID" ]; then
    echo "### Already running (PID: $PID). Please stop first."
    exit 1
fi

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.302.b08-0.el7_9.x86_64

APP_DIR=/data/www/MVA_SP_SDK_PROD

if [ ! -d "$APP_DIR" ]; then
    echo "### APP_DIR not found: $APP_DIR"
    exit 1
fi

cd $APP_DIR

JAVA_OPTS="-Xms2048m -Xmx8192m -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=2048m"

echo "### APP_DIR    : ${APP_DIR}"
echo "### JAVA_HOME  : ${JAVA_HOME}"
echo "### JAVA_OPTS  : ${JAVA_OPTS}"

nohup $JAVA_HOME/bin/java $JAVA_OPTS -jar $APP_DIR/sp-with-sdk.war > /dev/null 2>&1 &

echo "### MVA SP start complete (PID: $!)"
echo "### Start time: $(date '+%Y-%m-%d %H:%M:%S')"