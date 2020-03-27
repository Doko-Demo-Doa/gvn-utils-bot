package com.clipsub.gvnutilsbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.stream.Collectors;

public class GVNUtilsBotApp extends ListenerAdapter {
    private static int PIN_THRESHOLD = 1;
    private static String PIN_EMOTE = "\uD83D\uDCCC";

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDABuilder.createDefault(System.getenv("DISCORD_TOKEN"))
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
        String msgId = event.getMessageId();
        TextChannel channel = event.getTextChannel();

        channel.retrieveMessageById(msgId).queue(message -> {
            if (pinEmoji.equals(PIN_EMOTE)) {
                System.out.println("Oh yeah");
                channel.pinMessageById(message.getIdLong()).queue();
            }
        });
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        String msgId = event.getMessageId();
        System.out.println("Go here 2");
        event.getChannel().retrieveMessageById(msgId).queue(message -> {
            List<MessageReaction> reactions = message
                    .getReactions()
                    .stream()
                    .filter(messageReaction -> messageReaction.getReactionEmote().getName().equals(PIN_EMOTE))
                    .collect(Collectors.toList());

            if (reactions.size() < PIN_THRESHOLD) {
                event.getChannel().unpinMessageById(msgId).queue();
            }
        }, failure -> System.out.println("Error"));
    }
}
