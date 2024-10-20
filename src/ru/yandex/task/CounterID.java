package ru.yandex.task;

public class CounterID {
    private static int currentMaxID;

    public static int getNextID () {
        currentMaxID++;
        return currentMaxID;
    }
}
