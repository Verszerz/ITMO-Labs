package org.example.server;

import java.io.*;

/**
 * Утилита для сериализации и десериализации объектов.
 * Используется для преобразования объектов в байты (для UDP) и обратно.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class Serializer {

    /**
     * Сериализует объект в массив байтов.
     *
     * @param obj объект для сериализации
     * @return массив байтов
     * @throws IOException при ошибке сериализации
     */
    public static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    /**
     * Десериализует объект из массива байтов.
     *
     * @param bytes массив байтов
     * @return десериализованный объект
     * @throws IOException            при ошибке чтения
     * @throws ClassNotFoundException если класс не найден
     */
    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        }
    }
}
