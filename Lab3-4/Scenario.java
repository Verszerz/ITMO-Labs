
import java.util.ArrayList;
import java.util.Random;

public class Scenario {

    private final ArrayList<Person> persons = new ArrayList<>(); //Создаём ArrayList
    private final Random random = new Random(); //Создаём Рандом

    public void Start() {
        Mood[] allMoods = Mood.values(); //Берём все виды настроений из enum
        Mood CarlsonMood = allMoods[random.nextInt(allMoods.length)]; //Настроение Карлсона
        Mood KidMood = allMoods[random.nextInt(allMoods.length)]; //Настроение Малыша

        Carlson carlson = new Carlson("Карлсон", CarlsonMood); //Создаём объект "Карлсон"
        Kid kid = new Kid("Kid", KidMood); //Создаём объект "Малыш"

        persons.add(carlson); //Добавляем Карлсона в ArrayList
        persons.add(kid); //Добавляем Малыша в ArrayList

        System.out.println("---==Начало сценария==---");
        System.out.println("Начальное состояние:");
        for (Person i : persons) {
            System.out.println("   " + i); //Для этого переопределяли toString
        }
        System.out.println();

        //Действие 1: Карлсон сажает растение
        System.out.println("1. Карлсон сажает растение:");
        Pot pot = new Pot(false, false); //Создаём горшок
        try {
            carlson.putSeedInPot(pot);
            carlson.coverWithSoil(pot); //Карлсон пытается посадить семечко и засыпать его землёй
        } catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage()); //Если ошибка RuntimeException, то выводим...
        }

        //Действие 2: Малыш думает...:
        kid.ThinkAboutAnswer();
        System.out.println(kid.think());

        //Действие 3: Попытка спора:
        System.out.println("\n3. Попытка спора:");
        try {
            kid.argue(carlson);
        } catch (CannotArgueException e) {
            System.out.println("Исключение: " + e.getMessage());
            System.out.println("Сейчас настроение Карлсона: " + carlson.mood);

            //У Карлсона меняется настроение:
            System.out.println("Прошло 15 минут. Настроение у Карлсона поменялось...");
            carlson.changeMood();

            //Снова начинаем спор:
            try {
                kid.argue(carlson);
            } catch (CannotArgueException e2) {
                System.out.println("Снова исключение: " + e2.getMessage()); //Настроение меняется рандомно. 
                //Если и второй раз он не хочет спорить, то выводим переопределённый метод getMessage()
            }
        }

        //Действие 4: Карлсон издаёт звук
        System.out.println("\n4. Карслон меняет настроение и издаёт звук:");
        carlson.changeMood();
        Sound sound = carlson.makeSound();
        System.out.println("#Карлсон издаёт: " + sound.description());

        System.out.println("---==Конец сценария==---");

        //Демонстрация ArrayList
        System.out.println("Все персонажи в ArrayList: ");
        for (int i = 0; i < persons.size(); i++) {
            System.out.println("-=-" + (i + 1) + ". " + persons.get(i));
        }
    }

    public static void main(String[] args) {
        Scenario scenario = new Scenario();
        scenario.Start();
    }
}
