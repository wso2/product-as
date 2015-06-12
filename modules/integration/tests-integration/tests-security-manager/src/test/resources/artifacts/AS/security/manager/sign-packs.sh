#!/bin/bash

keyname="wso2carbon.jks"
keyalias=wso2carbon
keypass='wso2carbon'

keystore=./repository/resources/security/$keyname
for p in ./repository/components/plugins ./repository/components/patches ./repository/components/lib ./lib  ./bin
do
    for i in `find $p -iname "*.jar"`
    do
        echo $i
        jarsigner -digestalg SHA1 -keystore $keystore -storepass $keypass $i $keyalias
    done
done
