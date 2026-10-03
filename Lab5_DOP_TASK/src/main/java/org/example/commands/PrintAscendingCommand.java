package org.example.commands;

import org.example.collection.CollectionManager;
import org.example.model.Organization;

/**
 * Команда print_ascending — вывод в обратном алфавитном порядке по name.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class PrintAscendingCommand implements Command {

    private final CollectionManager manager;

    public PrintAscendingCommand(CollectionManager manager) { this.manager = manager; }

    @Override
    public void execute(String[] args) {
        if (manager.size() == 0) { System.out.println("Коллекция пуста."); return; }
        for (Organization org : manager.getSorted()) {
            printOrg(org);
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
    public String getDescription() { return "вывести элементы в порядке убывания по name"; }
}
