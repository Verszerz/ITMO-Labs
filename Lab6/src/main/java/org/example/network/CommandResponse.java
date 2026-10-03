package org.example.network;

import org.example.model.Organization;

import java.io.Serializable;
import java.util.List;

/**
 * Ответ сервера клиенту.
 *
 * <p>Содержит текстовый результат выполнения команды
 * и опционально список организаций (для show, filter и т.д.).
 * Передаётся в сериализованном виде по UDP.</p>
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class CommandResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Текст ответа для вывода в консоль */
    private final String message;

    /**
     * Список организаций (для команд show, print_ascending, filter_*).
     * Отсортирован по размеру (annualTurnover).
     * null если команда не возвращает организации.
     */
    private final List<Organization> organizations;

    /** true если команда выполнена успешно */
    private final boolean success;

    /**
     * Создаёт ответ только с текстовым сообщением.
     *
     * @param message текст ответа
     * @param success успех выполнения
     */
    public CommandResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.organizations = null;
    }

    /**
     * Создаёт ответ с текстом и списком организаций.
     *
     * @param message       текст ответа
     * @param organizations список организаций
     * @param success       успех выполнения
     */
    public CommandResponse(String message, List<Organization> organizations, boolean success) {
        this.message = message;
        this.organizations = organizations;
        this.success = success;
    }

    /** @return текст ответа */
    public String getMessage() { return message; }

    /** @return список организаций или null */
    public List<Organization> getOrganizations() { return organizations; }

    /** @return true если команда выполнена успешно */
    public boolean isSuccess() { return success; }
}
