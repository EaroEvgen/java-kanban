package ru.yandex.task;

public class Task {
    private final int id;
    private String name;
    private String description;
    private TaskStatus status;
    private Task parent;

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.parent = null;
    }

    public Task(int id, String name, String description, Task parent) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Task getParent() {
        return parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        String result =  this.getClass() + "@" + id + "[name = " + name + ", description = " + description +
                ", status" + status + ", parent id = ";
        if (parent == null) {
            return result + "null]";
        } else {
            return result + parent.getId() + "]";
        }
    }
}
