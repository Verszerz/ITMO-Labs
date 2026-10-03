package org.example.client;

import org.example.network.CommandRequest;
import org.example.network.CommandResponse;
import org.example.server.Serializer;

import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

/**
 * Клиентский отправщик/получатель UDP-датаграмм.
 *
 * <p>Использует {@link DatagramSocket} и {@link DatagramPacket}
 * для отправки запросов и получения ответов от сервера.</p>
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class ClientSender {

    private static final Logger logger = Logger.getLogger(ClientSender.class.getName());

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final int BUFFER_SIZE = 65536;
    private static final int TIMEOUT_MS = 5000; // 5 секунд ожидания ответа

    private final DatagramSocket socket;
    private final InetAddress serverAddress;

    /**
     * Создаёт клиентский сокет.
     *
     * @throws IOException если не удалось создать сокет или разрешить адрес
     */
    public ClientSender() throws IOException {
        socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT_MS);
        // setSoTimeout — receive() бросит SocketTimeoutException
        // если ответ не пришёл за TIMEOUT_MS миллисекунд
        serverAddress = InetAddress.getByName(SERVER_HOST);
    }

    /**
     * Отправляет запрос серверу и получает ответ.
     *
     * <p>Если сервер недоступен (таймаут) — возвращает ответ с ошибкой,
     * не крашит программу.</p>
     *
     * @param request запрос для отправки
     * @return ответ от сервера или ответ об ошибке
     */
    public CommandResponse sendAndReceive(CommandRequest request) {
        try {
            // Сериализуем запрос в байты
            byte[] requestData = Serializer.serialize(request);

            // Отправка — DatagramPacket
            DatagramPacket sendPacket = new DatagramPacket(
                    requestData, requestData.length, serverAddress, SERVER_PORT);
            socket.send(sendPacket);
            logger.info("Отправлен запрос: " + request.getType());

            // Получение ответа
            byte[] responseBuffer = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(receivePacket);
            // receive() заблокируется до ответа или таймаута

            // Десериализуем ответ
            byte[] responseData = new byte[receivePacket.getLength()];
            System.arraycopy(receivePacket.getData(), 0, responseData, 0, receivePacket.getLength());

            CommandResponse response = (CommandResponse) Serializer.deserialize(responseData);
            logger.info("Получен ответ: " + (response.isSuccess() ? "OK" : "ERROR"));
            return response;

        } catch (SocketTimeoutException e) {
            // Сервер недоступен — корректно обрабатываем
            logger.warning("Сервер недоступен (таймаут " + TIMEOUT_MS + "мс)");
            return new CommandResponse(
                    "Сервер недоступен. Проверьте что сервер запущен на порту " + SERVER_PORT + ".",
                    false);
        } catch (IOException e) {
            logger.warning("Ошибка сети: " + e.getMessage());
            return new CommandResponse("Ошибка сети: " + e.getMessage(), false);
        } catch (ClassNotFoundException e) {
            logger.warning("Ошибка десериализации ответа: " + e.getMessage());
            return new CommandResponse("Ошибка получения ответа от сервера.", false);
        }
    }

    /**
     * Закрывает сокет.
     */
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
