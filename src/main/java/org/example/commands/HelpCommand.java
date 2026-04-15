package org.example.commands;

import java.util.Map;

/**
 * Команда help — справка по всем командам.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class HelpCommand implements Command {

    private final Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) { this.commands = commands; }

    @Override
    public void execute(String[] args) {
        System.out.println("+----------------------------------------------+");
        System.out.println("| Доступные команды:                           |");
        System.out.println("+----------------------------------------------+");
        commands.forEach((name, cmd) ->
            System.out.printf("| %-20s %s%n", name, cmd.getDescription()));
        System.out.println("+----------------------------------------------+");
    }

    @Override
    public String getDescription() { return "вывести справку по командам"; }
}
