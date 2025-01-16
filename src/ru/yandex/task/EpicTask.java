package ru.yandex.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EpicTask extends Task {
    private final ArrayList<SubTask> subTasks;

    public EpicTask(String name, String description) {
        super(name, description, LocalDateTime.now(), Duration.ZERO);
        subTasks = new ArrayList<>();
    }

    @Override
    public void setStatus(TaskStatus status) {
        System.out.println("Статус эпической задачи меняется автоматически вместе с подзадачами.");
    }

    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("Обращение к объекту равному null.");
            return;
        }
        if (!subTasks.contains(subTask)) {
            subTasks.add(subTask);
        }
        updateStatus();
        updateTime();
    }

    public void removeSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("Обращение к объекту равному null.");
            return;
        }
        subTasks.remove(subTask);
        updateStatus();
        updateTime();
    }

    public ArrayList<SubTask> getSubTaskList() {
        return subTasks;
    }

    public void updateStatus() {
        if (subTasks.isEmpty()) {
            super.setStatus(TaskStatus.NEW);
            return;
        }
        boolean isDone = false;
        boolean isNew = false;
        boolean isInProgress = false;
        for (Task subTask : subTasks) {
            switch (subTask.getStatus()) {
                case NEW -> isNew = true;
                case DONE -> isDone = true;
                case IN_PROGRESS -> isInProgress = true;
                default -> {
                    return;
                }
            }
        }
        if (isDone && !isNew && !isInProgress) {
            super.setStatus(TaskStatus.DONE);
        } else if (isNew && !isDone && !isInProgress) {
            super.setStatus(TaskStatus.NEW);
        } else {
            super.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void cleanSubtaskIds() {
        subTasks.clear();
        updateStatus();
    }

    public void updateTime() {
        subTasks.sort((SubTask sub1, SubTask sub2) -> {
            if (sub1.getStartTime().isBefore(sub2.getStartTime())) {
                return -1;
            } else {
                return 1;
            }
        });
        if (subTasks.isEmpty()) {
            super.setStartTime(LocalDateTime.now());
            super.setDuration(Duration.ZERO);
            return;
        }
        super.setStartTime(subTasks.getFirst().getStartTime());
        super.setDuration(Duration.between(getStartTime(), subTasks.getLast().getEndTime()));
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subTasks.isEmpty()) {
            return super.getEndTime();
        }
        return subTasks.getLast().getEndTime();
    }
}
