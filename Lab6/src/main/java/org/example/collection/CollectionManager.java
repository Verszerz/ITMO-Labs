package org.example.collection;

import org.example.io.XmlReader;
import org.example.io.XmlWriter;
import org.example.model.Organization;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Менеджер коллекции организаций.
 * Все операции над коллекцией реализованы через Stream API с лямбда-выражениями.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class CollectionManager {

    private static final Logger logger = Logger.getLogger(CollectionManager.class.getName());

    private LinkedList<Organization> collection;
    private final ZonedDateTime initDate;
    private final String filePath;
    private long nextId = 1L;

    /**
     * @param filePath путь к файлу хранения коллекции
     */
    public CollectionManager(String filePath) {
        this.filePath = filePath;
        this.initDate = ZonedDateTime.now();
        this.collection = new LinkedList<>();
        loadFromFile();
    }

    private void loadFromFile() {
        XmlReader reader = new XmlReader();
        collection = reader.readFromFile(filePath);
        // Stream API: находим максимальный id
        collection.stream()
                .mapToLong(Organization::getId)
                .max()
                .ifPresent(max -> nextId = max + 1);
        logger.info("Загружено элементов: " + collection.size());
    }

    /** @return новый уникальный ID */
    public synchronized long generateId() {
        return nextId++;
    }

    /**
     * Проверяет уникальность fullName через Stream API.
     *
     * @param fullName  проверяемое значение
     * @param excludeId исключаемый id
     * @return true если уникально
     */
    public boolean isFullNameUnique(String fullName, long excludeId) {
        if (fullName == null) return true;
        // Stream API + лямбда
        return collection.stream()
                .filter(o -> o.getId() != excludeId)
                .noneMatch(o -> fullName.equals(o.getFullName()));
    }

    /** Добавляет организацию. */
    public void add(Organization org) {
        collection.add(org);
        logger.info("Добавлен элемент id=" + org.getId());
    }

    /**
     * Обновляет организацию по id.
     *
     * @return true если найдена и обновлена
     */
    public boolean updateById(long id, Organization org) {
        // Stream API для поиска индекса
        Optional<Organization> existing = collection.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst();
        if (existing.isEmpty()) return false;
        int idx = collection.indexOf(existing.get());
        org.setId(id);
        collection.set(idx, org);
        return true;
    }

    /**
     * Удаляет по id.
     *
     * @return true если удалён
     */
    public boolean removeById(long id) {
        boolean removed = collection.removeIf(o -> o.getId().equals(id));
        if (removed) logger.info("Удалён элемент id=" + id);
        return removed;
    }

    /** Очищает коллекцию. */
    public void clear() {
        collection.clear();
        logger.info("Коллекция очищена");
    }

    /**
     * Сохраняет коллекцию в файл.
     *
     * @throws IOException при ошибке записи
     */
    public void save() throws IOException {
        XmlWriter writer = new XmlWriter();
        writer.writeToFile(filePath, collection);
        logger.info("Коллекция сохранена в " + filePath);
    }

    /**
     * Удаляет и возвращает первый элемент.
     *
     * @return первый элемент или null
     */
    public Organization removeHead() {
        if (collection.isEmpty()) return null;
        return collection.removeFirst();
    }

    /**
     * Удаляет элементы с id > id заданного элемента. Stream API.
     *
     * @return количество удалённых
     */
    public int removeGreater(Organization element) {
        int before = collection.size();
        collection.removeIf(o -> o.getId() > element.getId());
        return before - collection.size();
    }

    /**
     * Удаляет элементы с id < id заданного элемента. Stream API.
     *
     * @return количество удалённых
     */
    public int removeLower(Organization element) {
        int before = collection.size();
        collection.removeIf(o -> o.getId() < element.getId());
        return before - collection.size();
    }

    /**
     * Фильтрует по подстроке name. Stream API + лямбда.
     *
     * @param substring подстрока
     * @return отфильтрованный список, отсортированный по annualTurnover
     */
    public List<Organization> filterContainsName(String substring) {
        return collection.stream()
                .filter(o -> o.getName().toLowerCase().contains(substring.toLowerCase()))
                .sorted(Comparator.comparing(Organization::getAnnualTurnover))
                .collect(Collectors.toList());
    }

    /**
     * Фильтрует по обороту. Stream API + лямбда.
     *
     * @param value пороговое значение
     * @return отфильтрованный список, отсортированный по annualTurnover
     */
    public List<Organization> filterLessThanAnnualTurnover(float value) {
        return collection.stream()
                .filter(o -> o.getAnnualTurnover() < value)
                .sorted(Comparator.comparing(Organization::getAnnualTurnover))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает отсортированную коллекцию (по name обратно). Stream API.
     *
     * @return список, отсортированный по name Я->А
     */
    public List<Organization> getSorted() {
        return collection.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает все элементы, отсортированные по annualTurnover (для передачи клиенту).
     *
     * @return список, отсортированный по размеру (оборот)
     */
    public List<Organization> getAllSortedBySize() {
        return collection.stream()
                .sorted(Comparator.comparing(Organization::getAnnualTurnover))
                .collect(Collectors.toList());
    }

    /** @return коллекция */
    public LinkedList<Organization> getCollection() { return collection; }

    /** @return дата инициализации */
    public ZonedDateTime getInitDate() { return initDate; }

    /** @return путь к файлу */
    public String getFilePath() { return filePath; }

    /** @return размер коллекции */
    public int size() { return collection.size(); }

    /**
     * Поиск по id через Stream API.
     *
     * @param id идентификатор
     * @return Optional с организацией
     */
    public Optional<Organization> findById(long id) {
        return collection.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst();
    }
}
