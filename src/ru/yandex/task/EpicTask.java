package ru.yandex.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.yandex.task.TaskStatus.IN_PROGRESS;

public class EpicTask extends Task {
    private List<Integer> subTasks;

    public EpicTask(String name, String description) {
        super(name, description, LocalDateTime.now(), Duration.ZERO);
        subTasks = new ArrayList<>();
    }

    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("Обращение к объекту равному null.");
            return;
        }
        if (!subTasks.contains(subTask.getId())) {
            subTasks.add(subTask.getId());
        }
    }

    public void removeSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("Обращение к объекту равному null.");
            return;
        }
        final int curTaskId = subTask.getId();
        subTasks = subTasks.stream()
                .filter(task -> task != curTaskId)
                .toList();
    }

    public List<Integer> getSubTaskList() {
        return subTasks;
    }

    public static List<SubTask> getSubTaskList(EpicTask epicTask, final List<SubTask> subTasks) {
        List<Integer> curSubTasksIDList = epicTask.getSubTaskList();
        return subTasks.stream()
                .filter(task -> curSubTasksIDList.contains(task.getId()))
                .toList();
    }

    public void cleanSubtaskIds() {
        subTasks.clear();
    }

    public static void update(EpicTask epicTask, final List<SubTask> subTasks) {
        updateStatus(epicTask, subTasks);
        updateTime(epicTask, subTasks);
    }

    public static void updateStatus(EpicTask epicTask, final List<SubTask> subTasks) {
        if (epicTask.getSubTaskList().isEmpty()) {
            epicTask.setStatus(TaskStatus.NEW);
            return;
        }
        List<SubTask> curSubTasks = getSubTaskList(epicTask, subTasks);

        boolean isDone = false;
        boolean isNew = false;
        boolean isInProgress = false;
        for (SubTask subTask : curSubTasks) {
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
            epicTask.setStatus(TaskStatus.DONE);
        } else if (isNew && !isDone && !isInProgress) {
            epicTask.setStatus(TaskStatus.NEW);
        } else {
            epicTask.setStatus(IN_PROGRESS);
        }
    }

    public static void updateTime(EpicTask epicTask, final List<SubTask> subTasks) {
        List<SubTask> curSubTaskList = getSubTaskList(epicTask, subTasks).stream()
                .sorted((SubTask sub1, SubTask sub2) -> {
                    if (sub1.getStartTime().isBefore(sub2.getStartTime())) {
                        return -1;
                    } else {
                        return 1;
                    }
                })
                .toList();
        if (curSubTaskList.isEmpty()) {
            epicTask.setStartTime(LocalDateTime.now());
            epicTask.setDuration(Duration.ZERO);
            return;
        }
        epicTask.setStartTime(subTasks.getFirst().getStartTime());

        Duration resultDuration = Duration.ofMinutes(0);
        for (SubTask task : curSubTaskList) {
            resultDuration = resultDuration.plus(task.getDuration());
        }
        epicTask.setDuration(resultDuration);
    }
}
