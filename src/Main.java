import ru.yandex.controllers.TaskManager;
import ru.yandex.task.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        /*System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(new Task("task name 1", "task description 1"));
        taskManager.addTask(new Task("task name 2", "task description 2"));
        taskManager.addTask(new Task("task name 3", "task description 3"));
        taskManager.addTask(new EpicTask("task name 4", "task description 4"));
        taskManager.addTask(new EpicTask("task name 5", "task description 5"));
        taskManager.addTask(new EpicTask("task name 6", "task description 6"));
        taskManager.addTask(new SubTask("name sub task 1",
                "description sub task 1", taskManager.getTaskByID(4)));
        taskManager.addTask(new SubTask("name sub task 2",
                "description sub task 2", taskManager.getTaskByID(4)));
        taskManager.addTask(new SubTask("name sub task 3",
                "description sub task 3", taskManager.getTaskByID(5)));
        taskManager.addTask(new Task("task name 7", "task description 7"));

        taskManager.setTaskStatusByID(1, TaskStatus.IN_PROGRESS);
        taskManager.setTaskStatusByID(2, TaskStatus.DONE);
        for (SubTask subTask : ((EpicTask) taskManager.getTaskByID(4)).getSubTaskList()) {
            subTask.setStatus(TaskStatus.IN_PROGRESS);
        }
        for (SubTask subTask : ((EpicTask) taskManager.getTaskByID(5)).getSubTaskList()) {
            subTask.setStatus(TaskStatus.DONE);
        }
        for (SubTask subTask : ((EpicTask) taskManager.getTaskByID(6)).getSubTaskList()) {
            subTask.setStatus(TaskStatus.DONE);
        }

        printAllTasks(taskManager);
        System.out.println("-----------------------------------------------------");
        taskManager.removeByID(6);
        printAllTasks(taskManager);
        System.out.println("-----------------------------------------------------");
        taskManager.removeByID(6);
        printAllTasks(taskManager);
        System.out.println("-----------------------------------------------------");
        taskManager.removeByID(5);
        taskManager.removeByID(2);
        printAllTasks(taskManager);
        System.out.println("-----------------------------------------------------");
        taskManager.deleteTasks();
        taskManager.deleteEpics();
        printAllTasks(taskManager);
        System.out.println("-----------------------------------------------------");
        taskManager.addTask(new Task("task name 1", "task description 1"));
        taskManager.addTask(new EpicTask("task name 4", "task description 4"));
        taskManager.addTask(new EpicTask("task name 5", "task description 5"));
        taskManager.addTask(new SubTask("name sub task 1",
                "description sub task 1", taskManager.getTaskByID(12)));
        taskManager.addTask(new SubTask("name sub task 2",
                "description sub task 2", taskManager.getTaskByID(12)));
        taskManager.addTask(new SubTask("name sub task 3",
                "description sub task 3", taskManager.getTaskByID(12)));
        printAllTasks(taskManager);
        System.out.println("-----------------------------------------------------");
        Task currentTask = taskManager.getTaskByID(11);
        currentTask.setName("forUpdate");
        //taskManager.updateTask(currentTask);
        taskManager.addTask(new Task("dwdw", "nbgfytycsvejhs"));
        printAllTasks(taskManager);
        System.out.println("-----------------------------------------------------");
        System.out.println(taskManager.getTaskHistory());*/
    }

//    public static void printAllTasks(TaskManager taskManager) {
//        if (taskManager == null) {
//            return;
//        }
//        List<Task> taskList = taskManager.getTaskList();
//        List<EpicTask> epicTaskList = taskManager.getEpicTaskList();
//        for (Task task : taskList) {
//            System.out.println(task);
//        }
//        for (EpicTask task : epicTaskList) {
//            System.out.println(task);
//            for (SubTask subTask : task.getSubTaskList()) {
//                System.out.println(" -> " + subTask);
//            }
//        }
//    }
}
