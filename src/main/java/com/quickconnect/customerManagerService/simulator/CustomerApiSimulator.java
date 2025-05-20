package com.quickconnect.customerManagerService.simulator;

import com.quickconnect.customerManagerService.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerApiSimulator {

    private static final String[] FIRST_NAMES = {
            "Leia", "Sadie", "Jose", "Sara", "Frank",
            "Dewey", "Tomas", "Joel", "Lukas", "Carlos",
            "Alice", "Bob", "Carol", "David", "Eve",
            "Grace", "Hank", "Ivy", "John"
    };

    private static final String[] LAST_NAMES = {
            "Liberty", "Ray", "Harrison", "Ronan", "Drew",
            "Powell", "Larsen", "Chan", "Anderson", "Lane",
            "Anderson", "Brown", "Chen", "Doe", "Evans",
            "Garcia", "Hall", "Ivanov", "Jones", "Khan"
    };

    private static final String BASE_URL = "https://wtqdw7xw41.execute-api.us-east-2.amazonaws.com/Prod/customers";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();
    private static final AtomicInteger idGenerator = new AtomicInteger(1000); // starts from 1000

    private static List<Customer> generateRandomCustomers(int count) {
        List<Customer> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            int age = 10 + random.nextInt(81); // 10 to 90
            int id = idGenerator.getAndIncrement();

            Customer c = new Customer(firstName, lastName, age, id);
            list.add(c);
        }
        return list;
    }
    private static void sendPostRequest() {
        try {
            List<Customer> customers = generateRandomCustomers(2 + random.nextInt(3)); // 2–4 customers
            String body = objectMapper.writeValueAsString(customers);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(res -> {
                        System.out.println("[POST] Status: " + res.statusCode());
                        System.out.flush();
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendGetRequest() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> System.out.println("[GET] Status: " + res.statusCode() + ", Response: " + res.body()));
    }

    public static void main(String[] args) throws Exception {
        System.out.println("🚀 Running Customer API Simulator...");

        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            futures.add(CompletableFuture.runAsync(() -> sendPostRequest(), executor));
            futures.add(CompletableFuture.runAsync(() -> sendGetRequest(), executor));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        Thread.sleep(2000);
        executor.shutdown();
    }
}
