package org.example.model;

/**
 * Координаты организации на карте.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class Coordinates {
    private Float x;
    private Double y;

    /**
     * @param x координата X
     * @param y координата Y
     */
    public Coordinates(Float x, Double y) {
        this.x = x;
        this.y = y;
    }

    /** @return координата X */
    public Float getX() { return x; }
    /** @param x новое значение X */
    public void setX(Float x) { this.x = x; }
    /** @return координата Y */
    public Double getY() { return y; }
    /** @param y новое значение Y */
    public void setY(Double y) { this.y = y; }

    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + "}";
    }
}
