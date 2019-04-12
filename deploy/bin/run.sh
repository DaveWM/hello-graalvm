#!/bin/sh

set -ae

JAVA_BIN_PATH="/usr/bin/java"
RUN_JAR="$JAVA_BIN_PATH -jar"

exec $RUN_JAR /usr/share/java/hello-graalvm-0.1.0-SNAPSHOT-standalone.jar $@
