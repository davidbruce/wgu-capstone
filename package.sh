#!/bin/bash

if [ ! -d "runtime" ]; then
  jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.sql,java.xml --output runtime
fi
until [ -f "runtime/bin/java" ]
do
     sleep 1
     echo "Building runtime..."
done
jpackage --type "app-image" --name WguCapstone --dest dist/mac/arm/ --input target/artifacts/wgu_capstone_jar --main-jar wgu-capstone.jar --main-class com.wgu.capstone.Main --runtime-image runtime/
until [ -f "dist/mac/arm/WguCapstone.app/Contents/app/wgu-capstone.jar" ]
do
     sleep 1
     echo "Building app-image..."
done
cp -r database/ dist/mac/arm/WguCapstone.app/Contents/database
cp -r webview/ dist/mac/arm/WguCapstone.app/Contents/webview