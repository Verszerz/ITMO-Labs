package org.example.commands;

import org.example.collection.CollectionManager;
import org.example.model.Organization;
import org.example.util.InputReader;

import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * Команда add — добавление нового элемента в коллекцию.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class AddCommand implements Command {

    private final CollectionManager manager;
    private final InputReader inputReader;

    public AddCommand(CollectionManager manager, InputReader inputReader) {
        this.manager = manager;
        this.inputReader = inputReader;
    }

    @Override
    public void execute(String[] args) {
        try {
            Organization org = inputReader.readOrganization(manager, -1L);
            org.setId(manager.generateId());
            org.setCreationDate(ZonedDateTime.now());
            manager.add(org);
            System.out.println("Организация добавлена с id=" + org.getId());
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
    public String getDescription() { return "добавить новый элемент в коллекцию"; }
}
