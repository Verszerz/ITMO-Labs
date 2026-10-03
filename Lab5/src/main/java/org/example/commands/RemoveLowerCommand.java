package org.example.commands;

import org.example.collection.CollectionManager;
import org.example.model.Organization;

import java.util.Optional;

/**
 * Команда remove_lower — удаление элементов с id < заданного.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class RemoveLowerCommand implements Command {

    private final CollectionManager manager;

    public RemoveLowerCommand(CollectionManager manager) { this.manager = manager; }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) { System.out.println("Укажите id: remove_lower <id>"); return; }
        try {
            long id = Long.parseLong(args[0]);
            Optional<Organization> found = manager.findById(id);
            if (found.isEmpty()) { System.out.println("Элемент с id=" + id + " не найден."); return; }
            int removed = manager.removeLower(found.get());
            System.out.println("Удалено элементов с id < " + id + ": " + removed);
        } catch (NumberFormatException e) { System.out.println("id должен быть числом."); }
    }

    @Override
    public String getDescription() { return "remove_lower <id> — удалить с id < заданного"; }
}
