package org.example.commands;

import org.example.collection.CollectionManager;

/**
 * Команда remove_by_id — удаление по id.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class RemoveByIdCommand implements Command {

    private final CollectionManager manager;

    public RemoveByIdCommand(CollectionManager manager) { this.manager = manager; }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) { System.out.println("Укажите id: remove_by_id <id>"); return; }
        try {
            long id = Long.parseLong(args[0]);
            if (manager.removeById(id)) System.out.println("Элемент с id=" + id + " удалён.");
            else System.out.println("Элемент с id=" + id + " не найден.");
        } catch (NumberFormatException e) { System.out.println("id должен быть числом."); }
    }

    @Override
    public String getDescription() { return "remove_by_id <id> — удалить по id"; }
}
