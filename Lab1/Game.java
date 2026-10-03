
import pokemon.*;
import ru.ifmo.se.pokemon.Battle;
import ru.ifmo.se.pokemon.Pokemon;

public final class Game {

    public static void main(String[] args) {
        Battle b = new Battle();
        Pokemon[] allies = {new Komala("Komala", 19), new Spearow("Spearow", 20), new Fearow("Fearow", 20)};
        Pokemon[] foes = {new Seedot("Seedot", 20), new Nuzleaf("Nuzleaf", 19), new Shiftry("Shiftry", 20)};

        for (Pokemon p : allies) {
            b.addAlly(p);
        }
        for (Pokemon f : foes) {
            b.addFoe(f);
        }

        b.go();
    }
}
