package ru.yandex.task;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> taskList;

    public TaskManager() {
        this.taskList = new HashMap<>();
    }

    public void addTask (Task task) {
        taskList.put(task.getId(), task);
    }

    public void addTask (String name, String description) {
        int newID = CounterID.getNextID();
        taskList.put(newID, new Task(newID, name, description));
    }

    public void addEpicTask (String name, String description) {
        int newID = CounterID.getNextID();
        taskList.put(newID, new EpicTask(newID, name, description));
    }

    public void addSubTask (String name, String description, Task epicTask) {
        if (epicTask == null) {
            return;
        }
        if (epicTask.getClass() != EpicTask.class) {
            System.out.println("Подзадачу можно добавить только для эпической задачи.");
            return;
        }
        ((EpicTask) epicTask).addSubTask(new SubTask(CounterID.getNextID(), name, description, (EpicTask) epicTask));
    }

    public void updateTask (Task task) {
        if (task == null) {
            return;
        }
        if (taskList.containsKey(task.getId())) {
            taskList.remove(taskList.get(task.getId()));
        }
        taskList.put(task.getId(), task);
    }

    public void clearAllTask () {
        taskList.clear();
    }

    public void removeByID (int id) {
        taskList.remove(id);
    }

    public void setTaskStatusByID (int id, TaskStatus status) {
        taskList.get(id).setStatus(status);
    }

    public Task getTaskByID (int id) {
        return taskList.get(id);
    }

    public void printAllTasks () {
        for (Task task : taskList.values()) {
            System.out.println(task);
            if (task.getClass() == (EpicTask.class)) {
                for (SubTask subTask : ((EpicTask) task).getSubTaskList()) {
                    System.out.println(" -> " + subTask);
                }
            }
        }
    }
}
