package com.clipsub.gvnutilsbot;

import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import com.clipsub.gvnutilsbot.ai.NSFWDetectorClient;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.AttachmentOption;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class GVNUtilsBotApp extends ListenerAdapter {
    private static int PIN_THRESHOLD = 1;
    private static String PIN_EMOTE = "\uD83D\uDCCC";
    private static float SFW_THRESHOLD = 0.7f;

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDABuilder.createDefault(System.getenv("DISCORD_TOKEN"))
                .addEventListeners(new GVNUtilsBotApp())
                .build();
    }

    private void processAttachment(Message m) {
        String img = "https://i.ibb.co/0BHss9p/1173690-20200322004317-1.png";

        Model<Concept> nsfwModel = NSFWDetectorClient.get().getDefaultModels().nsfwModel();
        PredictRequest<Concept> request = nsfwModel.predict().withInputs(
                ClarifaiInput.forImage(img));

        List<ClarifaiOutput<Concept>> result = request.executeSync().get();
        System.out.println();
        Concept dc = result.get(0).data().get(0);

        if (dc.name() != null && dc.name().equals("nsfw")) {
            this.processNsfwMessage(m);
            return;
        }
        if (dc.name() != null && dc.name().equals("sfw") && dc.value() < SFW_THRESHOLD) {
            this.processNsfwMessage(m);
        }
    }

    private void processNsfwMessage(Message message) {
        TextChannel currentTextChannel = message.getTextChannel();

        String newContent = message.getContentStripped()
                .replace("!nsfw", "")
                .replace("!ns", "");

        String builder = "From " +
                "<@" +
                message.getAuthor().getIdLong() +
                "> :" +
                newContent;
        // Create MessageAction.
        MessageAction composingAction = currentTextChannel.sendMessage(builder);

        // Create cached file ref:
        List<InputStream> cachedStreams = new ArrayList<>();

        message.getAttachments().forEach(attachment -> {
            try {
                attachment.retrieveInputStream();
                InputStream fileStream = attachment.retrieveInputStream().get();
                composingAction.addFile(fileStream, attachment.getFileName(), AttachmentOption.SPOILER);

                cachedStreams.add(fileStream);

                // Cleanup temp. file after using.
                // attachmentFile.delete();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        // Execute sending new message:
        composingAction.queue();

        // Delete old message reference:
        message.delete().queue();

        // Delete cached files.
        cachedStreams.forEach(inputStream -> {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        try {
            Message m = event.getMessage();
            if (m.getContentRaw().startsWith("!nsfw") || m.getContentRaw().startsWith("!ns")) {
                this.processNsfwMessage(m);
            }

            m.getAttachments().forEach(attachment -> {
                if (attachment.isImage() || attachment.isVideo()) {
                    // this.processAttachment(m);
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);

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

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        String msgId = event.getMessageId();
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
