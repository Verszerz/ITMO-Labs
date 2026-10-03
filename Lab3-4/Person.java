
public abstract class Person { //Создаём абстрактный класс (шаблон) для будущего переопределения

    protected String name; //Доступ внутри класса, наследниках и пакета
    protected Mood mood; //У каждого человека есть имя и настроение (типа enum)

    public Person(String name, Mood mood) { //Конструктор для инициализации
        this.name = name;
        this.mood = mood;
    }

    public abstract String talk(); //Нет реализации, обязан быть реализован наследниками

    public void sit() {
        System.out.println(name + " сидит"); //Каждый человек может сидеть
    }

    public void eat(String meal) {
        System.out.println(name + " ест " + meal); //Каждый человек может кушать
    }

    public void changeMood() { //Вариативность сценария
        Mood[] allMoods = Mood.values(); //Получаем значение enum как массив
        int index = (int) (Math.random() * allMoods.length); //Рандом от 0.0 до 1.0
        this.mood = allMoods[index]; //Меняем состояние объекта
        System.out.println("#" + name + " поменял настроение на " + mood); //Выводим, что поменял настроение
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; //1. Проверка на тот же объект (сравниваем ссылки)

        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; //2. Проверка, не передали ли нам null, сравниваем типы классов

        }
        Person person = (Person) obj; //3. Приведение типа
        return name.equals(person.name); //4. Сравнение по имени
    }

    @Override
    public int hashCode() { //Обязаны переопределить
        return name.hashCode(); //использауем хэш-код от имени
    }

    @Override
    public String toString() {
        return name + " (настроение: " + mood + ")";
    }
}
