package org.example.commands;

import org.example.collection.CollectionManager;
import org.example.model.Organization;

import java.util.List;

/**
 * Команда filter_contains_name — поиск по подстроке name.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class FilterContainsNameCommand implements Command {

    private final CollectionManager manager;

    public FilterContainsNameCommand(CollectionManager manager) { this.manager = manager; }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Укажите подстроку: filter_contains_name <n>");
            return;
        }
        List<Organization> found = manager.filterContainsName(args[0]);
        if (found.isEmpty()) { System.out.println("Ничего не найдено по: " + args[0]); return; }
        System.out.println("Найдено (" + found.size() + "):");
        for (Organization org : found) { printOrg(org); }
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
    public String getDescription() { return "filter_contains_name <n> — поиск по name"; }
}
