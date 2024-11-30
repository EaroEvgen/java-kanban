package ru.yandex.task;

public class TaskNode {
    private TaskNode next;
    private TaskNode previous;
    private Task task;

    public TaskNode(TaskNode next, TaskNode previous, Task task) {
        this.next = next;
        this.previous = previous;
        this.task = task;
    }

    public TaskNode getNext() {
        return next;
    }

    public void setNext(TaskNode next) {
        this.next = next;
    }

    public TaskNode getPrevious() {
        return previous;
    }

    public void setPrevious(TaskNode previous) {
        this.previous = previous;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
