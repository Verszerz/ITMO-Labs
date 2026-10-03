package org.example.client;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Точка входа клиентского приложения.
 *
 * <p>Запуск: java -cp lab6.jar org.example.client.ClientMain</p>
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class ClientMain {

    private static final Logger logger = Logger.getLogger(ClientMain.class.getName());

    /**
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        logger.info("Запуск клиентского приложения");
        try {
            ClientSender sender = new ClientSender();
            ClientConsole console = new ClientConsole(sender);
            console.run();
        } catch (IOException e) {
            System.out.println("Не удалось инициализировать клиент: " + e.getMessage());
            logger.severe("Ошибка инициализации клиента: " + e.getMessage());
        }
    }
}
