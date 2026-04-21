package org.example.util;

import org.example.collection.CollectionManager;
import org.example.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Утилита для чтения и валидации пользовательского ввода.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class InputReader {

    private final BufferedReader reader;
    private final boolean interactive;

    /**
     * @param reader      источник ввода
     * @param interactive true для интерактивного режима
     */
    public InputReader(BufferedReader reader, boolean interactive) {
        this.reader = reader;
        this.interactive = interactive;
    }

    /**
     * Читает строку с приглашением.
     *
     * @param prompt приглашение
     * @return введённая строка
     * @throws IOException при ошибке чтения
     */
    public String prompt(String prompt) throws IOException {
        if (interactive) System.out.print(prompt + ": ");
        String line = reader.readLine();
        if (line == null) throw new IOException("Конец ввода");
        return line.trim();
    }

    /**
     * Читает непустую строку.
     *
     * @param prompt приглашение
     * @return непустая строка
     * @throws IOException при ошибке чтения
     */
    public String promptNonEmpty(String prompt) throws IOException {
        while (true) {
            String value = prompt(prompt);
            if (!value.isEmpty()) return value;
            if (interactive) System.out.println("Ошибка: значение не может быть пустым.");
            else throw new IllegalArgumentException("Пустое значение для: " + prompt);
        }
    }

    /**
     * Читает Float строго больше 0.
     *
     * @param prompt приглашение
     * @return значение > 0
     * @throws IOException при ошибке чтения
     */
    public float promptPositiveFloat(String prompt) throws IOException {
        while (true) {
            String raw = promptNonEmpty(prompt);
            try {
                float val = Float.parseFloat(raw);
                if (val > 0) return val;
                if (interactive) System.out.println("Ошибка: значение должно быть больше 0.");
                else throw new IllegalArgumentException("Значение должно быть > 0");
            } catch (NumberFormatException e) {
                if (interactive) System.out.println("Ошибка: введите число.");
                else throw new IllegalArgumentException("Некорректное число: " + raw);
            }
        }
    }

    /**
     * Читает Double.
     *
     * @param prompt приглашение
     * @return значение Double
     * @throws IOException при ошибке чтения
     */
    public double promptDouble(String prompt) throws IOException {
        while (true) {
            String raw = promptNonEmpty(prompt);
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                if (interactive) System.out.println("Ошибка: введите число.");
                else throw new IllegalArgumentException("Некорректное число: " + raw);
            }
        }
    }

    /**
     * Читает Float.
     *
     * @param prompt приглашение
     * @return значение Float
     * @throws IOException при ошибке чтения
     */
    public float promptFloat(String prompt) throws IOException {
        while (true) {
            String raw = promptNonEmpty(prompt);
            try {
                return Float.parseFloat(raw);
            } catch (NumberFormatException e) {
                if (interactive) System.out.println("Ошибка: введите число.");
                else throw new IllegalArgumentException("Некорректное число: " + raw);
            }
        }
    }

    /**
     * Читает значение enum с выводом допустимых значений.
     *
     * @param prompt    приглашение
     * @param enumClass класс enum
     * @param <T>       тип enum
     * @return выбранная константа
     * @throws IOException при ошибке чтения
     */
    public <T extends Enum<T>> T promptEnum(String prompt, Class<T> enumClass) throws IOException {
        String constants = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name).collect(Collectors.joining(", "));
        if (interactive) System.out.println("Допустимые значения: " + constants);
        while (true) {
            String raw = promptNonEmpty(prompt);
            try {
                return Enum.valueOf(enumClass, raw.toUpperCase());
            } catch (IllegalArgumentException e) {
                if (interactive) System.out.println("Ошибка: введите одно из: " + constants);
                else throw new IllegalArgumentException("Неизвестное значение: " + raw);
            }
        }
    }

    /**
     * Читает все поля организации у пользователя.
     *
     * @param manager   менеджер коллекции
     * @param excludeId id исключаемого элемента (-1 для новых)
     * @return объект Organization (без id и creationDate)
     * @throws IOException при ошибке чтения
     */
    public Organization readOrganization(CollectionManager manager, long excludeId) throws IOException {
        String name = promptNonEmpty("Введите название (name)");
        float cx = promptFloat("Введите координату X (Float)");
        double cy = promptDouble("Введите координату Y (Double)");
        Coordinates coordinates = new Coordinates(cx, cy);

        float annualTurnover = promptPositiveFloat("Введите годовой оборот (annualTurnover, > 0)");

        String fullNameRaw = prompt("Введите полное название (fullName, можно оставить пустым)");
        String fullName = fullNameRaw.isEmpty() ? null : fullNameRaw;
        if (fullName != null) {
            while (!manager.isFullNameUnique(fullName, excludeId)) {
                if (interactive) {
                    System.out.println("Ошибка: fullName '" + fullName + "' уже занято.");
                    fullNameRaw = prompt("Введите полное название (fullName)");
                    fullName = fullNameRaw.isEmpty() ? null : fullNameRaw;
                } else {
                    throw new IllegalArgumentException("fullName не уникально: " + fullName);
                }
            }
        }

        OrganizationType type = promptEnum("Введите тип организации (type)", OrganizationType.class);
        String zipCode = promptNonEmpty("Введите почтовый индекс (zipCode)");

        double tx = promptDouble("Введите X города");
        double ty = promptDouble("Введите Y города");
        double tz = promptDouble("Введите Z города");
        String tnRaw = promptNonEmpty("Введите название города");
        Location town = new Location(tx, ty, tz, tnRaw);

        Address address = new Address(zipCode, town);
        return new Organization(0L, name, coordinates, ZonedDateTime.now(),
                annualTurnover, fullName, type, address);
    }
}
