package service;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleService.class);
    private final Scanner scanner;

    public ConsoleService(Scanner scanner) {
        this.scanner = scanner;
    }

    public void print(String message) {
        LOGGER.info(message);
        System.out.println(message);
    }

    public String readString(String prompt) {
        LOGGER.info(prompt);
        System.out.print(prompt + " ");
        return scanner.nextLine();
    }

    public int readInt(String prompt) {
        LOGGER.info(prompt);
        System.out.print(prompt + " ");
        while (!scanner.hasNextInt()) {
            scanner.next(); // érvénytelen input eldobása
            System.out.print(prompt + " ");
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // buffer ürítése
        return value;
    }
}
