package org.example.server;

import org.example.collection.CollectionManager;
import org.example.commands.CommandDispatcher;
import org.example.network.CommandRequest;
import org.example.network.CommandResponse;
import org.example.network.CommandType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * UDP-сервер для обработки команд управления коллекцией.
 *
 * <p>Архитектура сервера (однопоточная, неблокирующий режим):</p>
 * <ul>
 *   <li>Модуль приёма — {@link DatagramChannel} в неблокирующем режиме</li>
 *   <li>Модуль чтения запроса — десериализация из UDP-датаграммы</li>
 *   <li>Модуль обработки — {@link CommandDispatcher}</li>
 *   <li>Модуль отправки — сериализация и отправка ответа</li>
 * </ul>
 *
 * <p>Сервер использует {@link Selector} для мультиплексирования
 * в неблокирующем режиме — не блокирует поток на ожидании данных.</p>
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private static final int BUFFER_SIZE = 65536;

    private static final int PORT = 12345;

    private final CollectionManager manager;
    private final CommandDispatcher dispatcher;

    private DatagramChannel channel;
    private Selector selector;
    private volatile boolean running = true;

    /**
     * @param manager менеджер коллекции
     */
    public Server(CollectionManager manager) {
        this.manager = manager;
        this.dispatcher = new CommandDispatcher(manager);
    }

    /**
     * Запускает сервер.
     * Инициализирует канал, запускает цикл обработки запросов
     * и поток чтения серверных команд из консоли.
     */
    public void start() {
        try {
            initChannel();
            logger.info("Сервер запущен на порту " + PORT);
            System.out.println("Сервер запущен на порту " + PORT);
            System.out.println("Команды сервера: save — сохранить коллекцию, exit — завершить");

            // Поток для чтения серверных команд (save, exit)
            Thread consoleThread = new Thread(this::readServerCommands, "server-console");
            consoleThread.setDaemon(true);
            consoleThread.start();

            // Основной цикл обработки UDP-запросов
            runLoop();

        } catch (IOException e) {
            logger.severe("Ошибка запуска сервера: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    /**
     * Инициализирует DatagramChannel в неблокирующем режиме.
     *
     * @throws IOException при ошибке создания канала
     */
    private void initChannel() throws IOException {
        // Модуль приёма подключений
        channel = DatagramChannel.open();
        channel.configureBlocking(false); // НЕБЛОКИРУЮЩИЙ режим
        channel.bind(new InetSocketAddress(PORT));

        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
        // OP_READ — канал готов к чтению (пришла датаграмма)
    }

    /**
     * Основной цикл обработки запросов.
     * Использует Selector для неблокирующего ожидания.
     *
     * @throws IOException при ошибке ввода-вывода
     */
    private void runLoop() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (running) {
            // select() — ждём событий, но не дольше 1 секунды
            // (чтобы можно было проверить флаг running)
            int ready = selector.select(1000);
            if (ready == 0) continue;

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isReadable()) {
                    // Модуль чтения запроса
                    buffer.clear();
                    SocketAddress clientAddr = channel.receive(buffer);
                    // receive() в неблокирующем режиме — не блокирует поток
                    // возвращает null если данных нет (но у нас Selector гарантирует наличие)

                    if (clientAddr != null) {
                        logger.info("Получен запрос от: " + clientAddr);
                        handleRequest(buffer, clientAddr);
                    }
                }
            }
        }
    }

    /**
     * Обрабатывает один запрос от клиента.
     * Модуль чтения + Модуль обработки + Модуль отправки.
     *
     * @param buffer     буфер с данными
     * @param clientAddr адрес клиента
     */
    private void handleRequest(ByteBuffer buffer, SocketAddress clientAddr) {
        try {
            // Модуль чтения — десериализация
            buffer.flip(); // переключаем буфер в режим чтения
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            CommandRequest request = (CommandRequest) Serializer.deserialize(data);
            logger.info("Команда: " + request.getType());

            // Модуль обработки команды
            CommandResponse response = dispatcher.dispatch(request);

            // Модуль отправки ответа
            sendResponse(response, clientAddr);

        } catch (Exception e) {
            logger.warning("Ошибка обработки запроса: " + e.getMessage());
            try {
                sendResponse(new CommandResponse("Ошибка сервера: " + e.getMessage(), false), clientAddr);
            } catch (IOException ex) {
                logger.severe("Не удалось отправить ответ об ошибке: " + ex.getMessage());
            }
        }
    }

    /**
     * Отправляет ответ клиенту.
     *
     * @param response   ответ
     * @param clientAddr адрес клиента
     * @throws IOException при ошибке отправки
     */
    private void sendResponse(CommandResponse response, SocketAddress clientAddr) throws IOException {
        byte[] data = Serializer.serialize(response);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        channel.send(buffer, clientAddr);
        logger.info("Ответ отправлен клиенту: " + clientAddr + " (" + data.length + " байт)");
    }

    /**
     * Читает команды с консоли сервера (save, exit).
     * Выполняется в отдельном потоке.
     */
    private void readServerCommands() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (running) {
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim().toLowerCase();
                    switch (line) {
                        case "save":
                            // Серверная команда save — клиент её отправить не может
                            dispatcher.dispatch(new CommandRequest(CommandType.SERVER_SAVE));
                            System.out.println("Коллекция сохранена.");
                            break;
                        case "exit":
                            System.out.println("Завершение сервера...");
                            saveAndStop();
                            break;
                        default:
                            System.out.println("Неизвестная команда. Доступно: save, exit");
                    }
                }
            }
        }
    }

    /**
     * Сохраняет коллекцию и останавливает сервер.
     */
    private void saveAndStop() {
        try {
            manager.save();
            logger.info("Коллекция сохранена при завершении");
        } catch (IOException e) {
            logger.warning("Ошибка сохранения при завершении: " + e.getMessage());
        }
        running = false;
        selector.wakeup(); // прерываем selector.select()
    }

    /**
     * Освобождает ресурсы.
     */
    private void shutdown() {
        try {
            if (channel != null) channel.close();
            if (selector != null) selector.close();
            logger.info("Сервер остановлен.");
        } catch (IOException e) {
            logger.warning("Ошибка закрытия ресурсов: " + e.getMessage());
        }
    }
}
