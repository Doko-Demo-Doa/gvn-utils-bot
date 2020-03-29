package com.clipsub.gvnutilsbot.pin;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class PinProcessor {
    private static int PIN_THRESHOLD = 5;
    private static String PIN_EMOTE = "\uD83D\uDCCC";

    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        String pinEmoji = event.getReactionEmote().getName();
        String msgId = event.getMessageId();
        TextChannel channel = event.getTextChannel();

        Message m = channel.retrieveMessageById(msgId).complete();

        channel.retrieveMessageById(msgId).queue(message -> {
            if (pinEmoji.equals(PIN_EMOTE)) {
                channel.pinMessageById(message.getIdLong()).queue();
            }
        });
    }

    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        String msgId = event.getMessageId();
        event.getChannel().retrieveMessageById(msgId).queue(message -> {
            List<MessageReaction> reactions = message
                    .getReactions()
                    .stream()
                    .filter(messageReaction -> messageReaction.getReactionEmote().getName().equals(PIN_EMOTE))
                    .collect(Collectors.toList());

            if (reactions.size() <= PIN_THRESHOLD - 2) {
                event.getChannel().unpinMessageById(msgId).queue();
            }
        }, failure -> System.out.println("Error"));
    }
}
