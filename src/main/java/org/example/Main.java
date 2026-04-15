package org.example;

import java.io.*;
import org.example.collection.CollectionManager;
import org.example.commands.CommandManager;
import org.example.util.InputReader;

/**
 * Точка входа в приложение управления коллекцией организаций.
 *
 * <p>Поддерживает редиректы вывода в файл без внешних библиотек:</p>
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
            System.out.println("Поддерживаются редиректы: команда > файл  и  команда >> файл");

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
                if (line.trim().isEmpty()) continue;

                executeWithRedirect(line, commandManager);
            }
        } catch (IOException e) {
            System.out.println("Критическая ошибка: " + e.getMessage());
        }
    }

    /**
     * Разбирает строку на команду и редирект, выполняет с перехватом вывода.
     *
     * @param line           строка ввода
     * @param commandManager менеджер команд
     */
    private static void executeWithRedirect(String line, CommandManager commandManager) {
        int doubleIdx = line.lastIndexOf(">>");
        int singleIdx = line.lastIndexOf(">");

        String  command;
        String  targetFile;
        boolean append;

        if (doubleIdx >= 0) {
            command    = line.substring(0, doubleIdx).trim();
            targetFile = line.substring(doubleIdx + 2).trim();
            append     = true;
        } else if (singleIdx >= 0) {
            command    = line.substring(0, singleIdx).trim();
            targetFile = line.substring(singleIdx + 1).trim();
            append     = false;
        } else {
            commandManager.processLine(line);
            return;
        }

        if (command.isEmpty()) {
            System.out.println("Ошибка: команда не указана перед редиректом.");
            return;
        }
        if (targetFile == null || targetFile.isEmpty()) {
            System.out.println("Ошибка: не указан файл для редиректа.");
            return;
        }

        ByteArrayOutputStream captureBuffer = new ByteArrayOutputStream();

        PrintStream captureOut;
        try {
            captureOut = new PrintStream(
                new TeeOutputStream(captureBuffer, System.out), true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Ошибка кодировки: " + e.getMessage());
            return;
        }

        PrintStream originalOut = System.out;

        System.setOut(captureOut);

        try {
            commandManager.processLine(command);
        } finally {
            System.setOut(originalOut);
            captureOut.flush();
        }

        File file = new File(targetFile);
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(file, append)) {
                captureBuffer.writeTo(fos);
            }

            System.out.println("Вывод " + (append ? "дозаписан" : "записан") + " в файл: " + targetFile);

        } catch (IOException e) {
            System.out.println("Ошибка записи в файл \''" + targetFile + "\': " + e.getMessage());
        }
    }

    /**
     * Поток-разветвитель: всё что в него пишется — дублируется
     * одновременно в два потока (primary и secondary).
     *
     * <p>Используется чтобы вывод команды шёл одновременно
     * в буфер (для записи в файл) и в консоль (для отображения пользователю).</p>
     *
     */
    private static class TeeOutputStream extends OutputStream {

        private final OutputStream primary;
        private final OutputStream secondary;

        /**
         * @param primary   первичный поток (буфер)
         * @param secondary вторичный поток (консоль)
         */
        TeeOutputStream(OutputStream primary, OutputStream secondary) {
            this.primary   = primary;
            this.secondary = secondary;
        }

        /**
         * Записывает один байт в оба потока.
         *
         * @param b байт для записи
         * @throws IOException при ошибке записи
         */
        @Override
        public void write(int b) throws IOException {
            primary.write(b);
            secondary.write(b);
        }

        /**
         * Записывает массив байт в оба потока.
         *
         * @param b   массив байт
         * @param off смещение
         * @param len длина
         * @throws IOException при ошибке записи
         */
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            primary.write(b, off, len);
            secondary.write(b, off, len);
        }

        /**
         * Сбрасывает буферы обоих потоков.
         *
         * @throws IOException при ошибке сброса
         */
        @Override
        public void flush() throws IOException {
            primary.flush();
            secondary.flush();
        }

        /**
         * Закрывает оба потока.
         *
         * @throws IOException при ошибке закрытия
         */
        @Override
        public void close() throws IOException {
            try { primary.close(); }
            finally { secondary.close(); }
        }
    }
}
