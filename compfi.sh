#!/bin/bash

dir=$(pwd -P)
javac -d bin -classpath $dir/lib/gson-2.8.6.jar src/tech/jriascos/*/*.java
java -classpath $dir/bin:$dir/lib/gson-2.8.6.jar tech.jriascos.runtime.Fix