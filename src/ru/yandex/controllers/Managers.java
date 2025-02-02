package ru.yandex.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.adapters.DurationTypeAdapter;
import ru.yandex.adapters.LocalDateTimeTypeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getDefaultGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
    }
}
