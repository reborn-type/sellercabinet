package com.example.sellercabinet.utils;

import com.example.sellercabinet.dto.ProductResponse;
import com.example.sellercabinet.dto.SellerListItem;
import com.example.sellercabinet.dto.SellerResponse;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class ConsolePrintUtils {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void printSellers(List<SellerListItem> sellers) {
        System.out.println("\n--- Список пользователей ---");

        if (sellers == null || sellers.isEmpty()) {
            System.out.println("Пользователей нет.");
            return;
        }

        System.out.printf(
                "%-5s %-20s %-20s %-8s %-30s%n",
                "ID",
                "Имя",
                "Фамилия",
                "Возраст",
                "Email"
        );

        System.out.println("-".repeat(90));

        for (SellerListItem seller : sellers) {
            System.out.printf(
                    "%-5s %-20s %-20s %-8s %-30s%n",
                    seller.getSellerId(),
                    seller.getFirstName(),
                    seller.getLastName(),
                    seller.getAge(),
                    seller.getEmail()
            );
        }
    }

    public void printError(HttpResponse<String> response) {
        System.out.println("\nОшибка HTTP: " + response.statusCode());

        String body = response.body();

        if (body == null || body.isBlank()) {
            System.out.println("Сервер не вернул описание ошибки.");
            return;
        }

        try {
            var json = objectMapper.readTree(body);
            System.out.println(json.toPrettyString());
        } catch (Exception e) {
            System.out.println(body);
        }
    }



    public void printProducts(List<ProductResponse> products) {
        System.out.println("\n--- Список товаров ---");

        if (products == null || products.isEmpty()) {
            System.out.println("Товаров нет.");
            return;
        }

        System.out.printf(
                "%-5s %-25s %-12s %-10s %-10s %-12s%n",
                "ID",
                "Название",
                "Цена",
                "Кол-во",
                "Продаж",
                "Оценка"
        );

        System.out.println("-".repeat(80));

        for (ProductResponse product : products) {
            System.out.printf(
                    "%-5s %-25s %-12.2f %-10s %-10s %-12.2f%n",
                    product.getProductId(),
                    product.getName(),
                    product.getPrice(),
                    product.getCount(),
                    product.getCountOfSales(),
                    product.getAverageEstimation()
            );
        }
    }

    public void requestAndPrint(String method, String url, String body, boolean logsNeed) throws Exception {
        HttpResponse<String> response = ConsoleHttpClient.sendRequest(method, url, body, logsNeed);

        int status = response.statusCode();

        if (status == 204) {
            System.out.println("Операция выполнена успешно.");
            return;
        }

        if (!logsNeed) {
            System.out.println("HTTP status: " + response.statusCode());
            System.out.println(response.body());
            if (response.body() == null || response.body().isBlank()) {
                System.out.println("Сервер вернул пустой ответ.");
            } else {
                System.out.println(response.body());
            }
        }
    }

    public void showProfile(SellerResponse seller) {
        System.out.println("\n--- Мой профиль ---");
        System.out.println("ID: " + seller.getSellerId());
        System.out.println("Имя: " + seller.getLastName() + " " + seller.getFirstName());
        System.out.println("Email: " + seller.getEmail());
        System.out.println("Возраст: " + seller.getAge());
    }

    public void printUserMenu() {
        System.out.println("\n--- Меню пользователя ---");
        System.out.println("1. Мой профиль");
        System.out.println("2. Мои товары");
        System.out.println("3. Мои продажи");
        System.out.println("4. Добавить товар");
        System.out.println("5. Удалить товар");
        System.out.println("6. Показывать/скрывать логи");
        System.out.println("7. Выйти в меню входа");
        System.out.println("8. Удалить мой профиль");
        System.out.println("9. Завершить программу");
        System.out.print("Выберите пункт: ");
    }

    public void printAdminMenu() {
        System.out.println("\n--- Меню администратора ---");
        System.out.println("1. Все пользователи");
        System.out.println("2. Все товары");
        System.out.println("3. Общее количество продаж");
        System.out.println("4. Добавить товар");
        System.out.println("5. Удалить пользователя");
        System.out.println("6. Показывать/скрывать логи");
        System.out.println("7. Выйти в меню входа");
        System.out.println("8. Завершить программу");
        System.out.print("Выберите пункт: ");
    }

}
