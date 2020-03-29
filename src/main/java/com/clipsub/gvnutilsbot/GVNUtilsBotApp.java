package com.clipsub.gvnutilsbot;

import com.clipsub.gvnutilsbot.ai.NSFWProcessor;
import com.clipsub.gvnutilsbot.pin.PinProcessor;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GVNUtilsBotApp extends ListenerAdapter {
    private NSFWProcessor nsfwProcessor;
    private PinProcessor pinProcessor;

    private final ScheduledExecutorService scheduler;

    public GVNUtilsBotApp() {
        nsfwProcessor = new NSFWProcessor();
        pinProcessor = new PinProcessor();

        scheduler = Executors.newScheduledThreadPool(1);
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDABuilder.createDefault(System.getenv("DISCORD_TOKEN"))
                .addEventListeners(new GVNUtilsBotApp())
                .build();
    }

    private void checkStreamers() {

    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        super.onReady(event);
//        scheduler.scheduleAtFixedRate(() -> {
//
//        }, 0, 2, TimeUnit.MINUTES);
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        try {
            Message m = event.getMessage();
            if (m.getContentRaw().startsWith("!nsfw") || m.getContentRaw().startsWith("!ns")) {
                nsfwProcessor.processNsfwMessage(m);
                return;
            }

            nsfwProcessor.processAttachment(m);
        } catch (Exception e) {
            // Code...
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
