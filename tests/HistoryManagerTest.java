import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.yandex.controllers.HistoryManager;
import ru.yandex.controllers.Managers;
import ru.yandex.task.Task;

import java.util.ArrayList;
import java.util.List;

public class HistoryManagerTest {
    private static HistoryManager historyManager;

    @BeforeEach
    public void initHistoryManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        Task task = new Task("Name task", "Description task");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history, "История не пустая.");
        Assertions.assertEquals("Name task", history.getFirst().getName(), "История не пустая.");
    }

    @Test
    void addMaxHistory() {
        List<Task> history = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            history.add(new Task("Name task # " + (i + 1) + ".", "Description task# " + (i + 1) + "."));
            historyManager.add(history.get(i));
        }

        List<Task> historyReturn = historyManager.getHistory();
        Assertions.assertTrue(history.containsAll(historyReturn)
                && historyReturn.containsAll(history), "Не влезло 10 записей в историю");


        history.clear();
        Task task = new Task("Name task", "Description task");
        for (int i = 0; i < 10; i++) {
            historyManager.add(task);
            history.add(task);
        }

        historyReturn = historyManager.getHistory();
        Assertions.assertTrue(history.containsAll(historyReturn)
                && historyReturn.containsAll(history), "Не переписывает историю");
    }

}
