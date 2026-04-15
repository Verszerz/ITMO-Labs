package org.example.io;

import org.example.model.*;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

/**
 * Запись коллекции организаций в XML-файл.
 * Использует {@link OutputStreamWriter} для записи данных.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class XmlWriter {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");

    /**
     * Записывает коллекцию в XML-файл.
     *
     * @param filePath   путь к файлу
     * @param collection коллекция организаций
     * @throws IOException при ошибке записи
     */
    public void writeToFile(String filePath, LinkedList<Organization> collection) throws IOException {
        File file = new File(filePath);

        if (file.exists() && !file.canWrite()) {
            throw new IOException("Нет прав на запись в файл: " + filePath);
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8)) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<organizations>\n");

            for (Organization org : collection) {
                writer.write(organizationToXml(org));
            }

            writer.write("</organizations>\n");
            writer.flush();
        }
    }

    /**
     * Конвертирует организацию в XML-строку.
     *
     * @param org организация
     * @return XML-представление
     */
    private String organizationToXml(Organization org) {
        StringBuilder sb = new StringBuilder();
        sb.append("  <organization>\n");
        sb.append("    <id>").append(org.getId()).append("</id>\n");
        sb.append("    <n>").append(escapeXml(org.getName())).append("</n>\n");
        sb.append("    <coordinates>\n");
        sb.append("      <cx>").append(org.getCoordinates().getX()).append("</cx>\n");
        sb.append("      <cy>").append(org.getCoordinates().getY()).append("</cy>\n");
        sb.append("    </coordinates>\n");
        sb.append("    <creationDate>")
          .append(org.getCreationDate().format(FORMATTER))
          .append("</creationDate>\n");
        sb.append("    <annualTurnover>").append(org.getAnnualTurnover()).append("</annualTurnover>\n");
        sb.append("    <fullName>")
          .append(org.getFullName() != null ? escapeXml(org.getFullName()) : "null")
          .append("</fullName>\n");
        sb.append("    <type>").append(org.getType()).append("</type>\n");
        sb.append("    <officialAddress>\n");
        sb.append("      <zipCode>").append(escapeXml(org.getOfficialAddress().getZipCode())).append("</zipCode>\n");

        Location town = org.getOfficialAddress().getTown();
        if (town != null) {
            sb.append("      <town>\n");
            sb.append("        <tx>").append(town.getX()).append("</tx>\n");
            sb.append("        <ty>").append(town.getY()).append("</ty>\n");
            sb.append("        <tz>").append(town.getZ()).append("</tz>\n");
            sb.append("        <tname>")
              .append(town.getName() != null ? escapeXml(town.getName()) : "null")
              .append("</tname>\n");
            sb.append("      </town>\n");
        } else {
            sb.append("      <town>null</town>\n");
        }

        sb.append("    </officialAddress>\n");
        sb.append("  </organization>\n");
        return sb.toString();
    }

    /**
     * Экранирует спецсимволы XML.
     *
     * @param s входная строка
     * @return экранированная строка
     */
    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
