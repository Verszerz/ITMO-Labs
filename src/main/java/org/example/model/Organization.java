package org.example.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс, представляющий организацию.
 * Реализует сортировку по умолчанию — по полю name.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class Organization implements Comparable<Organization> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");

    /** Уникальный идентификатор. Не null, > 0, генерируется автоматически. */
    private Long id;
    /** Название. Не null, не пустая строка. */
    private String name;
    /** Координаты. Не null. */
    private Coordinates coordinates;
    /** Дата создания. Не null, генерируется автоматически. */
    private ZonedDateTime creationDate;
    /** Годовой оборот. Не null, > 0. */
    private Float annualTurnover;
    /** Полное название. Может быть null, уникально. */
    private String fullName;
    /** Тип организации. Не null. */
    private OrganizationType type;
    /** Официальный адрес. Не null. */
    private Address officialAddress;

    /**
     * @param id              уникальный идентификатор
     * @param name            название
     * @param coordinates     координаты
     * @param creationDate    дата создания
     * @param annualTurnover  годовой оборот
     * @param fullName        полное название (может быть null)
     * @param type            тип организации
     * @param officialAddress официальный адрес
     */
    public Organization(Long id, String name, Coordinates coordinates,
                        ZonedDateTime creationDate, Float annualTurnover,
                        String fullName, OrganizationType type,
                        Address officialAddress) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.annualTurnover = annualTurnover;
        this.fullName = fullName;
        this.type = type;
        this.officialAddress = officialAddress;
    }

    /** @return идентификатор */
    public Long getId() { return id; }
    /** @param id новый идентификатор */
    public void setId(Long id) { this.id = id; }
    /** @return название */
    public String getName() { return name; }
    /** @param name новое название */
    public void setName(String name) { this.name = name; }
    /** @return координаты */
    public Coordinates getCoordinates() { return coordinates; }
    /** @param coordinates новые координаты */
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    /** @return дата создания */
    public ZonedDateTime getCreationDate() { return creationDate; }
    /** @param creationDate новая дата */
    public void setCreationDate(ZonedDateTime creationDate) { this.creationDate = creationDate; }
    /** @return годовой оборот */
    public Float getAnnualTurnover() { return annualTurnover; }
    /** @param annualTurnover новый оборот */
    public void setAnnualTurnover(Float annualTurnover) { this.annualTurnover = annualTurnover; }
    /** @return полное название или null */
    public String getFullName() { return fullName; }
    /** @param fullName новое полное название */
    public void setFullName(String fullName) { this.fullName = fullName; }
    /** @return тип организации */
    public OrganizationType getType() { return type; }
    /** @param type новый тип */
    public void setType(OrganizationType type) { this.type = type; }
    /** @return официальный адрес */
    public Address getOfficialAddress() { return officialAddress; }
    /** @param officialAddress новый адрес */
    public void setOfficialAddress(Address officialAddress) { this.officialAddress = officialAddress; }

    /**
     * Сравнивает организации по названию (name) в алфавитном порядке.
     *
     * @param other другая организация
     * @return отрицательное, 0 или положительное число
     */
    @Override
    public int compareTo(Organization other) {
        return Long.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Organization{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", coordinates=" + coordinates +
               ", creationDate=" + creationDate.format(FORMATTER) +
               ", annualTurnover=" + annualTurnover +
               (fullName != null ? ", fullName='" + fullName + '\'' : "") +
               ", type=" + type +
               ", officialAddress=" + officialAddress +
               "}";
    }
}
