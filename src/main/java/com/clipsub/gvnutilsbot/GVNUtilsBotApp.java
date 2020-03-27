package com.clipsub.gvnutilsbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

public class GVNUtilsBotApp extends ListenerAdapter {
    private static int PIN_THRESHOLD = 5;

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createDefault("")
                .addEventListeners(new GVNUtilsBotApp())
                .build();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        User user = event.getAuthor();
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);

        String pinEmoji = event.getReactionEmote().getName();
        if (pinEmoji.equals("\uD83D\uDCCC") && event.getReaction().getCount() >= PIN_THRESHOLD) {
            String msgId = event.getMessageId();
            event.getChannel().pinMessageById(msgId);
        }
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        // event.getChannel().getJDA().getre
    }
}
