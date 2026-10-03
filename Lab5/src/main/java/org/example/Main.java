package org.example;

import org.example.collection.CollectionManager;
import org.example.commands.CommandManager;
import org.example.util.InputReader;

import java.io.*;

/**
 * Точка входа в приложение управления коллекцией организаций.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class Main {

    /**
     * Главный метод приложения.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        String filePath = System.getenv("COLLECTION_FILE");
        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = "collection.xml";
        }

        System.out.println("=== Менеджер коллекции организаций ===");
        System.out.println("Файл: " + filePath);

        CollectionManager collectionManager = new CollectionManager(filePath);

        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            InputReader inputReader = new InputReader(consoleReader, true);
            CommandManager commandManager = new CommandManager(collectionManager, inputReader);

            System.out.println("Введите команду (help — справка):");

            String line;
            while (true) {
                System.out.print("> ");
                try {
                    line = consoleReader.readLine();
                } catch (IOException e) {
                    System.out.println("Ошибка чтения: " + e.getMessage());
                    break;
                }
                if (line == null) {
                    System.out.println("\nКонец ввода. Завершение программы.");
                    break;
                }
                commandManager.processLine(line);
            }
        } catch (IOException e) {
            System.out.println("Критическая ошибка: " + e.getMessage());
        }
    }
}
