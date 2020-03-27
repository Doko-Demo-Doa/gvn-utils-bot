package com.clipsub.gvnutilsbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

public class GVNUtilsBotApp extends ListenerAdapter {
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createDefault("")
                .addEventListeners(new GVNUtilsBotApp())
                .build();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        User user = event.getAuthor();
    }
}
