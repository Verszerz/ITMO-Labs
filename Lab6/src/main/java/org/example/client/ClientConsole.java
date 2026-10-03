package org.example.client;

import org.example.model.*;
import org.example.network.CommandRequest;
import org.example.network.CommandResponse;
import org.example.network.CommandType;
import org.example.util.InputReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * Клиентское консольное приложение.
 *
 * <p>Читает команды из консоли, валидирует данные,
 * формирует {@link CommandRequest}, отправляет на сервер
 * и выводит полученный {@link CommandResponse}.</p>
 *
 * <p>Команда save удалена — сохранение только на сервере.</p>
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class ClientConsole {

    private static final Logger logger = Logger.getLogger(ClientConsole.class.getName());

    private final ClientSender sender;
    private final InputReader inputReader;
    private boolean running = true;

    /**
     * @param sender отправщик запросов
     */
    public ClientConsole(ClientSender sender) {
        this.sender = sender;
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        this.inputReader = new InputReader(consoleReader, true);
    }

    /**
     * Запускает интерактивный цикл ввода команд.
     */
    public void run() {
        System.out.println("=== Клиент управления коллекцией ===");
        System.out.println("Введите команду (help — справка):");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            InputReader ir = new InputReader(reader, true);

            while (running) {
                System.out.print("> ");
                String line;
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    break;
                }
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty()) continue;

                processCommand(line, ir);
            }
        } catch (IOException e) {
            logger.warning("Ошибка консоли: " + e.getMessage());
        } finally {
            sender.close();
        }
    }

    /**
     * Разбирает строку команды и выполняет её.
     *
     * @param line введённая строка
     * @param ir   читатель ввода для полей объектов
     */
    private void processCommand(String line, InputReader ir) {
        String[] parts = line.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1].trim() : null;

        try {
            CommandRequest request = buildRequest(cmd, arg, ir);
            if (request == null) return; // команда обработана локально (exit)

            CommandResponse response = sender.sendAndReceive(request);
            printResponse(response);

        } catch (IOException e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        }
    }

    /**
     * Формирует объект запроса по введённой команде.
     *
     * @param cmd имя команды
     * @param arg аргумент (или null)
     * @param ir  читатель ввода
     * @return готовый запрос или null если команда обработана локально
     * @throws IOException при ошибке ввода
     */
    private CommandRequest buildRequest(String cmd, String arg, InputReader ir) throws IOException {
        switch (cmd) {
            case "help":
                return new CommandRequest(CommandType.HELP);

            case "info":
                return new CommandRequest(CommandType.INFO);

            case "show":
                return new CommandRequest(CommandType.SHOW);

            case "add": {
                // Читаем все поля организации у пользователя
                // Валидация происходит здесь на клиенте
                Organization org = readOrganization(ir, -1L, null);
                return new CommandRequest(CommandType.ADD, org);
            }

            case "update": {
                if (arg == null) { System.out.println("Укажите id: update <id>"); return null; }
                try { Long.parseLong(arg); }
                catch (NumberFormatException e) { System.out.println("id должен быть числом."); return null; }
                Organization org = readOrganization(ir, Long.parseLong(arg), null);
                return new CommandRequest(CommandType.UPDATE, arg, org);
            }

            case "remove_by_id":
                if (arg == null) { System.out.println("Укажите id: remove_by_id <id>"); return null; }
                return new CommandRequest(CommandType.REMOVE_BY_ID, arg);

            case "clear":
                return new CommandRequest(CommandType.CLEAR);

            case "remove_head":
                return new CommandRequest(CommandType.REMOVE_HEAD);

            case "remove_greater":
                if (arg == null) { System.out.println("Укажите id: remove_greater <id>"); return null; }
                return new CommandRequest(CommandType.REMOVE_GREATER, arg);

            case "remove_lower":
                if (arg == null) { System.out.println("Укажите id: remove_lower <id>"); return null; }
                return new CommandRequest(CommandType.REMOVE_LOWER, arg);

            case "filter_contains_name":
                if (arg == null) { System.out.println("Укажите подстроку."); return null; }
                return new CommandRequest(CommandType.FILTER_CONTAINS_NAME, arg);

            case "filter_less_than_annual_turnover":
                if (arg == null) { System.out.println("Укажите значение."); return null; }
                return new CommandRequest(CommandType.FILTER_LESS_THAN_ANNUAL_TURNOVER, arg);

            case "print_ascending":
                return new CommandRequest(CommandType.PRINT_ASCENDING);

            case "execute_script":
                if (arg == null) { System.out.println("Укажите файл."); return null; }
                return new CommandRequest(CommandType.EXECUTE_SCRIPT, arg);

            case "save":
                // save удалена — объясняем пользователю
                System.out.println("Команда save доступна только на сервере (введите save в консоли сервера).");
                return null;

            case "exit":
                // Отправляем EXIT на сервер (тот просто подтверждает), затем завершаемся
                CommandResponse resp = sender.sendAndReceive(new CommandRequest(CommandType.EXIT));
                System.out.println(resp.getMessage());
                running = false;
                return null;

            default:
                System.out.println("Неизвестная команда: " + cmd + ". Введите help.");
                return null;
        }
    }

    /**
     * Интерактивно читает все поля организации с клавиатуры.
     * Валидация данных происходит здесь на клиенте.
     *
     * @param ir        читатель ввода
     * @param excludeId id исключаемый из проверки уникальности (для update)
     * @param existing  существующая организация (для update, чтобы не ходить на сервер)
     * @return объект Organization (id=0, creationDate=now — сервер перезапишет)
     * @throws IOException при ошибке ввода
     */
    private Organization readOrganization(InputReader ir, long excludeId, Organization existing)
            throws IOException {

        String name = ir.promptNonEmpty("Введите название (name)");
        float cx = ir.promptFloat("Введите координату X");
        double cy = ir.promptDouble("Введите координату Y");
        Coordinates coords = new Coordinates(cx, cy);

        float turnover = ir.promptPositiveFloat("Введите годовой оборот (> 0)");

        String fnRaw = ir.prompt("Введите полное название (пусто = null)");
        String fullName = fnRaw.isEmpty() ? null : fnRaw;

        OrganizationType type = ir.promptEnum("Введите тип", OrganizationType.class);
        String zip = ir.promptNonEmpty("Введите почтовый индекс");

        double tx = ir.promptDouble("Введите X города");
        double ty = ir.promptDouble("Введите Y города");
        double tz = ir.promptDouble("Введите Z города");
        String tnRaw = ir.promptNonEmpty("Введите название города");
        Location town = new Location(tx, ty, tz, tnRaw);
        Address address = new Address(zip, town);

        return new Organization(0L, name, coords, ZonedDateTime.now(),
                turnover, fullName, type, address);
    }

    /**
     * Выводит ответ сервера в красивом формате.
     *
     * @param response ответ от сервера
     */
    private void printResponse(CommandResponse response) {
        System.out.println(response.getMessage());

        List<Organization> orgs = response.getOrganizations();
        if (orgs != null && !orgs.isEmpty()) {
            for (Organization org : orgs) {
                printOrg(org);
            }
        }
    }

    private static void printOrg(Organization org) {
        System.out.println("+----------------------------------------------+");
        System.out.println("| ID:            " + org.getId());
        System.out.println("| Название:      " + org.getName());
        System.out.println("| Полное имя:    " + (org.getFullName() != null ? org.getFullName() : "-"));
        System.out.println("| Тип:           " + org.getType());
        System.out.println("| Оборот:        " + org.getAnnualTurnover());
        System.out.println("| Координаты:    x=" + org.getCoordinates().getX()
                         + ", y=" + org.getCoordinates().getY());
        System.out.println("| Дата создания: " + org.getCreationDate());
        System.out.println("| Индекс:        " + org.getOfficialAddress().getZipCode());
        if (org.getOfficialAddress().getTown() != null) {
            System.out.println("| Город:         " + org.getOfficialAddress().getTown().getName());
        }
        System.out.println("+----------------------------------------------+");
    }
}
