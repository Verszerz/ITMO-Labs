package org.example.commands;

import org.example.collection.CollectionManager;
import org.example.util.InputReader;

import java.io.*;
import java.util.*;

/**
 * Команда execute_script — выполнение скрипта из файла.
 *
 * <p>Формат скрипта:</p>
 * <pre>
 *   "show"       — команда в двойных кавычках
 *   "add"        — команда с данными ниже
 *   {Яндекс}     — значение поля в фигурных скобках
 *   {55.75}
 *   # комментарий — строки с # игнорируются
 * </pre>
 *
 * <p>Защита от рекурсии реализована через статический Stack канонических путей.
 * Stack хранит цепочку всех скриптов которые сейчас выполняются.
 * Перед запуском нового скрипта проверяем что его пути нет в Stack.
 * Это ловит как прямую рекурсию (A -> A), так и косвенную (A -> B -> A).</p>
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class ExecuteScriptCommand implements Command {

    /**
     * Статический стек путей скриптов которые сейчас выполняются.
     *
     * <p>static — один стек на весь класс, общий для всех экземпляров.
     * Это ключевое: когда скрипт A создаёт новый CommandManager и тот
     * создаёт новый ExecuteScriptCommand — они все видят ОДИН и тот же стек.</p>
     *
     * <p>Используем Deque как стек: push() кладёт наверх, pop() снимает.</p>
     */
    private static final Deque<String> scriptStack = new ArrayDeque<>();

    private final CollectionManager manager;

    /**
     * @param manager менеджер коллекции
     */
    public ExecuteScriptCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Укажите файл: execute_script <file>");
            return;
        }

        File file = new File(args[0]);

        if (!file.exists())  { System.out.println("Файл не найден: " + args[0]); return; }
        if (!file.canRead()) { System.out.println("Нет прав на чтение: " + args[0]); return; }

        // Получаем канонический (нормализованный) путь
        // Это гарантирует что "script.txt" и "./script.txt" — один и тот же файл
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            System.out.println("Не удалось получить путь: " + e.getMessage());
            return;
        }

        // Проверяем есть ли этот путь уже в стеке
        // contains() за O(n) — но стек обычно маленький, это нормально
        if (scriptStack.contains(canonicalPath)) {
            System.out.println("Ошибка: рекурсия обнаружена!");
            System.out.println("Цепочка вызовов: " + buildChain(canonicalPath));
            return;
        }

        // Кладём путь на вершину стека — помечаем скрипт как "выполняется"
        scriptStack.push(canonicalPath);
        System.out.println("Выполняю скрипт: " + args[0]);

        try {
            List<Token> tokens = parseScript(file);
            executeTokens(tokens);
            System.out.println("Скрипт выполнен: " + args[0]);
        } catch (IOException e) {
            System.out.println("Ошибка чтения скрипта: " + e.getMessage());
        } finally {
            // finally — выполняется ВСЕГДА, даже если было исключение
            // Убираем путь из стека когда скрипт завершился
            scriptStack.pop();
        }
    }

    /**
     * Строит строку с текущей цепочкой вызовов для сообщения об ошибке.
     *
     * @param loopPath путь скрипта вызвавшего рекурсию
     * @return строка вида "a.txt -> b.txt -> a.txt"
     */
    private String buildChain(String loopPath) {
        // Копируем стек в список чтобы перебрать в нужном порядке
        List<String> chain = new ArrayList<>(scriptStack);
        Collections.reverse(chain); // стек хранит в обратном порядке (последний сверху)
        chain.add(loopPath);        // добавляем замыкающий элемент
        // Оставляем только имена файлов для читаемости
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chain.size(); i++) {
            sb.append(new File(chain.get(i)).getName());
            if (i < chain.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Внутренний класс токена
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Токен скрипта — единица разбора файла.
     * Бывает двух типов: COMMAND ("show") и VALUE ({Яндекс}).
     */
    private static class Token {
        /** Тип токена */
        enum Type { COMMAND, VALUE }

        final Type type;
        final String text;

        Token(Type type, String text) {
            this.type = type;
            this.text = text;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Разбор файла скрипта
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Читает файл скрипта и разбивает его на токены.
     *
     * <p>Правила разбора строк:</p>
     * <ul>
     *   <li>Пустые строки и строки начинающиеся с # — игнорируются</li>
     *   <li>"команда" (в двойных кавычках) — токен COMMAND</li>
     *   <li>{значение} (в фигурных скобках) — токен VALUE</li>
     *   <li>остальные строки — тоже VALUE</li>
     * </ul>
     *
     * @param file файл скрипта
     * @return список токенов в порядке чтения
     * @throws IOException при ошибке чтения файла
     */
    private List<Token> parseScript(File file) throws IOException {
        List<Token> tokens = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Пропускаем пустые строки и комментарии
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("\"") && line.endsWith("\"") && line.length() >= 2) {
                    // "команда" — убираем кавычки с обоих концов
                    String cmd = line.substring(1, line.length() - 1).trim();
                    tokens.add(new Token(Token.Type.COMMAND, cmd));

                } else if (line.startsWith("{") && line.endsWith("}") && line.length() >= 2) {
                    // {значение} — убираем фигурные скобки
                    String val = line.substring(1, line.length() - 1);
                    tokens.add(new Token(Token.Type.VALUE, val));

                } else {
                    // Строка без разметки — считаем значением
                    tokens.add(new Token(Token.Type.VALUE, line));
                }
            }
        }

        return tokens;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Выполнение токенов
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Выполняет список токенов.
     *
     * <p>Алгоритм: группируем токены по командам.
     * Каждая команда забирает все VALUE-токены которые идут за ней
     * (до следующей команды). Затем выполняем каждую пару (команда + значения).</p>
     *
     * @param tokens список токенов из parseScript
     */
    private void executeTokens(List<Token> tokens) {
        // Разбиваем на группы: [COMMAND, [VAL, VAL, VAL], COMMAND, [VAL], ...]
        List<String> cmdList = new ArrayList<>();
        List<List<String>> valLists = new ArrayList<>();
        List<String> currentVals = new ArrayList<>();

        for (Token t : tokens) {
            if (t.type == Token.Type.COMMAND) {
                // Новая команда — сохраняем накопленные значения предыдущей
                if (!cmdList.isEmpty()) {
                    valLists.add(new ArrayList<>(currentVals));
                    currentVals.clear();
                }
                cmdList.add(t.text);
            } else {
                // Значение — накапливаем для текущей команды
                currentVals.add(t.text);
            }
        }
        // Значения после последней команды
        valLists.add(new ArrayList<>(currentVals));

        // Выполняем каждую команду с её значениями
        for (int i = 0; i < cmdList.size(); i++) {
            String cmd = cmdList.get(i);
            List<String> vals = (i < valLists.size()) ? valLists.get(i) : new ArrayList<>();
            System.out.println("  >> \"" + cmd + "\"");

            if (isInlineArgCmd(cmd) && !vals.isEmpty()) {
                // Команды типа remove_by_id, filter_contains_name —
                // аргумент идёт прямо в строке: "remove_by_id 3"
                makeManager(Collections.emptyList()).processLine(cmd + " " + vals.get(0));
            } else {
                // Команды типа add, update — читают данные построчно из vals
                makeManager(vals).processLine(cmd);
            }
        }
    }

    /**
     * Создаёт CommandManager который читает строки из переданного списка.
     *
     * <p>StringReader превращает список строк в источник ввода
     * — как будто пользователь вводит их с клавиатуры, но автоматически.</p>
     *
     * @param vals строки для ввода (поля организации и т.п.)
     * @return готовый CommandManager
     */
    private CommandManager makeManager(List<String> vals) {
        // Склеиваем строки через \n — InputReader читает их через readLine()
        String data = String.join("\n", vals) + "\n";

        // StringReader читает из строки в памяти (не из файла и не с клавиатуры)
        BufferedReader br = new BufferedReader(new StringReader(data));

        // false = неинтерактивный режим — не выводить приглашения "Введите..."
        InputReader ir = new InputReader(br, false);

        // Создаём новый CommandManager — он создаст новый ExecuteScriptCommand,
        // который будет работать с тем же статическим стеком scriptStack
        return new CommandManager(manager, ir);
    }

    /**
     * Возвращает true если команда принимает аргумент прямо в строке.
     *
     * <p>Такие команды не читают данные построчно из InputReader,
     * а получают аргумент сразу: processLine("remove_by_id 3")</p>
     *
     * @param cmd имя команды
     * @return true если аргумент в строке
     */
    private boolean isInlineArgCmd(String cmd) {
        return cmd.equals("remove_by_id")
            || cmd.equals("remove_greater")
            || cmd.equals("remove_lower")
            || cmd.equals("update")
            || cmd.equals("filter_contains_name")
            || cmd.equals("filter_less_than_annual_turnover")
            || cmd.equals("execute_script");
    }

    @Override
    public String getDescription() {
        return "execute_script <file> — исполнить скрипт из файла";
    }
}
