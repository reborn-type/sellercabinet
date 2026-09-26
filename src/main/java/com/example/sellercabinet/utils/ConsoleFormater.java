package com.example.sellercabinet.utils;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleFormater {
    public Integer readInteger(Scanner scanner) {
        while (true) {
            String value = scanner.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.print("Введите целое число ещё раз: ");
            }
        }
    }

    public Double readDouble(Scanner scanner) {
        while (true) {
            String value = scanner.nextLine().trim().replace(',', '.');

            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                System.out.print("Введите число ещё раз: ");
            }
        }
    }

    public String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    public Integer readAge(
            Scanner scanner
    ) {
        while (true) {
            System.out.print(
                    "Возраст от 18 до 110: "
            );

            String value = scanner.nextLine()
                    .trim();

            if (value.isBlank()) {
                System.out.println(
                        "Возраст не может быть пустым."
                );
                continue;
            }

            final int age;

            try {
                age = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Введите целое число, например 25."
                );
                continue;
            }

            if (age < 18 || age > 110) {
                System.out.println(
                        "Возраст должен быть от 18 до 110."
                );
                continue;
            }

            return age;
        }
    }

    public String readRequiredString(
            Scanner scanner,
            String message
    ) {
        while (true) {
            System.out.print(message);

            String value = scanner.nextLine()
                    .trim();

            if (value.isBlank()) {
                System.out.println(
                        "Поле не может быть пустым."
                );
                continue;
            }

            return value;
        }
    }

    public String readNameString(
            Scanner scanner,
            String message
    ) {
        while(true) {
            System.out.print(message);

            String value = scanner.nextLine()
                    .trim();

            if (value.isBlank()) {
                System.out.println(
                        "Поле не может быть пустым."
                );
                continue;
            }

            if (value.length() < 2 || value.length() > 50) {
                System.out.println("Длина должна быть от 2 до 50 символов.");
                continue;
            }

            if (!value.matches("^[\\p{L}]+(?:[ -][\\p{L}]+)*$")) {
                System.out.println("Используйте только буквы и дефис.");
                continue;
            }

            if(value.contains(" ")){
                System.out.println("Поле не должно содержать пробелы.");
                continue;
            }

            return value;
        }
    }

    public String readEmail(
            Scanner scanner
    ) {
        while (true) {
            System.out.print("Email: ");

            String email = scanner.nextLine()
                    .trim();

            if (email.isBlank()) {
                System.out.println("Email не может быть пустым.");
                continue;
            }

            if (email.length() > 50 || email.length() < 5) {
                System.out.println("Длина email не может быть больше 50 символов или меньше 5 символов.");
                continue;
            }

            if(email.contains(" ")){
                System.out.println("В почте не может быть пробелов");
                continue;
            }

            if(!email.contains("@")){
                System.out.println("Не указан домен почты.");
                continue;
            }

            return email;
        }
    }

    public Integer readNonNegativeInteger(
            Scanner scanner,
            String message
    ) {
        while (true) {
            System.out.print(message);

            String value = scanner.nextLine().trim();

            if (value.isBlank()) {
                System.out.println("Значение не может быть пустым.");
                continue;
            }

            try {
                int number = Integer.parseInt(value);

                if (number < 0) {
                    System.out.println("Значение не может быть отрицательным.");
                    continue;
                }

                return number;

            } catch (NumberFormatException e) {
                System.out.println("Введите целое число.");
            }
        }
    }

    public Double readNonNegativeDouble(
            Scanner scanner,
            String message
    ) {
        while (true) {
            System.out.print(message);

            String value = scanner.nextLine()
                    .trim()
                    .replace(',', '.');

            if (value.isBlank()) {
                System.out.println("Значение не может быть пустым.");
                continue;
            }

            try {
                double number = Double.parseDouble(value);

                if (!Double.isFinite(number)) {
                    System.out.println("Введите обычное число.");
                    continue;
                }

                if (number < 0) {
                    System.out.println("Значение не может быть отрицательным.");
                    continue;
                }
                return number;

            } catch (NumberFormatException e) {
                System.out.println("Введите число, например 100.50.");
            }
        }
    }

    public Double readRating(
            Scanner scanner,
            String message
    ) {
        while (true) {
            System.out.print(message);

            String value = scanner.nextLine()
                    .trim()
                    .replace(',', '.');

            if (value.isBlank()) {
                System.out.println("Оценка не может быть пустой.");
                continue;
            }

            try {
                double rating = Double.parseDouble(value);

                if (!Double.isFinite(rating)) {
                    System.out.println("Введите обычное число.");
                    continue;
                }

                if (rating < 1 || rating > 5) {
                    System.out.println("Оценка должна быть от 1 до 5.");
                    continue;
                }

                return rating;

            } catch (NumberFormatException e) {
                System.out.println("Введите число от 1 до 5.");
            }
        }
    }
}
