#!/bin/bash

export DISCORD_TOKEN=token_value
mvn package
unset DISCORD_TOKEN
java -jar target/gvn-utils-bot-1.0.0.jar
