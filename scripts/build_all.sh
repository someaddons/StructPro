#!/bin/bash
SCRIPT=build.sh
FILES=$(pwd)/../builds/*/$SCRIPT
for f in $FILES
do
  echo "Processing: $f"
  cd $(dirname $f)
  echo $(pwd)
  chmod +x $SCRIPT
  chmod +x gradlew
  ./$SCRIPT
done