#!/bin/bash

if [ ! -d "runtime" ]; then
  jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.sql,java.xml --output runtime
fi
until [ -f "runtime/bin/java" ]
do
     sleep 1
     echo "Building runtime..."
done
cp -r database/ target/artifacts/wgu_capstone_jar/database/
jpackage --java-options  "\"-DdatabasePath=\$APPDIR/database\"" --java-options "\"-DiconFile=\$APPDIR/../Resources/WguCapstone.icns\"" --java-options "\"-XstartOnFirstThread\"" --type "app-image" --name WguCapstone --resource-dir macos/ --dest dist/mac/arm/ --input target/artifacts/wgu_capstone_jar --main-jar wgu-capstone.jar --main-class com.wgu.capstone.Main --runtime-image runtime/
until [ -f "dist/mac/arm/WguCapstone.app/Contents/app/wgu-capstone.jar" ]
do
     sleep 1
     echo "Building app-image..."
done
