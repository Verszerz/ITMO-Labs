package org.example.commands;

/**
 * Команда exit — завершение программы без сохранения.
 * @author Yan Urnerenko
 * @version 1.0
 */
public class ExitCommand implements Command {

    @Override
    public void execute(String[] args) {
        System.out.println("Завершение программы.");
        System.exit(0);
    }

    @Override
    public String getDescription() { return "завершить программу (без сохранения)"; }
}
