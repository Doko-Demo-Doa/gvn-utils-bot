package com.clipsub.gvnutilsbot.interfaces;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public interface MessageBasedProcessor {
    public void onMessageReceived(@Nonnull MessageReceivedEvent event);
}
