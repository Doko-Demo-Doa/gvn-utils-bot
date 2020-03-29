package com.clipsub.gvnutilsbot.ai;

import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.AttachmentOption;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NSFWProcessor {
    private static float SFW_THRESHOLD = 0.85f;

    public void processAttachment(Message m) {
        boolean isGeneralChannel = System.getenv("GENERAL_CHANNEL_ID").equals(m.getChannel().getId());
        if (!isGeneralChannel) return;
        boolean safe = true;

        if (m.getAttachments().size() < 1) return;
        if (m.getAuthor().isBot()) return;

        m.getAttachments().forEach(attachment -> {
            if (!attachment.isImage() && !attachment.isVideo()) return;
            if (attachment.getFileName().startsWith("SPOILER_")) return;

            // Get media url.
            String media = attachment.getUrl();

            Model<Concept> nsfwModel = NSFWDetectorClient.get().getDefaultModels().nsfwModel();
            PredictRequest<Concept> request = nsfwModel.predict().withInputs(
                    ClarifaiInput.forImage(media));

            List<ClarifaiOutput<Concept>> result = request.executeSync().get();
            Concept dc = result.get(0).data().get(0);

            if (dc.name() != null && dc.name().equals("nsfw")) {
                this.processNsfwMessage(m);
                return;
            }
            if (dc.name() != null && dc.name().equals("sfw") && dc.value() < SFW_THRESHOLD) {
                this.processNsfwMessage(m);
            }
        });
    }

    public void processNsfwMessage(Message message) {
        if (message.getAuthor().isBot()) return;
        if (message.getAttachments().size() > 0 && message.getAttachments().get(0).getFileName().startsWith("SPOILER_"))
            return;
        TextChannel currentTextChannel = message.getTextChannel();

        String newContent = message.getContentStripped()
                .replace("!nsfw", "")
                .replace("!ns", "");

        String builder = "Nguyên văn từ " +
                "<@" +
                message.getAuthor().getIdLong() +
                "> : " +
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
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        // Execute sending new message:
        composingAction.complete();

        // Delete old message reference:
        message.delete().complete();

        // Delete cached files.
        cachedStreams.forEach(inputStream -> {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // cachedStreams.clear();
    }
}
