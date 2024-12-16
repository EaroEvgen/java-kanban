package ru.yandex.controllers;

import ru.yandex.exceptions.ManagerSaveException;
import ru.yandex.task.EpicTask;
import ru.yandex.task.SubTask;
import ru.yandex.task.Task;
import ru.yandex.task.TaskStatus;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String NAME_FILE_FOR_SAVE = "TaskSave.csv";
    private static final String SEPARATOR = ";";

    public void save() {
        StringBuilder curTask = new StringBuilder();
        curTask.append("Task" + SEPARATOR
                + "ID" + SEPARATOR
                + "Name" + SEPARATOR
                + "Description" + SEPARATOR
                + "Status" + SEPARATOR
                + "EpicTask, SubTasks" + SEPARATOR);
        curTask.append("\n");

        for (Task task : super.getTaskList()) {
            curTask.append("Task" + SEPARATOR)
                    .append(task.getId()).append(SEPARATOR)
                    .append(task.getName()).append(SEPARATOR)
                    .append(task.getDescription()).append(SEPARATOR)
                    .append(task.getStatus()).append(SEPARATOR);
            curTask.append("\n");
        }

        for (EpicTask task : super.getEpicTaskList()) {
            curTask.append("EpicTask" + SEPARATOR)
                    .append(task.getId()).append(SEPARATOR)
                    .append(task.getName()).append(SEPARATOR)
                    .append(task.getDescription()).append(SEPARATOR)
                    .append(task.getStatus()).append(SEPARATOR);

            for (SubTask subTask : task.getSubTaskList()) {
                curTask.append(subTask.getId()).append(SEPARATOR);
            }
            curTask.append("\n");
        }

        for (SubTask task : super.getSubTaskList()) {
            curTask.append("SubTask" + SEPARATOR)
                    .append(task.getId()).append(SEPARATOR)
                    .append(task.getName()).append(SEPARATOR)
                    .append(task.getDescription()).append(SEPARATOR)
                    .append(task.getStatus()).append(SEPARATOR)
                    .append(task.getEpicTask().getId()).append(SEPARATOR);
            curTask.append("\n");
        }

        try (Writer writer = new FileWriter("./" + NAME_FILE_FOR_SAVE)) {
            writer.write(curTask.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл: " + "./" + NAME_FILE_FOR_SAVE, e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager curManager = new FileBackedTaskManager();
        String curTaskLine;
        try (Reader reader = new FileReader(file.getPath())) {
            BufferedReader br = new BufferedReader(reader);
            br.readLine();
            while (br.ready()) {
                curTaskLine = br.readLine();
                String[] curTaskMassTask = curTaskLine.split(SEPARATOR);
                switch (curTaskMassTask[0]) {
                    case "Task" -> curManager.addTask(getTaskFromString(curTaskMassTask));
                    case "EpicTask" -> curManager.addTask(getEpicTaskFromString(curTaskMassTask));
                    case "SubTask" -> curManager.addTask(getSubTaskFromString(curTaskMassTask,
                            (EpicTask) curManager.getTaskByID(Integer.parseInt(curTaskMassTask[5]))));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла: " + "./" + NAME_FILE_FOR_SAVE, e);
        }
        return curManager;
    }

    private static Task getTaskFromString(String[] massStringTask) {
        Task curTask = new Task(massStringTask[2], massStringTask[3]);
        curTask.setId(Integer.parseInt(massStringTask[1]));
        curTask.setStatus(taskStatusByString(massStringTask[4]));
        return curTask;
    }

    private static EpicTask getEpicTaskFromString(String[] massStringTask) {
        EpicTask curTask = new EpicTask(massStringTask[2], massStringTask[3]);
        curTask.setId(Integer.parseInt(massStringTask[1]));
        return curTask;
    }

    private static SubTask getSubTaskFromString(String[] massStringTask, EpicTask epicTask) {
        SubTask curTask;
        curTask = new SubTask(massStringTask[2], massStringTask[3], epicTask);
        curTask.setId(Integer.parseInt(massStringTask[1]));
        curTask.setStatus(taskStatusByString(massStringTask[4]));
        return curTask;
    }

    private static TaskStatus taskStatusByString(String status) {
        return switch (status) {
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "DONE" -> TaskStatus.DONE;
            default -> TaskStatus.NEW;
        };
    }

    @Override
    public <T extends Task> int addTask(T task) {
        int result = super.addTask(task);
        if (result > 0) {
            save();
        }
        return result;
    }

    @Override
    public <T extends Task> void updateTask(T task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeByID(int id) {
        super.removeByID(id);
        save();
    }

    @Override
    public void setTaskStatusByID(int id, TaskStatus status) {
        super.setTaskStatusByID(id, status);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }
}
