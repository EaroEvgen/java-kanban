package ru.yandex.task;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;

    public Task(String name, String description) {
        this.id = CounterID.getNextID();
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Task currentTask = (Task) obj;
        return this.id == currentTask.id;
    }

    @Override
    public String toString() {
        return this.getClass() + "@" + id + "[name = " + name + ", description = " + description +
                ", status" + status + "]";
    }

    public void setId(int id) {
        this.id = id;
    }
}
