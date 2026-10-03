
public enum Mood {
    ARGUABLE, JOYFUL, NEUTRAL;

    public boolean isTimeToArgue() {
        return this == ARGUABLE;
    }
}
