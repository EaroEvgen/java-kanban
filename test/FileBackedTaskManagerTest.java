import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import ru.yandex.task.*;
import ru.yandex.controllers.*;

import java.io.File;
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
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW);
        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask);

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
    void methodLoadFromFile() {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW);
        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask);

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
