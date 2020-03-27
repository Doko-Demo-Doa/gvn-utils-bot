#!/bin/bash

export DISCORD_TOKEN=token
mvn package
java -jar target/gvn-utils-bot-1.0.0.jar
unset DISCORD_TOKEN
