package ru.yandex.task;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private EpicTask epicTask;

    public SubTask(String name, String description, Task epicTask, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        if (epicTask.getClass() == EpicTask.class) {
            this.epicTask = (EpicTask) epicTask;
        } else {
            System.out.println("Подзадача может быть присоединена только к эпической задаче. Вы пытаетесь указать вместо эпической: " + epicTask);
        }
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
    }

    public EpicTask getEpicTask() {
        return epicTask;
    }
}
