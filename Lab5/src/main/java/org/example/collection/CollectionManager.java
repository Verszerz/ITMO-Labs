package org.example.collection;

import org.example.io.XmlReader;
import org.example.io.XmlWriter;
import org.example.model.Organization;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Менеджер коллекции организаций.
 * Управляет {@link LinkedList}, обеспечивает загрузку, сохранение и операции с элементами.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class CollectionManager {

    private LinkedList<Organization> collection;
    private final ZonedDateTime initDate;
    private final String filePath;
    private long nextId = 1L;

    /**
     * Создаёт менеджер и загружает коллекцию из файла.
     *
     * @param filePath путь к файлу хранения
     */
    public CollectionManager(String filePath) {
        this.filePath = filePath;
        this.initDate = ZonedDateTime.now();
        this.collection = new LinkedList<>();
        loadFromFile();
    }

    /**
     * Загружает данные из файла и обновляет счётчик ID.
     */
    private void loadFromFile() {
        XmlReader reader = new XmlReader();
        collection = reader.readFromFile(filePath);
        collection.stream()
                .mapToLong(Organization::getId)
                .max()
                .ifPresent(max -> nextId = max + 1);
        System.out.println("Загружено элементов: " + collection.size());
    }

    /**
     * Генерирует уникальный ID.
     *
     * @return новый уникальный ID
     */
    public long generateId() {
        return nextId++;
    }

    /**
     * Проверяет уникальность fullName (исключая элемент с указанным id).
     *
     * @param fullName  проверяемое значение
     * @param excludeId id исключаемого элемента
     * @return true если значение уникально
     */
    public boolean isFullNameUnique(String fullName, long excludeId) {
        if (fullName == null) return true;
        return collection.stream()
                .filter(o -> o.getId() != excludeId)
                .noneMatch(o -> fullName.equals(o.getFullName()));
    }

    /**
     * Добавляет организацию в коллекцию.
     *
     * @param org организация
     */
    public void add(Organization org) {
        collection.add(org);
    }

    /**
     * Обновляет организацию по id.
     *
     * @param id  идентификатор
     * @param org новые данные
     * @return true если обновлено
     */
    public boolean updateById(long id, Organization org) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId().equals(id)) {
                org.setId(id);
                collection.set(i, org);
                return true;
            }
        }
        return false;
    }

    /**
     * Удаляет организацию по id.
     *
     * @param id идентификатор
     * @return true если удалено
     */
    public boolean removeById(long id) {
        return collection.removeIf(o -> o.getId().equals(id));
    }

    /**
     * Очищает коллекцию.
     */
    public void clear() {
        collection.clear();
    }

    /**
     * Сохраняет коллекцию в файл.
     *
     * @throws IOException при ошибке записи
     */
    public void save() throws IOException {
        XmlWriter writer = new XmlWriter();
        writer.writeToFile(filePath, collection);
    }

    /**
     * Возвращает и удаляет первый элемент.
     *
     * @return первый элемент или null
     */
    public Organization removeHead() {
        if (collection.isEmpty()) return null;
        return collection.removeFirst();
    }

    /**
     * Удаляет элементы, превышающие заданный.
     *
     * @param element элемент для сравнения
     * @return количество удалённых
     */
    public int removeGreater(Organization element) {
        int before = collection.size();
        collection.removeIf(o -> o.getId() > element.getId());
        return before - collection.size();
    }

    /**
     * Удаляет элементы, меньшие заданного.
     *
     * @param element элемент для сравнения
     * @return количество удалённых
     */
    public int removeLower(Organization element) {
        int before = collection.size();
        collection.removeIf(o -> o.getId() < element.getId());
        return before - collection.size();
    }

    /**
     * Возвращает элементы, name которых содержит подстроку.
     *
     * @param substring подстрока
     * @return список подходящих организаций
     */
    public List<Organization> filterContainsName(String substring) {
        return collection.stream()
                .filter(o -> o.getName().toLowerCase().contains(substring.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает элементы с annualTurnover меньше заданного.
     *
     * @param value пороговое значение
     * @return список подходящих организаций
     */
    public List<Organization> filterLessThanAnnualTurnover(float value) {
        return collection.stream()
                .filter(o -> o.getAnnualTurnover() < value)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает все элементы в отсортированном порядке.
     *
     * @return отсортированный список
     */
    public List<Organization> getSorted() {
        return collection.stream().sorted(java.util.Comparator.reverseOrder()).collect(Collectors.toList());
    }

    /**
     * Возвращает коллекцию.
     *
     * @return коллекция организаций
     */
    public LinkedList<Organization> getCollection() { return collection; }

    /**
     * Возвращает дату инициализации.
     *
     * @return дата инициализации
     */
    public ZonedDateTime getInitDate() { return initDate; }

    /**
     * Возвращает путь к файлу.
     *
     * @return путь к файлу
     */
    public String getFilePath() { return filePath; }

    /**
     * Возвращает размер коллекции.
     *
     * @return количество элементов
     */
    public int size() { return collection.size(); }

    /**
     * Ищет организацию по id.
     *
     * @param id идентификатор
     * @return Optional с организацией
     */
    public Optional<Organization> findById(long id) {
        return collection.stream().filter(o -> o.getId().equals(id)).findFirst();
    }
}
