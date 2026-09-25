package com.example.sellercabinet.utils;

import com.example.sellercabinet.dto.SellerResponse;
import com.example.sellercabinet.repository.ProductRepository;
import com.example.sellercabinet.utils.ConsoleHttpClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import com.example.sellercabinet.dto.SellerListItem;
import com.example.sellercabinet.dto.ProductResponse;
import com.example.sellercabinet.service.ProductService;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.util.List;
import com.example.sellercabinet.utils.ConsolePrintUtils;
import com.example.sellercabinet.utils.ConsoleProductUtils;
import com.example.sellercabinet.utils.ConsoleSellerUtils;

@Component
public class ConsoleMenuRunner implements CommandLineRunner {
    private final ProductService productService;
    private final ConsoleProductUtils consoleProductUtils;
    private final ConsoleSellerUtils consoleSellerUtils;
    private final ConsolePrintUtils consolePrintUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ConsoleMenuRunner(ProductService productService, ConsoleProductUtils consoleProductUtils,
                             ConsoleSellerUtils consoleSellerUtils, ConsolePrintUtils consolePrintUtils) {
        this.consolePrintUtils = consolePrintUtils;
        this.consoleProductUtils = consoleProductUtils;
        this.consoleSellerUtils = consoleSellerUtils;
        this.productService = productService;

    }

    @Override
    public void run(String... args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true){
                boolean exit = startMenu(scanner);
                if (exit) {
                    System.out.println("Программа завершена.");
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка клиента: " + e.getMessage());
        }
    }


    private void printLoginMenu() {
        System.out.println("\n--- Вход ---");
        System.out.println("create. Создать пользователя");
        System.out.println("login. Войти по ID");
        System.out.println("admin. Войти как admin");
        System.out.println("exit. Завершить программу");
        System.out.print("Выберите пункт: ");
    }

    private boolean startMenu(Scanner scanner) throws Exception {
        printLoginMenu();

        String command = scanner.nextLine().trim().toLowerCase();

        switch (command) {
            case("create") -> {
                consoleSellerUtils.createSeller(scanner);
                return false;
            }

            case("login") -> {
                System.out.println("Введите ID пользователя или admin.");
                String value = scanner.nextLine().trim();
                try {
                    Long sellerId = Long.parseLong(value);
                    return openUserSession(scanner, sellerId);
                } catch (NumberFormatException e) {
                    System.out.println("ID должен быть числом.");
                    return false;
                }
            }

            case "admin" -> {
                return openAdminSession(scanner);
            }

            case "exit" -> {
                return true;
            }

            default -> {
                System.out.println("Неизвестный пункт.");
                return false;
            }
        }
    }

    private boolean openUserSession(
            Scanner scanner,
            Long sellerId
    ) throws Exception {

        SellerResponse seller =
                consoleSellerUtils.loadSeller(sellerId);

        if (seller == null) {
            System.out.println(
                    "Пользователь не найден."
            );
            return false;
        }

        System.out.printf(
                "Добро пожаловать, %s %s!%n",
                seller.getLastName(),
                seller.getFirstName()
        );

        return showUserMenu(
                scanner,
                sellerId,
                seller
        );
    }

    private boolean openAdminSession(
            Scanner scanner
    ) throws Exception {

        System.out.println(
                "Включён административный режим."
        );

        return showAdminMenu(scanner);
    }

    private boolean showUserMenu(
            Scanner scanner,
            Long sellerId,
            SellerResponse seller
    ) throws Exception {

        boolean logsNeed = false;

        while (true) {
            consolePrintUtils.printUserMenu();

            String command = scanner.nextLine()
                    .trim();

            switch (command) {
                case "1" -> {
                    consolePrintUtils.showProfile(seller);
                }

                case "2" -> {
                    consoleProductUtils.getSellerProducts(
                            sellerId,
                            logsNeed
                    );
                }

                case "3" -> {
                    consoleSellerUtils.getAllSellerSales(sellerId, logsNeed);
                }

                case "4" -> {
                    consoleProductUtils.createProduct(sellerId, scanner, logsNeed);
                }

                case "5" -> {
                    consoleProductUtils.deleteProduct(sellerId, scanner, logsNeed);
                }

                case "6" -> {
                    logsNeed = !logsNeed;
                    System.out.println(logsNeed ? "Логи включены." : "Логи выключены.");
                }

                case "7" -> {
                    System.out.println("Возврат в меню входа.");
                    return false;
                }

                case "8" -> {
                    boolean deleted = consoleSellerUtils.deleteOwnProfile(sellerId, scanner, logsNeed);

                    if (deleted) {
                        return false;
                    }
                }

                case "9" -> {
                    return true;
                }

                default -> System.out.println(
                        "Неизвестный пункт."
                );
            }
        }
    }

    private boolean showAdminMenu(
            Scanner scanner
    ) throws Exception {

        boolean logsNeed = false;

        while (true) {
            consolePrintUtils.printAdminMenu();

            String command = scanner.nextLine().trim();

            switch (command) {
                case "1" -> {
                    consoleSellerUtils.getAllSellers(logsNeed);
                }

                case "2" -> {
                    consoleProductUtils.getAllProducts(logsNeed);
                }

                case "3" -> {
                    consoleProductUtils.getAllSales(logsNeed);
                }

                case "4" -> {
                    System.out.println("Создание товаров из админ-меню " + "пока не реализовано.");
                }

                case "5" -> {
                    consoleSellerUtils.deleteSellerAsAdmin(scanner, logsNeed);
                }

                case "6" -> {
                    logsNeed = !logsNeed;

                    System.out.println(logsNeed ? "Логи включены." : "Логи выключены.");
                }

                case "7" -> {
                    System.out.println("Возврат в меню входа.");
                    return false;
                }

                case "8" -> {
                    return true;
                }

                default -> System.out.println(
                        "Неизвестный пункт."
                );
            }
        }
    }
}