import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import ru.yandex.task.*;
import ru.yandex.controllers.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerTest {
    protected static TaskManager taskManager;

    @BeforeEach
    public void initTaskManager() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskByID(taskId);

        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        Assertions.assertNotNull(tasks, "Задачи не возвращаются.");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void safeTaskAllTypes () {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));

        int taskId = taskManager.addTask(task);
        int epicTaskId = taskManager.addTask(epicTask);
        int subTaskId = taskManager.addTask(subTask);

        Assertions.assertEquals(task, taskManager.getTaskByID(taskId), "Не смог добавить простую задачу");
        Assertions.assertEquals(epicTask, taskManager.getTaskByID(epicTaskId), "Не смог добавить эпическую задачу");
        Assertions.assertEquals(subTask, taskManager.getTaskByID(subTaskId), "Не смог добавить подзадачу");
    }

    @Test
    void methodGetAllTaskList() {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));

        taskManager.addTask(task);
        taskManager.addTask(epicTask);
        taskManager.addTask(subTask);
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        List<EpicTask> epicTaskList = new ArrayList<>();
        epicTaskList.add(epicTask);
        List<SubTask> subTaskList = new ArrayList<>();
        subTaskList.add(subTask);

        Assertions.assertTrue(taskList.containsAll(taskManager.getTaskList())
                && taskManager.getTaskList().containsAll(taskList));
        Assertions.assertTrue(epicTaskList.containsAll(taskManager.getEpicTaskList())
                && taskManager.getEpicTaskList().containsAll(epicTaskList));
        Assertions.assertTrue(subTaskList.containsAll(taskManager.getSubTaskList())
                && taskManager.getSubTaskList().containsAll(subTaskList));

    }

    @Test
    void methodClearList() {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));

        taskManager.addTask(task);
        taskManager.addTask(epicTask);
        taskManager.addTask(subTask);
        taskManager.deleteTasks();
        taskManager.deleteEpics();
        taskManager.deleteSubtasks();

        Assertions.assertTrue(taskManager.getTaskList().isEmpty());
        Assertions.assertTrue(taskManager.getSubTaskList().isEmpty());
        Assertions.assertTrue(taskManager.getEpicTaskList().isEmpty());

    }

    @Test
    void methodUpdateTask () {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));

        int taskId = taskManager.addTask(task);
        int epicTaskId = taskManager.addTask(epicTask);
        int subTaskId = taskManager.addTask(subTask);

        task = new Task("NEW Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        task.setId(taskId);
        epicTask = new  EpicTask("NEW Epik task name", "Epik task description");
        epicTask.setId(epicTaskId);
        subTask = new SubTask("NEW subtask name", "subtask description", epicTask, LocalDateTime.now(), Duration.ofMinutes(10));
        subTask.setId(subTaskId);

        taskManager.updateTask(task);
        taskManager.updateTask(epicTask);
        taskManager.updateTask(subTask);

        Assertions.assertEquals(task, taskManager.getTaskByID(taskId), "Не смог обновить простую задачу");
        Assertions.assertEquals(epicTask, taskManager.getTaskByID(epicTaskId), "Не смог обновить эпическую задачу");
        Assertions.assertEquals(subTask, taskManager.getTaskByID(subTaskId), "Не смог обновить подзадачу");

    }

    @Test
    void methodRemoveByID () {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now(), Duration.ofMinutes(10));

        int taskId = taskManager.addTask(task);
        int epicTaskId = taskManager.addTask(epicTask);
        int subTaskId = taskManager.addTask(subTask);
        taskManager.removeByID(taskId);
        taskManager.removeByID(epicTaskId);
        taskManager.removeByID(subTaskId);
        Assertions.assertNull(taskManager.getTaskByID(taskId), "Задача не удаляется по ИД");
        Assertions.assertNull(taskManager.getTaskByID(epicTaskId), "Эпическая задача не удаляется по ИД");
        Assertions.assertNull(taskManager.getTaskByID(subTaskId), "Подзадача не удаляется по ИД");
    }

    @Test
    void getSubtaskInEpictask () {
        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now(), Duration.ofMinutes(10));

        int epicTaskId = taskManager.addTask(epicTask);
        taskManager.addTask(subTask);

        List<SubTask> subTaskList = new ArrayList<>();
        subTaskList.add(subTask);

        Assertions.assertTrue(subTaskList.containsAll(taskManager.getSubTaskList(epicTaskId))
                && taskManager.getSubTaskList(epicTaskId).containsAll(subTaskList));

    }

    @Test
    void checkChangeStatus () {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now(), Duration.ofMinutes(10));
        SubTask subTaskNew = new SubTask("new subtask name", "new subtask description", epicTask, LocalDateTime.now(), Duration.ofMinutes(10));

        int taskId = taskManager.addTask(task);
        int epicTaskId = taskManager.addTask(epicTask);
        int subTaskId = taskManager.addTask(subTask);

        Assertions.assertEquals(TaskStatus.NEW, taskManager.getTaskByID(taskId).getStatus(), "Ошибки в работе со статусами");
        taskManager.setTaskStatusByID(taskId, TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskByID(taskId).getStatus(), "Ошибки в работе со статусами");

        Assertions.assertEquals(TaskStatus.NEW, taskManager.getTaskByID(epicTaskId).getStatus());
        taskManager.setTaskStatusByID(epicTaskId, TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(TaskStatus.NEW, taskManager.getTaskByID(epicTaskId).getStatus(), "Ошибки в работе со статусами");

        Assertions.assertEquals(TaskStatus.NEW, taskManager.getTaskByID(subTaskId).getStatus());
        taskManager.setTaskStatusByID(subTaskId, TaskStatus.DONE);
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getTaskByID(subTaskId).getStatus(), "Ошибки в работе со статусами");
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getTaskByID(epicTaskId).getStatus(), "Ошибки в работе со статусами");

        int subTaskNewId = taskManager.addTask(subTaskNew);
        Assertions.assertEquals(TaskStatus.NEW, taskManager.getTaskByID(subTaskNewId).getStatus(), "Ошибки в работе со статусами");
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskByID(epicTaskId).getStatus(), "Ошибки в работе со статусами");
        taskManager.setTaskStatusByID(subTaskNewId, TaskStatus.DONE);
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getTaskByID(epicTaskId).getStatus(), "Ошибки в работе со статусами");
    }
}
