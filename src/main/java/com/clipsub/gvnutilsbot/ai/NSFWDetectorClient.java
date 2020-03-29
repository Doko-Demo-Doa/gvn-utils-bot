package com.clipsub.gvnutilsbot.ai;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class NSFWDetectorClient {
    private static ClarifaiClient client;

    public static ClarifaiClient get() {
        if (client == null) {
            client = new ClarifaiBuilder(System.getenv("CLARIFAI_TOKEN"))
                    .client(new OkHttpClient.Builder()
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .build()).buildSync();
            return client;
        }

        return client;
    }
}
