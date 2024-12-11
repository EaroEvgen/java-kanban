package ru.yandex.controllers;

import ru.yandex.task.EpicTask;
import ru.yandex.task.SubTask;
import ru.yandex.task.Task;
import ru.yandex.task.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    final static private String NAME_FILE_FOR_SAVE = "TaskSave.csv";
    final static private String SEPARATOR = ";";

    static void main(String[] args) {

    }

    public boolean save() {
        StringBuilder curTask = new StringBuilder();
        curTask.append("Task" + SEPARATOR
                + "ID" + SEPARATOR
                + "Name" + SEPARATOR
                + "Description" + SEPARATOR
                + "Status" + SEPARATOR
                + "EpicTask, SubTasks" + SEPARATOR);
        curTask.append("\n");

        for (Task task : super.getTaskList()) {
            curTask.append("Task" + SEPARATOR
                    + task.getId() + SEPARATOR
                    + task.getName() + SEPARATOR
                    + task.getDescription() + SEPARATOR
                    + task.getStatus() + SEPARATOR);
            curTask.append("\n");
        }

        for (EpicTask task : super.getEpicTaskList()) {
            curTask.append("EpicTask" + SEPARATOR
                    + task.getId() + SEPARATOR
                    + task.getName() + SEPARATOR
                    + task.getDescription() + SEPARATOR
                    + task.getStatus() + SEPARATOR);

            for (SubTask subTask : task.getSubTaskList()) {
                curTask.append(subTask.getId()).append(SEPARATOR);
            }
            curTask.append("\n");
        }

        for (SubTask task : super.getSubTaskList()) {
            curTask.append("SubTask" + SEPARATOR
                    + task.getId() + SEPARATOR
                    + task.getName() + SEPARATOR
                    + task.getDescription() + SEPARATOR
                    + task.getStatus() + SEPARATOR
                    + task.getEpicTask().getId() + SEPARATOR);
            curTask.append("\n");
        }

        try (Writer writer = new FileWriter("./" + NAME_FILE_FOR_SAVE)) {
            writer.write(curTask.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager curManager = new FileBackedTaskManager();
        String curTaskLine;
        try (Reader reader = new FileReader(file.getPath())) {
            BufferedReader br = new BufferedReader(reader);
            curTaskLine = br.readLine();
            while (br.ready()) {
                curTaskLine = br.readLine();
                String[] curTaskMassTask = curTaskLine.split(SEPARATOR);
                if (curTaskMassTask[0].equals("Task")) {
                    curManager.addTask(getTaskFromString(curTaskMassTask));
                } else if (curTaskMassTask[0].equals("EpicTask")) {
                    curManager.addTask(getEpicTaskFromString(curTaskMassTask));
                } else if (curTaskMassTask[0].equals("SubTask")) {
                    curManager.addTask(getSubTaskFromString(curTaskMassTask,
                            (EpicTask) curManager.getTaskByID(Integer.parseInt(curTaskMassTask[5]))));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
