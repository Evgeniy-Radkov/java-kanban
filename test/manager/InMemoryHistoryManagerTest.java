package manager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {

    private TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    public void testAddTaskToHistory() {
        Task task = new Task("Задача1", "Описание1", Status.NEW);
        manager.createTask(task);

        manager.getTaskById(task.getId());

        List<Task> history = manager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }
}
