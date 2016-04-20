#!/bin/bash

if [ -z ${GATLING_HOME+x} ]; then echo "GATLING_HOME is unset"; exit -1; fi

mkdir -p   ./../../../build/reports/scala
$GATLING_HOME/bin/gatling.sh -sf . -rf  ./../../../build/reports/scala
