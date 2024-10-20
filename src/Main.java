import ru.yandex.task.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("task name 1", "task description 1");
        taskManager.addTask("task name 2", "task description 2");
        taskManager.addTask("task name 3", "task description 3");
        taskManager.addEpicTask("task name 4", "task description 4");
        taskManager.addEpicTask("task name 5", "task description 5");
        taskManager.addEpicTask("task name 6", "task description 6");
        taskManager.addSubTask("name sub task 1",
                "description sub task 1", taskManager.getTaskByID(4));
        taskManager.addSubTask("name sub task 2",
                "description sub task 2", taskManager.getTaskByID(4));
        taskManager.addSubTask("name sub task 3",
                "description sub task 3", taskManager.getTaskByID(5));

        taskManager.setTaskStatusByID(1, TaskStatus.IN_PROGRESS);
        taskManager.setTaskStatusByID(2, TaskStatus.DONE);
        for (SubTask subTask :((EpicTask) taskManager.getTaskByID(4)).getSubTaskList()) {
            subTask.setStatus(TaskStatus.IN_PROGRESS);
        }
        for (SubTask subTask :((EpicTask) taskManager.getTaskByID(5)).getSubTaskList()) {
            subTask.setStatus(TaskStatus.DONE);
        }
        for (SubTask subTask :((EpicTask) taskManager.getTaskByID(6)).getSubTaskList()) {
            subTask.setStatus(TaskStatus.DONE);
        }
        taskManager.printAllTasks();
        System.out.println("-----------------------------------------------------");
        taskManager.removeByID(6);
        taskManager.printAllTasks();
        System.out.println("-----------------------------------------------------");
        taskManager.removeByID(6);
        taskManager.printAllTasks();
        System.out.println("-----------------------------------------------------");
        taskManager.removeByID(5);
        taskManager.removeByID(2);
        taskManager.printAllTasks();
        System.out.println("-----------------------------------------------------");
        taskManager.clearAllTask();
        taskManager.printAllTasks();
        System.out.println("-----------------------------------------------------");
        taskManager.addTask("task name 1", "task description 1");
        taskManager.addTask("task name 2", "task description 2");
        taskManager.addTask("task name 3", "task description 3");
        taskManager.addEpicTask("task name 4", "task description 4");
        taskManager.addEpicTask("task name 5", "task description 5");
        taskManager.addEpicTask("task name 6", "task description 6");
        taskManager.addSubTask("name sub task 111",
                "description sub task 111", taskManager.getTaskByID(10));
        taskManager.addSubTask("name sub task 2",
                "description sub task 2", taskManager.getTaskByID(14));
        taskManager.addSubTask("name sub task 3",
                "description sub task 3", taskManager.getTaskByID(14));
        taskManager.printAllTasks();
        System.out.println("-----------------------------------------------------");
        taskManager.updateTask(new Task(14, "new name", "new description"));
        taskManager.addTask(new Task(16, "dwdw", "nbgfytycsvejhs"));
        taskManager.printAllTasks();
        System.out.println("-----------------------------------------------------");
    }
}
