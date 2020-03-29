# GVN Utils Bot
(A.k.a Hikaru bot)

This is a (supposedly) private bot for GVN discord server, handles various tasks such as message pinning.

Written in Java, and using [JDA](https://github.com/DV8FromTheWorld/JDA) as the core.

To get started, create a bootstrap file (say, `start.sh`), then put the keys in:

```bash
#!/bin/bash

export DISCORD_TOKEN=<discord_token>
export CLARIFAI_TOKEN=<clarifai_key>
export GENERAL_CHANNEL_ID=<target_channel_id>
mvn package
java -jar target/gvn-utils-bot-1.0.0.jar
unset DISCORD_TOKEN
```