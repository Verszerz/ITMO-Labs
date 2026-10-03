
public record Sound(String description, Mood mood) { //record для создания неизменяемых объектов данных

    public boolean isJoyful() {
        return mood == Mood.JOYFUL; //Проверка, радостный ли звук
    }
}
