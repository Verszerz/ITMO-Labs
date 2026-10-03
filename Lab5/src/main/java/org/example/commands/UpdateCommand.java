package org.example.commands;

import org.example.collection.CollectionManager;
import org.example.model.Organization;
import org.example.util.InputReader;

import java.io.IOException;

/**
 * Команда update — обновление элемента по id.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class UpdateCommand implements Command {

    private final CollectionManager manager;
    private final InputReader inputReader;

    public UpdateCommand(CollectionManager manager, InputReader inputReader) {
        this.manager = manager;
        this.inputReader = inputReader;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) { System.out.println("Укажите id: update <id>"); return; }
        long id;
        try { id = Long.parseLong(args[0]); }
        catch (NumberFormatException e) { System.out.println("id должен быть числом."); return; }
        if (manager.findById(id).isEmpty()) {
            System.out.println("Элемент с id=" + id + " не найден.");
            return;
        }
        try {
            Organization org = inputReader.readOrganization(manager, id);
            org.setId(id);
            manager.findById(id).ifPresent(old -> org.setCreationDate(old.getCreationDate()));
            manager.updateById(id, org);
            System.out.println("Элемент с id=" + id + " обновлён:");
            printOrg(org);
        } catch (IOException e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        }
    }

    private static void printOrg(org.example.model.Organization org) {
        System.out.println("+----------------------------------------------+");
        System.out.println("| ID:            " + org.getId());
        System.out.println("| Название:      " + org.getName());
        System.out.println("| Полное имя:    " + (org.getFullName() != null ? org.getFullName() : "-"));
        System.out.println("| Тип:           " + org.getType());
        System.out.println("| Оборот:        " + org.getAnnualTurnover());
        System.out.println("| Координаты:    x=" + org.getCoordinates().getX() + ", y=" + org.getCoordinates().getY());
        System.out.println("| Дата создания: " + org.getCreationDate());
        System.out.println("| Индекс:        " + org.getOfficialAddress().getZipCode());
        if (org.getOfficialAddress().getTown() != null) {
            System.out.println("| Город:         " + org.getOfficialAddress().getTown().getName());
        }
        System.out.println("+----------------------------------------------+");
    }

    @Override
    public String getDescription() { return "update <id> — обновить элемент по id"; }
}
