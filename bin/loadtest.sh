#!/bin/bash

VERSION=1.0-SNAPSHOT
ARTIFACT=target/ldaptive-performance-$VERSION-jar-with-dependencies.jar
java -jar $ARTIFACT $@

