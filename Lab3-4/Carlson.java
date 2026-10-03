
import java.util.Random;

public class Carlson extends Person implements Plantable { //Класс Карлсона наследуется от абстрактного класса "Персона" и реализует интерфейс "СажатьРастение"

    private boolean clever;
    private boolean energetic; //Карлсон очень умный, энергичный и аккуратный)
    private boolean careful;

    public Carlson(String name, Mood mood) {
        super(name, mood); //Конструктор Родительского класса
        this.clever = true;
        this.energetic = true;
        this.careful = false;
    }

    private static final String[] PHRASES = { //Фразы Карлсона
        "Я самый лучший Карлсон в мире!!!",
        "Я сижу на крыше и кушаю варенье)",
        "Карлсон любит летать ночью по городу (!_!)_/"
    };

    @Override
    public String talk() { //Переопределяем абстрактный метод Родителя
        Random rand = new Random();
        return PHRASES[rand.nextInt(PHRASES.length)];
    }

    public Sound makeSound() { //Поведение Карлсона - издаёт звук в зависимости от настроения
        String description = switch (mood) {
            case JOYFUL ->
                "странный, но явно радостный звук, похожий на кудахтанье";
            case ARGUABLE ->
                "не слишком громкий, но явно недовольный звук";
            case NEUTRAL ->
                "звук полного умиротворения и спокойствия";
        };
        return new Sound(description, mood);//Возвращаем объект типа "Sound", в котором содержится описание звука и настроение
    }

    @Override
    public void coverWithSoil(Pot pot) { //Реализация интерфейса №1
        System.out.println(name + " энергично засыпает косточку землёй");
    }

    @Override
    public void putSeedInPot(Pot pot) { //Реализация интерфейса №2
        System.out.println(name + " успел тем временем ткнуть косточку в горшок");
    }
}
