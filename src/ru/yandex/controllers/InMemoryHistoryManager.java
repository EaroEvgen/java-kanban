package ru.yandex.controllers;

import ru.yandex.task.Task;
import ru.yandex.task.TaskNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private TaskNode headHistory;
    private TaskNode tailHistory;
    private final Map<Integer, TaskNode> taskHistory;

    public InMemoryHistoryManager() {
        taskHistory = new HashMap<>();
    }

    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        TaskNode currentNode = new TaskNode(null, tailHistory, task);

        if (headHistory == null) {
            headHistory = currentNode;
        }
        if (tailHistory != null) {
            tailHistory.setNext(currentNode);
        }
        tailHistory = currentNode;
        taskHistory.put(task.getId(), currentNode);
    }

    public List<Task> getHistory() {
        List<Task> currentTaskList = new ArrayList<>();
        TaskNode node = headHistory;
        while (node != null){
            currentTaskList.add(node.getTask());
            node = node.getNext();
        }
        return currentTaskList;
    }

    public void remove(int id) {
        TaskNode currentNode = taskHistory.remove(id);
        if (currentNode == null) {
            return;
        }
        TaskNode nextNode = currentNode.getNext();
        TaskNode previousNode = currentNode.getPrevious();

        if (previousNode == null) {
            headHistory = nextNode;
        } else {
            previousNode.setNext(nextNode);
        }

        if (nextNode == null) {
            tailHistory = previousNode;
        } else {
            nextNode.setPrevious(previousNode);
        }
    }
}