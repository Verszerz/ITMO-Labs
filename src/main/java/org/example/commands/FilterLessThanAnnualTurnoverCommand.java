package org.example.commands;

import org.example.collection.CollectionManager;
import org.example.model.Organization;

import java.util.List;

/**
 * Команда filter_less_than_annual_turnover — поиск по обороту.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class FilterLessThanAnnualTurnoverCommand implements Command {

    private final CollectionManager manager;

    public FilterLessThanAnnualTurnoverCommand(CollectionManager manager) { this.manager = manager; }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Укажите: filter_less_than_annual_turnover <val>");
            return;
        }
        try {
            float val = Float.parseFloat(args[0]);
            List<Organization> found = manager.filterLessThanAnnualTurnover(val);
            if (found.isEmpty()) { System.out.println("Нет элементов с оборотом < " + val); return; }
            System.out.println("Оборот < " + val + " (" + found.size() + "):");
            for (Organization org : found) { printOrg(org); }
        } catch (NumberFormatException e) {
            System.out.println("Значение должно быть числом.");
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
    public String getDescription() { return "filter_less_than_annual_turnover <val>"; }
}
