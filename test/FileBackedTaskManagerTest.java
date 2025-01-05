import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import ru.yandex.task.*;
import ru.yandex.controllers.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManagerTest {

    protected static FileBackedTaskManager taskManager;

    @BeforeEach
    public void initTaskManager() {
        taskManager = new FileBackedTaskManager();
    }

    @Test
    void methodGetAllTaskList() {
        Task task = new Task(
                "Task name",
                "Test addNewTask description",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 12, 24, 9, 00),
                Duration.ofMinutes(1));
        EpicTask epicTask = new EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask(
                "subtask name",
                "subtask description",
                epicTask,
                LocalDateTime.of(2024, 12, 24, 10, 00),
                Duration.ofMinutes(10));
        SubTask subTask1 = new SubTask(
                "subtask1 name",
                "subtask1 description",
                epicTask,
                LocalDateTime.of(2024, 12, 24, 11, 00),
                Duration.ofMinutes(10));

        taskManager.addTask(task);
        taskManager.addTask(epicTask);
        taskManager.addTask(subTask);
        taskManager.addTask(subTask1);
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        List<EpicTask> epicTaskList = new ArrayList<>();
        epicTaskList.add(epicTask);
        List<SubTask> subTaskList = new ArrayList<>();
        subTaskList.add(subTask);
        subTaskList.add(subTask1);

        Assertions.assertTrue(taskList.containsAll(taskManager.getTaskList())
                && taskManager.getTaskList().containsAll(taskList));
        Assertions.assertTrue(epicTaskList.containsAll(taskManager.getEpicTaskList())
                && taskManager.getEpicTaskList().containsAll(epicTaskList));
        Assertions.assertTrue(subTaskList.containsAll(taskManager.getSubTaskList())
                && taskManager.getSubTaskList().containsAll(subTaskList));
    }

    @Test
    void methodLoadFromFile() {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW
                ,LocalDateTime.now(), Duration.ofMinutes(10));
        EpicTask epicTask = new EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask
                ,LocalDateTime.now(), Duration.ofMinutes(10));

        taskManager.addTask(task);
        taskManager.addTask(epicTask);
        taskManager.addTask(subTask);
        FileBackedTaskManager backedTaskManager;

        try {
            File file = new File("./TaskSave.csv");
            backedTaskManager = FileBackedTaskManager.loadFromFile(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(backedTaskManager.getTaskList().containsAll(taskManager.getTaskList())
                && taskManager.getTaskList().containsAll(backedTaskManager.getTaskList()));
        Assertions.assertTrue(backedTaskManager.getEpicTaskList().containsAll(taskManager.getEpicTaskList())
                && taskManager.getEpicTaskList().containsAll(backedTaskManager.getEpicTaskList()));
        Assertions.assertTrue(backedTaskManager.getSubTaskList().containsAll(taskManager.getSubTaskList())
                && taskManager.getSubTaskList().containsAll(backedTaskManager.getSubTaskList()));

    }

    @Test
    void methodEmptyLoadFromFile() {

        taskManager = new FileBackedTaskManager();
        taskManager.save();
        FileBackedTaskManager backedTaskManager;

        try {
            File file = new File("./TaskSave.csv");
            backedTaskManager = FileBackedTaskManager.loadFromFile(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(backedTaskManager.getTaskList().containsAll(taskManager.getTaskList())
                && taskManager.getTaskList().containsAll(backedTaskManager.getTaskList()));
        Assertions.assertTrue(backedTaskManager.getEpicTaskList().containsAll(taskManager.getEpicTaskList())
                && taskManager.getEpicTaskList().containsAll(backedTaskManager.getEpicTaskList()));
        Assertions.assertTrue(backedTaskManager.getSubTaskList().containsAll(taskManager.getSubTaskList())
                && taskManager.getSubTaskList().containsAll(backedTaskManager.getSubTaskList()));

    }
}
