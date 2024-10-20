package ru.yandex.task;

public class SubTask extends Task {
    EpicTask epicTask;

    SubTask (int id, String name, String description, EpicTask epicTask) {
        super(id, name, description);
        this.epicTask = epicTask;
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
        epicTask.updateStatus();
    }
}
