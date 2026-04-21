package org.example.server;

import org.example.collection.CollectionManager;

import java.util.logging.Logger;

/**
 * Точка входа серверного приложения.
 *
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class ServerMain {

    private static final Logger logger = Logger.getLogger(ServerMain.class.getName());

    /**
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        logger.info("Запуск серверного приложения");

        String filePath = System.getenv("COLLECTION_FILE");
        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = "collection.xml";
            logger.warning("COLLECTION_FILE не задана, используется collection.xml");
        }

        CollectionManager manager = new CollectionManager(filePath);
        Server server = new Server(manager);
        server.start();
    }
}
