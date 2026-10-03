package org.example.commands;

import org.example.collection.CollectionManager;

/**
 * Команда info — информация о коллекции.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class InfoCommand implements Command {

    private final CollectionManager manager;

    public InfoCommand(CollectionManager manager) { this.manager = manager; }

    @Override
    public void execute(String[] args) {
        System.out.println("+----------------------------------------------+");
        System.out.println("| Тип:            " + manager.getCollection().getClass().getSimpleName());
        System.out.println("| Инициализация:  " + manager.getInitDate());
        System.out.println("| Количество:     " + manager.size());
        System.out.println("| Файл:           " + manager.getFilePath());
        System.out.println("+----------------------------------------------+");
    }

    @Override
    public String getDescription() { return "вывести информацию о коллекции"; }
}
