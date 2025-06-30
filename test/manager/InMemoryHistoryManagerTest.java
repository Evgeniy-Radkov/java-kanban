package manager;

import exception.NotFoundException;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void testRepeatViewOfTaskStoredOnce() {
        Task task = new Task("Задача 1", "Описание 1", Status.NEW);
        manager.createTask(task);

        manager.getTaskById(task.getId());
        manager.getTaskById(task.getId());

        List<Task> history = manager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void testTaskRemovedFromHistoryWhenDeleted() {
        Task task = new Task("Задача 1", "Описание 1", Status.NEW);
        manager.createTask(task);

        manager.getTaskById(task.getId());
        manager.deleteTaskById(task.getId());

        List<Task> history = manager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    public void testTasksAppearInHistoryInViewedOrder() {
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", Status.NEW);
        Task task3 = new Task("Задача 3", "Описание 3", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        manager.getTaskById(task2.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task3.getId());

        List<Task> history = manager.getHistory();

        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
        assertEquals(task3, history.get(2));
    }

    @Test
    void removeFromHistoryVariants() {
        Task t1 = new Task("t1", "d1", Status.NEW);
        Task t2 = new Task("t2", "d2", Status.NEW);
        Task t3 = new Task("t3", "d3", Status.NEW);
        manager.createTask(t1);
        manager.createTask(t2);
        manager.createTask(t3);


        manager.getTaskById(t1.getId());
        manager.getTaskById(t2.getId());
        manager.getTaskById(t3.getId());

        manager.deleteTaskById(t1.getId());
        assertFalse(manager.getHistory().contains(t1));


        assertThrows(NotFoundException.class,
                () -> manager.getTaskById(t1.getId()));


        manager.getTaskById(t2.getId());
        manager.getTaskById(t3.getId());

        manager.deleteTaskById(t2.getId());
        assertFalse(manager.getHistory().contains(t2));
        assertThrows(NotFoundException.class,
                () -> manager.getTaskById(t2.getId()));

        manager.deleteTaskById(t3.getId());
        assertFalse(manager.getHistory().contains(t3));
        assertThrows(NotFoundException.class,
                () -> manager.getTaskById(t3.getId()));
    }
}
