#!/bin/bash
export CLASSPATH=`find ./lib -name "*.jar" | tr '\n' ':'`
java -cp ${CLASSPATH}:classes/. Main
cd ..