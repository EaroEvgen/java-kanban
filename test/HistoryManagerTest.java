import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.yandex.controllers.HistoryManager;
import ru.yandex.controllers.Managers;
import ru.yandex.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HistoryManagerTest {
    private static HistoryManager historyManager;

    @BeforeEach
    public void initHistoryManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        Task task = new Task("Name task", "Description task", LocalDateTime.now(), Duration.ofMinutes(1));
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history, "История не пустая.");
        Assertions.assertEquals("Name task", history.getFirst().getName(), "История не пустая.");
    }

    @Test
    void removeAllPerOne() {
        List<Task> curList = new ArrayList<>();
        int countData = 200;
        for (int i = 0; i < countData; i++) {
            Task curTask = new Task("Name task " + i + ".", "Description task", LocalDateTime.now().plusHours(i), Duration.ofMinutes(10));
            historyManager.add(curTask);
            curList.add(curTask);
        }

        for (Task task : curList) {
            historyManager.remove(task.getId());
        }

        final List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history, "История не пустая.");
    }

    @Test
    void removeRandomHistory() {
        List<Task> curList = new ArrayList<>();
        int countData = 200;
        Random random = new Random();
        for (int i = 0; i < countData; i++) {
            Task curTask = new Task("Name task " + i + ".", "Description task", LocalDateTime.now().plusHours(i), Duration.ofMinutes(10));
            historyManager.add(curTask);
            curList.add(curTask);
        }

        while (!curList.isEmpty()) {
            int randomInt = random.nextInt(curList.size());
            historyManager.remove(randomInt + 1);
            curList.remove(randomInt);
        }

        Assertions.assertNotNull(historyManager.getHistory(), "История не пустая.");
    }
}
