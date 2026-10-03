
public class CannotArgueException extends Exception { //Наследуемся от класса Exception, делаем наще исключение Checked, 

    private final Mood currentMood; //Текущее состояние Персоны, его нельзя переопределить и оно доступно только классу

    public CannotArgueException(Mood mood) { //Конструктор, принимаем настроение - причину, по которой нельзя спорить
        super("Не могу спорить, пока настроение: " + mood); //Конструктор Exception, выдаём ошибку и выводим сообщение
        this.currentMood = mood;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " (требуется ARGUABLE!!!)"; //Он итак выведет то, что было в конструкторе + сообщение
    }

    public Mood getCurrentMood() { //Геттер, чтобы другие классы смогли узнать о нашем currentMood
        return currentMood;
    }
}
