
import java.util.Random;

public class Kid extends Person implements StartTalking { //Класс "Малыш" наследуется от абстрактного класса "Персона" и реализует интерфейс "Начинать Говорить"

    private boolean naive;
    private boolean enthusiastic; // Малыш очень добрый, восторженный и наивный...
    private boolean kind;

    public Kid(String name, Mood mood) { //Конструктор Малыша
        super(name, mood); //родительский констурктор
        this.naive = true;
        this.enthusiastic = true;
        this.kind = true;
    }

    private static final String[] PHRASES = { //Фразы Малыша
        "Я очень добрый и милый Малыш (^-^)",
        "Карлсон и вся моя семья называют меня Малыш...",
        "Я уже взрослый Малыш"
    };

    @Override
    public String talk() { //Переопределяем абстрактный метод Родителя
        Random rand = new Random();
        return PHRASES[rand.nextInt(PHRASES.length)];
    }

    public String think() { //Реализуем интерфейс
        return "Спорить с Карлсоном нелегко, особенно когда он не в настроении спорить...";
    }

    public void argue(Carlson carlson) throws CannotArgueException { //Проверяемое исключение. 
        if (!carlson.mood.isTimeToArgue()) {
            throw new CannotArgueException(carlson.mood);
        }
        System.out.println("Малыш начинает спор с Карлсоном (-_-)_/");
    }

    public void ThinkAboutAnswer() { //Локальный класс
        class Idea {

            private final String content;

            public Idea(String content) {
                this.content = content;
            }

            public void SayIt() {
                System.out.println("Малыш думает: \"" + content + "\"");
            }
        }
        Idea idea = new Idea("Не знаю, что ответить Карлсону");
        idea.SayIt();
    }
}
