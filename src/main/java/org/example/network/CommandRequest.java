package org.example.network;

import org.example.model.Organization;

import java.io.Serializable;

/**
 * Запрос от клиента к серверу.
 *
 * <p>Содержит тип команды и опциональные аргументы.
 * Передаётся в сериализованном виде по UDP.</p>
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class CommandRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Тип выполняемой команды */
    private final CommandType type;

    /**
     * Строковый аргумент команды.
     * Для remove_by_id — id, для filter_contains_name — подстрока, и т.д.
     */
    private final String stringArg;

    /**
     * Объект организации.
     * Для команд add и update.
     */
    private final Organization organizationArg;

    /**
     * Создаёт запрос только с типом команды (без аргументов).
     * Для: help, info, show, clear, exit, remove_head, print_ascending
     *
     * @param type тип команды
     */
    public CommandRequest(CommandType type) {
        this.type = type;
        this.stringArg = null;
        this.organizationArg = null;
    }

    /**
     * Создаёт запрос со строковым аргументом.
     * Для: remove_by_id, remove_greater, remove_lower,
     *      filter_contains_name, filter_less_than_annual_turnover, execute_script
     *
     * @param type      тип команды
     * @param stringArg строковый аргумент
     */
    public CommandRequest(CommandType type, String stringArg) {
        this.type = type;
        this.stringArg = stringArg;
        this.organizationArg = null;
    }

    /**
     * Создаёт запрос с объектом организации.
     * Для: add
     *
     * @param type            тип команды
     * @param organizationArg организация для добавления
     */
    public CommandRequest(CommandType type, Organization organizationArg) {
        this.type = type;
        this.stringArg = null;
        this.organizationArg = organizationArg;
    }

    /**
     * Создаёт запрос со строковым аргументом и объектом организации.
     * Для: update (id + новые данные)
     *
     * @param type            тип команды
     * @param stringArg       строковый аргумент (id)
     * @param organizationArg организация с новыми данными
     */
    public CommandRequest(CommandType type, String stringArg, Organization organizationArg) {
        this.type = type;
        this.stringArg = stringArg;
        this.organizationArg = organizationArg;
    }

    /** @return тип команды */
    public CommandType getType() { return type; }

    /** @return строковый аргумент или null */
    public String getStringArg() { return stringArg; }

    /** @return объект организации или null */
    public Organization getOrganizationArg() { return organizationArg; }

    @Override
    public String toString() {
        return "CommandRequest{type=" + type
             + (stringArg != null ? ", arg=" + stringArg : "")
             + (organizationArg != null ? ", org=" + organizationArg.getName() : "")
             + "}";
    }
}
