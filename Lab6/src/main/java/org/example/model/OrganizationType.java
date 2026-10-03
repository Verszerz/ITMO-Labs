package org.example.model;

import java.io.Serializable;

/**
 * Перечисление типов организаций.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public enum OrganizationType implements Serializable {
    /** Государственная организация */
    GOVERNMENT,
    /** Трест */
    TRUST,
    /** Общество с ограниченной ответственностью */
    PRIVATE_LIMITED_COMPANY;
}
