package org.example.commands;

import java.io.*;
import java.util.*;
import org.example.collection.CollectionManager;
import org.example.util.InputReader;

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
 *
 * @author Yan Urnerenko
 * @version 5.0
 */
public class ExecuteScriptCommand implements Command {

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

        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            System.out.println("Не удалось получить путь: " + e.getMessage());
            return;
        }

        if (scriptStack.contains(canonicalPath)) {
            System.out.println("Ошибка: рекурсия обнаружена!");
            System.out.println("Цепочка вызовов: " + buildChain(canonicalPath));
            return;
        }

        scriptStack.push(canonicalPath);
        System.out.println("Выполняю скрипт: " + args[0]);

        try {
            List<Token> tokens = parseScript(file);
            executeTokens(tokens);
            System.out.println("Скрипт выполнен: " + args[0]);
        } catch (IOException e) {
            System.out.println("Ошибка чтения скрипта: " + e.getMessage());
        } finally {
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
        List<String> chain = new ArrayList<>(scriptStack);
        Collections.reverse(chain);
        chain.add(loopPath);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chain.size(); i++) {
            sb.append(new File(chain.get(i)).getName());
            if (i < chain.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }

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

                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("\"") && line.endsWith("\"") && line.length() >= 2) {
                    String cmd = line.substring(1, line.length() - 1).trim();
                    tokens.add(new Token(Token.Type.COMMAND, cmd));

                } else if (line.startsWith("{") && line.endsWith("}") && line.length() >= 2) {
                    String val = line.substring(1, line.length() - 1);
                    tokens.add(new Token(Token.Type.VALUE, val));

                } else {
                    tokens.add(new Token(Token.Type.VALUE, line));
                }
            }
        }

        return tokens;
    }

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
        List<String> cmdList = new ArrayList<>();
        List<List<String>> valLists = new ArrayList<>();
        List<String> currentVals = new ArrayList<>();

        for (Token t : tokens) {
            if (t.type == Token.Type.COMMAND) {
                if (!cmdList.isEmpty()) {
                    valLists.add(new ArrayList<>(currentVals));
                    currentVals.clear();
                }
                cmdList.add(t.text);
            } else {
                currentVals.add(t.text);
            }
        }
        valLists.add(new ArrayList<>(currentVals));

        for (int i = 0; i < cmdList.size(); i++) {
            String cmd = cmdList.get(i);
            List<String> vals = (i < valLists.size()) ? valLists.get(i) : new ArrayList<>();
            System.out.println("  >> \"" + cmd + "\"");

            if (isInlineArgCmd(cmd) && !vals.isEmpty()) {
                makeManager(Collections.emptyList()).processLine(cmd + " " + vals.get(0));
            } else {
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
        String data = String.join("\n", vals) + "\n";

        BufferedReader br = new BufferedReader(new StringReader(data));

        InputReader ir = new InputReader(br, false);

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
