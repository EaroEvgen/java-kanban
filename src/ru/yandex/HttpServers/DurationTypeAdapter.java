package ru.yandex.HttpServers;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, final Duration duration) throws IOException {
        jsonWriter.value(duration.getSeconds());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        return Duration.ofSeconds(Integer.parseInt(jsonReader.nextString()));
    }
}