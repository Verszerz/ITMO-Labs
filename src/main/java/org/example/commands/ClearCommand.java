package org.example.commands;

import org.example.collection.CollectionManager;

/**
 * Команда clear — очистка коллекции.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class ClearCommand implements Command {

    private final CollectionManager manager;

    public ClearCommand(CollectionManager manager) { this.manager = manager; }

    @Override
    public void execute(String[] args) {
        manager.clear();
        System.out.println("Коллекция очищена.");
    }

    @Override
    public String getDescription() { return "очистить коллекцию"; }
}
