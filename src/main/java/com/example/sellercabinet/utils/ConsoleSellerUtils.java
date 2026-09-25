package com.example.sellercabinet.utils;

import com.example.sellercabinet.dto.SellerListItem;
import com.example.sellercabinet.dto.SellerResponse;
import com.example.sellercabinet.utils.ConsolePrintUtils;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.util.List;
import com.example.sellercabinet.utils.ConsoleFormater;
import java.util.Scanner;

@Component
public class ConsoleSellerUtils {

    private final ConsolePrintUtils consolePrintUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConsoleFormater consoleFormater;

    public ConsoleSellerUtils(ConsoleFormater consoleFormater, ConsolePrintUtils consolePrintUtils) {
        this.consoleFormater = consoleFormater;
        this.consolePrintUtils = consolePrintUtils;
    }

    public SellerResponse loadSeller(Long sellerId) throws Exception {

        String url = "http://localhost:8080/api/sellers/" + sellerId;

        HttpResponse<String> response =
                ConsoleHttpClient.sendRequest("GET", url, "", false);

        if (response.statusCode() != 200) {
            System.out.println("Сервер вернул HTTP " + response.statusCode());
            System.out.println(response.body());
            return null;
        }

        return objectMapper.readValue(response.body(), SellerResponse.class);
    }

    public void getAllSellers(boolean logsNeed) throws Exception {
        HttpResponse<String> response = ConsoleHttpClient.sendRequest("GET", "http://localhost:8080/api/sellers",
                "", logsNeed);
        int status = response.statusCode();

        if (status < 200 || status >= 300) {
            consolePrintUtils.printError(response);
            return;
        }

        List<SellerListItem> sellers = objectMapper.readValue(response.body(), objectMapper.getTypeFactory()
                .constructCollectionType(List.class, SellerListItem.class));

        consolePrintUtils.printSellers(sellers);
    }

    public void createSeller(
            Scanner scanner
    ) throws Exception {

        System.out.println(
                "\n--- Создание пользователя ---"
        );

        String firstName = consoleFormater.readRequiredString(scanner, "Имя: ");

        String lastName =  consoleFormater.readRequiredString(scanner, "Фамилия: ");

        Integer age = consoleFormater.readAge(scanner);

        String email = consoleFormater.readEmail(scanner);

        String body = """
            {
              "firstName": "%s",
              "lastName": "%s",
              "age": %d,
              "email": "%s"
            }
            """.formatted(
                consoleFormater.escapeJson(firstName),
                consoleFormater.escapeJson(lastName),
                age,
                consoleFormater.escapeJson(email)
        );

        HttpResponse<String> response =
                ConsoleHttpClient.sendRequest(
                        "POST",
                        "http://localhost:8080/api/sellers",
                        body,
                        false
                );

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            consolePrintUtils.printError(response);
            return;
        }

        SellerResponse created = objectMapper.readValue(response.body(), SellerResponse.class);

        System.out.println("\nПользователь успешно создан.");
        System.out.println("Ваш ID: " + created.getSellerId());
        System.out.println("Имя Фамилия: " + created.getFirstName() + " " + created.getLastName());
        System.out.println("Возраст" + created.getAge());
        System.out.println("Email: " + created.getEmail());
    }

    public boolean deleteOwnProfile(
            Long sellerId,
            Scanner scanner,
            boolean logsNeed
    ) throws Exception {

        System.out.println("\n--- Удаление профиля ---");

        System.out.print("Введите DELETE для подтверждения: ");

        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("DELETE")) {
            System.out.println("Удаление отменено.");
            return false;
        }

        String url = "http://localhost:8080/api/sellers/"+ sellerId;

        HttpResponse<String> response =
                ConsoleHttpClient.sendRequest(
                        "DELETE",
                        url,
                        "",
                        logsNeed
                );

        if (response.statusCode() == 204 || response.statusCode() == 200) {
            System.out.println("Профиль удалён.");
            return true;
        }

        consolePrintUtils.printError(response);
        return false;
    }

    public void getAllSellerSales(Long sellerId, boolean logsNeed) throws Exception{

        String url = "http://localhost:8080/api/sellers/"+ sellerId + "products/sales";
        HttpResponse<String> response = ConsoleHttpClient.sendRequest(
                "GET",
                url,
                "",
                logsNeed
        );
        System.out.println("\n--- У продавца "+sellerId + " ---");
        System.out.println("Общее количество продаж: " + response);

    }

    public boolean deleteSellerAsAdmin(Scanner scanner, boolean logsNeed) throws Exception {
        Long sellerId;

        while (true) {

            System.out.print("\nВведите id продавца, аккаунт которого нужно удалить (0, чтобы отменить):");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println(
                        "Удаление отменено."
                );
                return false;
            }

            if (input.isBlank()) {
                System.out.println(
                        "ID не может быть пустым."
                );
                continue;
            }

            try {
                sellerId = Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println(
                        "ID должен быть целым числом."
                );
                continue;
            }

            if (sellerId <= 0) {
                System.out.println(
                        "ID должен быть положительным."
                );
                continue;
            }

            break;
        }
        String url = "http://localhost:8080/api/sellers/" + sellerId;
        HttpResponse<String> response = ConsoleHttpClient.sendRequest("DELETE", url, "", logsNeed);

        if (response.statusCode() == 204 || response.statusCode() == 200) {
            System.out.println("Продавец удалён.");
            return true;
        }

        consolePrintUtils.printError(response);
        return false;
    }

}
