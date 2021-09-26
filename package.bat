@echo off
jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.sql,java.xml --output runtime

XCOPY  database\ target\artifacts\wgu_capstone_jar\database\
jpackage --java-options  "\"-DdatabasePath=app/database/\"" --java-options "\"-DiconFile=WguCapstone.ico\""  --type msi --name WguCapstone --icon resources\public\favicon.ico  --win-menu --win-dir-chooser --dest dist\windows\x86\ --input target\artifacts\wgu_capstone_jar --main-jar wgu-capstone.jar --main-class com.wgu.capstone.Main --runtime-image runtime\