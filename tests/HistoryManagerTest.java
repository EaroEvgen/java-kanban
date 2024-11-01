import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.yandex.controllers.HistoryManager;
import ru.yandex.controllers.Managers;
import ru.yandex.task.Task;

import java.util.List;

public class HistoryManagerTest {
    private static HistoryManager historyManager;
    @BeforeAll
    static public void initHistoryManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        Task task = new Task("Name task", "Description task");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history, "История не пустая.");
    }
}
