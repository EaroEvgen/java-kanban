import ru.yandex.task.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("task name 1", "task description 1");
        taskManager.addTask("task name 2", "task description 2", 1);
        taskManager.addTask("task name 3", "task description 3");
        taskManager.addTask("task name 4", "task description 4", 1);
        taskManager.addTask("task name 5", "task description 5");

        taskManager.printAllTasks();
    }
}
