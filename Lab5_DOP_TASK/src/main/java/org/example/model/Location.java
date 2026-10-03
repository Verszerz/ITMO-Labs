package org.example.model;

/**
 * Географическое местоположение города.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class Location {
    private double x;
    private double y;
    private double z;
    private String name;

    /**
     * @param x    координата X
     * @param y    координата Y
     * @param z    координата Z
     * @param name название (может быть null)
     */
    public Location(double x, double y, double z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    /** @return координата X */
    public double getX() { return x; }
    /** @param x новое значение */
    public void setX(double x) { this.x = x; }
    /** @return координата Y */
    public double getY() { return y; }
    /** @param y новое значение */
    public void setY(double y) { this.y = y; }
    /** @return координата Z */
    public double getZ() { return z; }
    /** @param z новое значение */
    public void setZ(double z) { this.z = z; }
    /** @return название или null */
    public String getName() { return name; }
    /** @param name новое название */
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Location{x=" + x + ", y=" + y + ", z=" + z +
               (name != null ? ", name='" + name + "'" : "") + "}";
    }
}
