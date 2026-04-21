package org.example.commands;

import org.example.network.CommandRequest;
import org.example.network.CommandResponse;

/**
 * Интерфейс обработчика команды на сервере.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public interface CommandHandler {

    /**
     * Обрабатывает запрос и возвращает ответ.
     *
     * @param request запрос от клиента
     * @return ответ для отправки клиенту
     */
    CommandResponse handle(CommandRequest request);
}
