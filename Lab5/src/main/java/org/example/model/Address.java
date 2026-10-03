package org.example.model;

/**
 * Почтовый адрес организации.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class Address {
    private String zipCode;
    private Location town;

    /**
     * @param zipCode почтовый индекс (не null)
     * @param town    местоположение города (может быть null)
     */
    public Address(String zipCode, Location town) {
        this.zipCode = zipCode;
        this.town = town;
    }

    /** @return почтовый индекс */
    public String getZipCode() { return zipCode; }
    /** @param zipCode новый индекс */
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    /** @return местоположение или null */
    public Location getTown() { return town; }
    /** @param town новое местоположение */
    public void setTown(Location town) { this.town = town; }

    @Override
    public String toString() {
        return "Address{zipCode='" + zipCode + "'" +
               (town != null ? ", town=" + town : "") + "}";
    }
}
