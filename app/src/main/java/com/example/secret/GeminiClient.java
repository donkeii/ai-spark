package com.example.secret;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Minimal Gemini API client using OkHttp.
 * This hits the Generative Language API's text model endpoint.
 */
public class GeminiClient {
    // Use the alias "-latest" to avoid 404 on deprecated versions
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-pro:generateContent?key=";
    private static final String ALT_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final Gson gson = new Gson();
    private final String apiKey;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final int MAX_RETRIES = 3;
    private static final long BASE_BACKOFF_MS = 1200;

    public interface GeminiCallback {
        void onSuccess(String text);
        void onError(String message);
    }

    public GeminiClient(String apiKey) {
        this.apiKey = apiKey;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .callTimeout(90, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(logging)
                .build();
    }

    public void generateAsync(String systemPrompt, String userMessage, @NonNull GeminiCallback cb) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            cb.onError("Thiếu GEMINI_API_KEY. Hãy đặt key trong local.properties.");
            return;
        }
        Map<String, Object> json = new HashMap<>();
        // Combine system guidance + user prompt into a single user content to keep schema simple
        String combined = systemPrompt + "\n\nUser: " + userMessage;

        Map<String, Object> content = new HashMap<>();
        content.put("role", "user");
        content.put("parts", new Map[]{part(combined)});

        json.put("contents", new Map[]{content});

        String body = gson.toJson(json);
        enqueueRequestWithRetry(body, BASE_URL, 0, cb);
    }

    private void enqueueRequestWithRetry(String body, String baseUrl, int attempt, @NonNull GeminiCallback cb) {
        Request request = new Request.Builder()
                .url(baseUrl + apiKey)
                .post(RequestBody.create(body, JSON))
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (attempt < MAX_RETRIES) {
                    long delay = (long) (BASE_BACKOFF_MS * Math.pow(2, attempt));
                    scheduler.schedule(() -> enqueueRequestWithRetry(body, baseUrl, attempt + 1, cb), delay, TimeUnit.MILLISECONDS);
                } else {
                    cb.onError(e.getMessage());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    try {
                        Map<?, ?> errRoot = gson.fromJson(resp, Map.class);
                        Object errorObj = errRoot != null ? errRoot.get("error") : null;
                        String msg = errorObj instanceof Map ? String.valueOf(((Map<?, ?>) errorObj).get("message")) : null;
                        String finalMsg = (msg != null && !msg.isEmpty()) ? msg : ("HTTP " + response.code());
                        boolean overloaded = response.code() == 429 || response.code() == 503 || finalMsg.toLowerCase().contains("overload");
                        if (overloaded && attempt < MAX_RETRIES) {
                            long delay = (long) (BASE_BACKOFF_MS * Math.pow(2, attempt));
                            String retryAfter = response.header("Retry-After");
                            if (retryAfter != null) {
                                try { delay = Math.max(delay, Long.parseLong(retryAfter) * 1000L); } catch (Exception ignored) {}
                            }
                            // First retry: try fallback lighter model
                            String nextUrl = (attempt == 0) ? ALT_URL : baseUrl;
                            scheduler.schedule(() -> enqueueRequestWithRetry(body, nextUrl, attempt + 1, cb), delay, TimeUnit.MILLISECONDS);
                            return;
                        }
                        cb.onError(finalMsg);
                    } catch (Exception e) {
                        cb.onError("HTTP " + response.code());
                    }
                    return;
                }
                // Parse minimal text from candidates[0].content.parts[0].text
                try {
                    Map<?, ?> root = gson.fromJson(resp, Map.class);
                    String text = extractText(root);
                    cb.onSuccess(text != null ? text : "");
                } catch (Exception ex) {
                    cb.onError(ex.getMessage());
                }
            }
        });
    }

    private Map<String, String> part(String text) {
        Map<String, String> p = new HashMap<>();
        p.put("text", text);
        return p;
    }

    private String extractText(Map<?, ?> root) {
        try {
            Object candidatesObj = root.get("candidates");
            if (!(candidatesObj instanceof java.util.List)) return null;
            java.util.List<?> candidates = (java.util.List<?>) candidatesObj;
            if (candidates.isEmpty()) return null;
            Object first = candidates.get(0);
            if (!(first instanceof Map)) return null;
            Map<?, ?> cand = (Map<?, ?>) first;
            Object contentObj = cand.get("content");
            if (!(contentObj instanceof Map)) return null;
            Map<?, ?> content = (Map<?, ?>) contentObj;
            Object partsObj = content.get("parts");
            if (!(partsObj instanceof java.util.List)) return null;
            java.util.List<?> parts = (java.util.List<?>) partsObj;
            if (parts.isEmpty()) return null;
            Object part = parts.get(0);
            if (!(part instanceof Map)) return null;
            Object text = ((Map<?, ?>) part).get("text");
            return text != null ? text.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}


