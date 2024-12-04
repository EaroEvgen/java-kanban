package ru.yandex.task;

public class SubTask extends Task {
    private EpicTask epicTask;

    public SubTask(String name, String description, Task epicTask) {
        super(name, description);
        if (epicTask.getClass() == EpicTask.class) {
            this.epicTask = (EpicTask) epicTask;
        } else {
            System.out.println("Подзадача может быть присоединена только к эпической задаче. Вы пытаетесь указать вместо эпической: " + epicTask);
        }
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
        epicTask.updateStatus();
    }

    public EpicTask getEpicTask() {
        return epicTask;
    }

    public void setEpicTask(EpicTask epicTask) {
        this.epicTask = epicTask;
    }
}
