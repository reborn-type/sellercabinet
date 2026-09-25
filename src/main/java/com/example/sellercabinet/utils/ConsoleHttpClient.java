package com.example.sellercabinet.utils;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class ConsoleHttpClient {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    public static HttpResponse<String> sendRequest(String method, String url, String body, boolean is_logs_need) throws IOException, InterruptedException{
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Accept", "application/json"); 

        switch (method) {
            case "GET" -> builder.GET();
            case "POST" -> builder 
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(body));
            case "PUT" -> builder
                            .header("Content-Type", "application/json")
                            .PUT(HttpRequest.BodyPublishers.ofString(body));
            case "DELETE" -> builder.DELETE();
        }

        HttpRequest request = builder.build(); 

        HttpResponse <String> response = CLIENT.send(
                request, HttpResponse.BodyHandlers.ofString()
        );


        if (is_logs_need) {
            System.out.println("\n --- Ответ сервера ---");
            System.out.println("HTTP status: " + response.statusCode());
            System.out.println("Headers: " + response.headers().map());
            System.out.println("Body: ");
            System.out.println(response.body());
        }

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response;
        } else {
            throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
        }
        }
}