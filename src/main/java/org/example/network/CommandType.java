package org.example.network;

import java.io.Serializable;

/**
 * Перечисление всех типов команд.
 * Используется для маршрутизации на сервере.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public enum CommandType implements Serializable {
    HELP,
    INFO,
    SHOW,
    ADD,
    UPDATE,
    REMOVE_BY_ID,
    CLEAR,
    EXECUTE_SCRIPT,
    EXIT,
    REMOVE_HEAD,
    REMOVE_GREATER,
    REMOVE_LOWER,
    FILTER_CONTAINS_NAME,
    FILTER_LESS_THAN_ANNUAL_TURNOVER,
    PRINT_ASCENDING,
    /** Команда только для сервера — клиент её отправить не может */
    SERVER_SAVE
}
