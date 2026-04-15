package org.example.io;

import org.example.model.*;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Чтение коллекции организаций из XML-файла.
 * Использует {@link BufferedReader} для построчного чтения.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class XmlReader {

    private static final Logger logger = Logger.getLogger(XmlReader.class.getName());

    /**
     * Читает коллекцию из XML-файла.
     *
     * @param filePath путь к файлу
     * @return коллекция организаций (пустая при ошибке)
     */
    public LinkedList<Organization> readFromFile(String filePath) {
        LinkedList<Organization> result = new LinkedList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Файл '" + filePath + "' не найден. Коллекция будет пустой.");
            return result;
        }
        if (!file.canRead()) {
            System.out.println("Нет прав на чтение файла: " + filePath);
            return result;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            parseOrganizations(sb.toString(), result);
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }

        return result;
    }

    /**
     * Парсит XML-содержимое и заполняет коллекцию.
     *
     * @param content XML-содержимое
     * @param result  коллекция для заполнения
     */
    private void parseOrganizations(String content, LinkedList<Organization> result) {
        Pattern p = Pattern.compile("<organization>(.*?)</organization>", Pattern.DOTALL);
        Matcher m = p.matcher(content);
        while (m.find()) {
            try {
                Organization org = parseOrganization(m.group(1));
                if (org != null) result.add(org);
            } catch (Exception e) {
                System.out.println("Ошибка при разборе записи: " + e.getMessage());
            }
        }
    }

    /**
     * Парсит XML одной организации.
     *
     * @param xml XML-блок организации
     * @return объект Organization или null при ошибке
     */
    private Organization parseOrganization(String xml) {
        try {
            Long id = Long.parseLong(extractTag(xml, "id"));
            String name = extractTag(xml, "n");
            ZonedDateTime creationDate = ZonedDateTime.parse(extractTag(xml, "creationDate"));
            Float annualTurnover = Float.parseFloat(extractTag(xml, "annualTurnover"));
            String fn = extractTag(xml, "fullName");
            String fullName = (fn == null || fn.equals("null")) ? null : fn;
            OrganizationType type = OrganizationType.valueOf(extractTag(xml, "type"));

            String coordXml = extractTag(xml, "coordinates");
            Float cx = Float.parseFloat(extractTag(coordXml, "cx"));
            Double cy = Double.parseDouble(extractTag(coordXml, "cy"));
            Coordinates coordinates = new Coordinates(cx, cy);

            String addrXml = extractTag(xml, "officialAddress");
            String zipCode = extractTag(addrXml, "zipCode");
            Location town = null;
            String townXml = extractTag(addrXml, "town");
            if (townXml != null && !townXml.trim().equals("null")) {
                double tx = Double.parseDouble(extractTag(townXml, "tx"));
                double ty = Double.parseDouble(extractTag(townXml, "ty"));
                double tz = Double.parseDouble(extractTag(townXml, "tz"));
                String tn = extractTag(townXml, "tname");
                town = new Location(tx, ty, tz, (tn == null || tn.equals("null")) ? null : tn);
            }
            Address address = new Address(zipCode, town);

            return new Organization(id, name, coordinates, creationDate,
                    annualTurnover, fullName, type, address);
        } catch (Exception e) {
            logger.warning("Не удалось разобрать организацию: " + e.getMessage());
            return null;
        }
    }

    /**
     * Извлекает содержимое XML-тега.
     *
     * @param xml строка XML
     * @param tag имя тега
     * @return содержимое или null
     */
    private String extractTag(String xml, String tag) {
        Pattern p = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">", Pattern.DOTALL);
        Matcher m = p.matcher(xml);
        return m.find() ? m.group(1).trim() : null;
    }
}
