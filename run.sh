#!/bin/bash
cd classes || exit
export CLASSPATH=$(find ../lib -name "*.jar" | tr '\n' ':')
java -cp "${CLASSPATH}":. Main
cd ..