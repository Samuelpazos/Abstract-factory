package com.bank.fraud;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GroqClient {
    private static final URI API_URI = URI.create("https://api.groq.com/openai/v1/chat/completions");
    private static final String MODEL = "llama3-70b-8192";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public GroqClient() {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(),
                new ObjectMapper(), System.getenv("GROQ_API_KEY"));
    }

    GroqClient(HttpClient httpClient, ObjectMapper objectMapper, String apiKey) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    public String ask(String prompt) throws IOException, InterruptedException {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GROQ_API_KEY is not set.");
        }

        String requestBody = objectMapper.createObjectNode()
                .put("model", MODEL)
                .put("temperature", 0)
                .putArray("messages")
                .addObject()
                .put("role", "user")
                .put("content", prompt)
                .toString();

        HttpRequest request = HttpRequest.newBuilder(API_URI)
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[Groq raw response] " + response.body());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Groq returned HTTP " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode content = root.path("choices").path(0).path("message").path("content");
        if (!content.isTextual() || content.asText().isBlank()) {
            throw new IOException("Groq response did not contain message content.");
        }
        return content.asText().trim();
    }
}
