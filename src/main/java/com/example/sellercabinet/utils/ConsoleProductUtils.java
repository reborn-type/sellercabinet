package com.example.sellercabinet.utils;
import com.example.sellercabinet.dto.ProductResponse;
import com.example.sellercabinet.utils.ConsolePrintUtils;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Scanner;
import com.example.sellercabinet.utils.ConsoleFormater;

@Component
public class ConsoleProductUtils {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConsolePrintUtils consolePrintUtils;
    private final ConsoleFormater consoleFormater;

    public ConsoleProductUtils(ConsoleFormater consoleFormater, ConsolePrintUtils consolePrintUtils ) {
        this.consoleFormater = consoleFormater;
        this.consolePrintUtils = consolePrintUtils;
    }

    public void createProduct(Long sellerId, Scanner scanner, boolean logsNeed) throws Exception{
        System.out.println("\n--- Создание товара ---");
        String name = consoleFormater.readRequiredString(scanner, "Название: ");
        Double price = consoleFormater.readNonNegativeDouble(scanner, "Цена: ");
        Integer count = consoleFormater.readNonNegativeInteger(scanner, "Количество: ");
        Integer countOfSales = consoleFormater.readNonNegativeInteger(scanner, "Количество продаж: ");
        Double averageEstimation = consoleFormater.readRating(scanner, "Средняя оценка: ");

        String body = """
            {
              "name": "%s",
              "price": %s,
              "count": %d,
              "countOfSales": %d,
              "averageEstimation": %s
            }
            """.formatted(
                consoleFormater.escapeJson(name),
                price,
                count,
                countOfSales,
                averageEstimation
        );

        String url = "http://localhost:8080/api/sellers/" + sellerId + "/products";

        consolePrintUtils.requestAndPrint("POST", url, body, logsNeed);
    }

    public void getAllProducts(boolean logsNeed) throws Exception {
        HttpResponse<String> response = ConsoleHttpClient.sendRequest("GET", "http://localhost:8080/api/sellers/products",
                "", logsNeed);
        int status = response.statusCode();

        if (status < 200 || status >= 300) {
            consolePrintUtils.printError(response);
            return;
        }

        List<ProductResponse> products = objectMapper.readValue(response.body(), objectMapper.getTypeFactory()
                .constructCollectionType(List.class, ProductResponse.class));
        consolePrintUtils.printProducts(products);
    }

    public void deleteProduct(Long sellerId, Scanner scanner, boolean logsNeed) throws Exception{
        System.out.println("\n--- Удаление товара ---");
        System.out.print("Введите ID товара: ");
        Long productId;
        try {
            productId = Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID товара должен быть числом.");
            return;
        }
        String url = "http://localhost:8080/api/sellers/product/" + productId;
        consolePrintUtils.requestAndPrint("DELETE", url, "", logsNeed);
    }

    public void getSellerProducts(Long sellerId, boolean logsNeed) throws Exception {
        String url = "http://localhost:8080/api/sellers/" + sellerId + "/products";

        HttpResponse<String> response = ConsoleHttpClient.sendRequest("GET", url, "", logsNeed);

        int status = response.statusCode();

        if (status < 200 || status >= 300) {
            consolePrintUtils.printError(response);
            return;
        }

        List<ProductResponse> products = objectMapper.readValue(response.body(), objectMapper.getTypeFactory()
                .constructCollectionType(List.class, ProductResponse.class));

        System.out.println("\n--- Товары продавца " + sellerId + " ---");

        consolePrintUtils.printProducts(products);
    }

    public void getAllSales(boolean logsNeed) throws Exception {
        String url = "http://localhost:8080/api/sellers/products/sales";
        HttpResponse<String> response = ConsoleHttpClient.sendRequest("GET", url, "", logsNeed);
        System.out.println("\nОбщее количество товаров, проданных на маркетплейсе: " + response);
    }
}
