
public record Pot(boolean hasSeed, boolean coveredWithSoil) { //Представляет горшок для посадки, есть ли семечко и засыпано ли землёй

    public Pot plantSeed() {
        return new Pot(true, coveredWithSoil);
    }

    public Pot coverWithSoil() {
        return new Pot(hasSeed, true);
    }
}
