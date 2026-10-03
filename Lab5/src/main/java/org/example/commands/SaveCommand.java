package org.example.commands;

import org.example.collection.CollectionManager;

import java.io.IOException;

/**
 * Команда save — сохранение коллекции в файл.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class SaveCommand implements Command {

    private final CollectionManager manager;

    public SaveCommand(CollectionManager manager) { this.manager = manager; }

    @Override
    public void execute(String[] args) {
        try {
            manager.save();
            System.out.println("Коллекция сохранена в файл: " + manager.getFilePath());
        } catch (IOException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() { return "сохранить коллекцию в файл"; }
}
