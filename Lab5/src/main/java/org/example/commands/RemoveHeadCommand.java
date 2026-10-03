package org.example.commands;

import org.example.collection.CollectionManager;
import org.example.model.Organization;

/**
 * Команда remove_head — вывод и удаление первого элемента.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class RemoveHeadCommand implements Command {

    private final CollectionManager manager;

    public RemoveHeadCommand(CollectionManager manager) { this.manager = manager; }

    @Override
    public void execute(String[] args) {
        Organization org = manager.removeHead();
        if (org == null) { System.out.println("Коллекция пуста."); return; }
        System.out.println("Удалён первый элемент:");
        printOrg(org);
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
    public String getDescription() { return "вывести первый элемент и удалить его"; }
}
