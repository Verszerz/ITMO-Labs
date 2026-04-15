package org.example.commands;

/**
 * Интерфейс для всех команд приложения.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public interface Command {

    /**
     * Выполняет команду.
     *
     * @param args аргументы командной строки
     */
    void execute(String[] args);

    /**
     * Возвращает описание команды для справки.
     *
     * @return строка с описанием
     */
    String getDescription();
}
