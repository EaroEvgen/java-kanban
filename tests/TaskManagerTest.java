import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import ru.yandex.task.*;
import ru.yandex.controllers.*;

import java.util.List;

public class TaskManagerTest {
    protected static TaskManager taskManager;

    @BeforeAll
    static public void initTaskManager() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskByID(taskId);

        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        Assertions.assertNotNull(tasks, "Задачи не возвращаются.");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }
}
