package org.example.commands;

import org.example.collection.CollectionManager;
import org.example.model.Organization;
import org.example.network.CommandRequest;
import org.example.network.CommandResponse;
import org.example.network.CommandType;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Диспетчер команд на сервере.
 *
 * <p>Регистрирует обработчики для каждого типа команды
 * и маршрутизирует входящие запросы к нужному обработчику.
 * Реализует паттерн Command.</p>
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class CommandDispatcher {

    private static final Logger logger = Logger.getLogger(CommandDispatcher.class.getName());

    private final Map<CommandType, CommandHandler> handlers = new EnumMap<>(CommandType.class);
    private final CollectionManager manager;

    /**
     * @param manager менеджер коллекции
     */
    public CommandDispatcher(CollectionManager manager) {
        this.manager = manager;
        registerHandlers();
    }

    /** Регистрирует все обработчики команд через лямбды. */
    private void registerHandlers() {

        // help
        handlers.put(CommandType.HELP, req -> new CommandResponse(
                "Доступные команды: help, info, show, add, update, " +
                "remove_by_id, clear, execute_script, exit, remove_head, " +
                "remove_greater, remove_lower, filter_contains_name, " +
                "filter_less_than_annual_turnover, print_ascending", true));

        // info
        handlers.put(CommandType.INFO, req -> new CommandResponse(
                "+----------------------------------------------+\n" +
                "| Тип:           " + manager.getCollection().getClass().getSimpleName() + "\n" +
                "| Инициализация: " + manager.getInitDate() + "\n" +
                "| Элементов:     " + manager.size() + "\n" +
                "| Файл:          " + manager.getFilePath() + "\n" +
                "+----------------------------------------------+", true));

        // show — возвращает все элементы, отсортированные по annualTurnover
        handlers.put(CommandType.SHOW, req -> {
            if (manager.size() == 0)
                return new CommandResponse("Коллекция пуста.", true);
            List<Organization> sorted = manager.getAllSortedBySize();
            return new CommandResponse("Элементов: " + sorted.size(), sorted, true);
        });

        // add
        handlers.put(CommandType.ADD, req -> {
            Organization org = req.getOrganizationArg();
            if (org == null)
                return new CommandResponse("Ошибка: объект организации не передан.", false);
            org.setId(manager.generateId());
            org.setCreationDate(ZonedDateTime.now());
            manager.add(org);
            return new CommandResponse("Организация добавлена с id=" + org.getId(), true);
        });

        // update
        handlers.put(CommandType.UPDATE, req -> {
            String idStr = req.getStringArg();
            Organization org = req.getOrganizationArg();
            if (idStr == null || org == null)
                return new CommandResponse("Ошибка: не передан id или объект.", false);
            try {
                long id = Long.parseLong(idStr);
                if (manager.findById(id).isEmpty())
                    return new CommandResponse("Элемент с id=" + id + " не найден.", false);
                // Сохраняем дату создания оригинального элемента
                manager.findById(id).ifPresent(old -> org.setCreationDate(old.getCreationDate()));
                manager.updateById(id, org);
                return new CommandResponse("Элемент с id=" + id + " обновлён.", true);
            } catch (NumberFormatException e) {
                return new CommandResponse("Ошибка: id должен быть числом.", false);
            }
        });

        // remove_by_id
        handlers.put(CommandType.REMOVE_BY_ID, req -> {
            try {
                long id = Long.parseLong(req.getStringArg());
                if (manager.removeById(id))
                    return new CommandResponse("Элемент с id=" + id + " удалён.", true);
                return new CommandResponse("Элемент с id=" + id + " не найден.", false);
            } catch (NumberFormatException e) {
                return new CommandResponse("Ошибка: id должен быть числом.", false);
            }
        });

        // clear
        handlers.put(CommandType.CLEAR, req -> {
            manager.clear();
            return new CommandResponse("Коллекция очищена.", true);
        });

        // remove_head
        handlers.put(CommandType.REMOVE_HEAD, req -> {
            Organization org = manager.removeHead();
            if (org == null)
                return new CommandResponse("Коллекция пуста.", false);
            return new CommandResponse("Удалён первый элемент: " + org.getName(), true);
        });

        // remove_greater
        handlers.put(CommandType.REMOVE_GREATER, req -> {
            try {
                long id = Long.parseLong(req.getStringArg());
                Optional<Organization> found = manager.findById(id);
                if (found.isEmpty())
                    return new CommandResponse("Элемент с id=" + id + " не найден.", false);
                int removed = manager.removeGreater(found.get());
                return new CommandResponse("Удалено элементов с id > " + id + ": " + removed, true);
            } catch (NumberFormatException e) {
                return new CommandResponse("Ошибка: id должен быть числом.", false);
            }
        });

        // remove_lower
        handlers.put(CommandType.REMOVE_LOWER, req -> {
            try {
                long id = Long.parseLong(req.getStringArg());
                Optional<Organization> found = manager.findById(id);
                if (found.isEmpty())
                    return new CommandResponse("Элемент с id=" + id + " не найден.", false);
                int removed = manager.removeLower(found.get());
                return new CommandResponse("Удалено элементов с id < " + id + ": " + removed, true);
            } catch (NumberFormatException e) {
                return new CommandResponse("Ошибка: id должен быть числом.", false);
            }
        });

        // filter_contains_name
        handlers.put(CommandType.FILTER_CONTAINS_NAME, req -> {
            String sub = req.getStringArg();
            if (sub == null) return new CommandResponse("Ошибка: не передана подстрока.", false);
            List<Organization> found = manager.filterContainsName(sub);
            if (found.isEmpty())
                return new CommandResponse("Ничего не найдено по: " + sub, true);
            return new CommandResponse("Найдено: " + found.size(), found, true);
        });

        // filter_less_than_annual_turnover
        handlers.put(CommandType.FILTER_LESS_THAN_ANNUAL_TURNOVER, req -> {
            try {
                float val = Float.parseFloat(req.getStringArg());
                List<Organization> found = manager.filterLessThanAnnualTurnover(val);
                if (found.isEmpty())
                    return new CommandResponse("Нет элементов с оборотом < " + val, true);
                return new CommandResponse("Найдено: " + found.size(), found, true);
            } catch (NumberFormatException e) {
                return new CommandResponse("Ошибка: значение должно быть числом.", false);
            }
        });

        // print_ascending — сортировка по name Я->А, возвращаем список
        handlers.put(CommandType.PRINT_ASCENDING, req -> {
            if (manager.size() == 0)
                return new CommandResponse("Коллекция пуста.", true);
            List<Organization> sorted = manager.getSorted();
            return new CommandResponse("Сортировка по name (Я->А):", sorted, true);
        });

        // server_save — только серверная команда
        handlers.put(CommandType.SERVER_SAVE, req -> {
            try {
                manager.save();
                return new CommandResponse("Коллекция сохранена.", true);
            } catch (IOException e) {
                return new CommandResponse("Ошибка сохранения: " + e.getMessage(), false);
            }
        });

        // exit — клиент завершается сам, сервер просто подтверждает
        handlers.put(CommandType.EXIT, req ->
                new CommandResponse("До свидания!", true));
    }

    /**
     * Обрабатывает входящий запрос.
     *
     * @param request запрос от клиента
     * @return ответ для отправки
     */
    public CommandResponse dispatch(CommandRequest request) {
        logger.info("Обработка команды: " + request.getType());
        CommandHandler handler = handlers.get(request.getType());
        if (handler == null) {
            return new CommandResponse("Неизвестная команда: " + request.getType(), false);
        }
        try {
            return handler.handle(request);
        } catch (Exception e) {
            logger.warning("Ошибка выполнения команды: " + e.getMessage());
            return new CommandResponse("Ошибка на сервере: " + e.getMessage(), false);
        }
    }
}
