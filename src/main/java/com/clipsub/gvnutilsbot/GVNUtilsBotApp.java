package com.clipsub.gvnutilsbot;

import com.clipsub.gvnutilsbot.ai.NSFWProcessor;
import com.clipsub.gvnutilsbot.pin.PinProcessor;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

public class GVNUtilsBotApp extends ListenerAdapter {
    private NSFWProcessor nsfwProcessor;
    private PinProcessor pinProcessor;

    public GVNUtilsBotApp() {
        nsfwProcessor = new NSFWProcessor();
        pinProcessor = new PinProcessor();
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDABuilder.createDefault(System.getenv("DISCORD_TOKEN"))
                .addEventListeners(new GVNUtilsBotApp())
                .build();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        try {
            Message m = event.getMessage();
            if (m.getContentRaw().startsWith("!nsfw") || m.getContentRaw().startsWith("!ns")) {
                nsfwProcessor.processNsfwMessage(m);
                return;
            }

            if (m.getAttachments().size() < 1) return;

            m.getAttachments().forEach(attachment -> {
                if (attachment.isImage() || attachment.isVideo()) {
                    nsfwProcessor.processAttachment(m);
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
        pinProcessor.onMessageReactionAdd(event);
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        pinProcessor.onMessageReactionRemove(event);
    }
}
