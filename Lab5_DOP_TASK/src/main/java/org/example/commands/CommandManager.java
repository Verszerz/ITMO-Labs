package org.example.commands;

import org.example.collection.CollectionManager;
import org.example.util.InputReader;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/**
 * Менеджер команд приложения.
 * Регистрирует все команды и обрабатывает пользовательский ввод.
 *
 * @author Yan Urnerenko
 * @version 1.0
 */
public class CommandManager {

    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final CollectionManager collectionManager;
    private final Set<String> runningScripts = new HashSet<>();

    /**
     * @param collectionManager менеджер коллекции
     * @param inputReader       читатель ввода
     */
    public CommandManager(CollectionManager collectionManager, InputReader inputReader) {
        this.collectionManager = collectionManager;
        registerCommands(inputReader);
    }

    /**
     * Регистрирует все команды приложения.
     *
     * @param inputReader читатель ввода
     */
    private void registerCommands(InputReader inputReader) {
        register("help",     new HelpCommand(commands));
        register("info",     new InfoCommand(collectionManager));
        register("show",     new ShowCommand(collectionManager));
        register("add",      new AddCommand(collectionManager, inputReader));
        register("update",   new UpdateCommand(collectionManager, inputReader));
        register("remove_by_id",  new RemoveByIdCommand(collectionManager));
        register("clear",    new ClearCommand(collectionManager));
        register("save",     new SaveCommand(collectionManager));
        register("execute_script", new ExecuteScriptCommand(collectionManager));
        register("exit",     new ExitCommand());
        register("remove_head",    new RemoveHeadCommand(collectionManager));
        register("remove_greater", new RemoveGreaterCommand(collectionManager));
        register("remove_lower",   new RemoveLowerCommand(collectionManager));
        register("filter_contains_name", new FilterContainsNameCommand(collectionManager));
        register("filter_less_than_annual_turnover", new FilterLessThanAnnualTurnoverCommand(collectionManager));
        register("print_ascending", new PrintAscendingCommand(collectionManager));
    }

    /**
     * Регистрирует одну команду.
     *
     * @param name    имя команды
     * @param command объект команды
     */
    private void register(String name, Command command) {
        commands.put(name, command);
    }

    /**
     * Разбирает строку ввода и выполняет команду.
     *
     * @param line строка ввода пользователя
     */
    public void processLine(String line) {
        if (line == null || line.trim().isEmpty()) return;

        String[] parts = line.trim().split("\\s+", 2);
        String cmdName = parts[0].toLowerCase();
        String[] args  = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

        Command cmd = commands.get(cmdName);
        if (cmd == null) {
            System.out.println("Неизвестная команда: '" + cmdName + "'. Введите help для справки.");
        } else {
            try {
                cmd.execute(args);
            } catch (Exception e) {
                System.out.println("Ошибка выполнения команды: " + e.getMessage());
            }
        }
    }
}
