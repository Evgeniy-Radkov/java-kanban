package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @Override
    protected TaskManager createManager() {
        return Managers.getDefault();
    }

    @Test
    public void subtaskCannotBeOwnEpic() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", Status.NEW, 111);
        manager.createSubtask(subtask);

        assertTrue(manager.getAllSubtasks().isEmpty());

    }

    @Test
    public void subtaskCannotBeSelfEpic() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", Status.NEW, 111);

        int before = manager.getAllSubtasks().size();

        manager.createSubtask(subtask);

        int after = manager.getAllSubtasks().size();

        assertEquals(before, after, "Подзадача с некорректным epicId не должна добавляться");
    }

    @Test
    void epicTimeFieldsCalculatedCorrectly() {
        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);

        Subtask s1 = new Subtask("S1", "Desc", Status.NEW, epic.getId());
        s1.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        s1.setDuration(Duration.ofMinutes(30));

        Subtask s2 = new Subtask("S2", "Desc", Status.NEW, epic.getId());
        s2.setStartTime(LocalDateTime.of(2023, 1, 1, 11, 0));
        s2.setDuration(Duration.ofMinutes(90));

        manager.createSubtask(s1);
        manager.createSubtask(s2);

        Epic updated = manager.getEpicById(epic.getId());

        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 0), updated.getStartTime());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 30), updated.getEndTime());
        assertEquals(Duration.ofMinutes(120), updated.getDuration());
    }

    @Test
    void prioritizedTasksSortedByStartTime() {
        Task t1 = new Task("задача1", "описание1", Status.NEW);
        t1.setStartTime(LocalDateTime.of(2023, 1, 1, 9, 0));
        t1.setDuration(Duration.ofMinutes(60));
        manager.createTask(t1);

        Task t2 = new Task("задача2", "описание2", Status.NEW);
        t2.setStartTime(LocalDateTime.of(2023, 1, 1, 8, 0));
        t2.setDuration(Duration.ofMinutes(60));
        manager.createTask(t2);

        List<Task> prioritized = manager.getPrioritizedTasks();

        assertEquals(t2, prioritized.get(0));
        assertEquals(t1, prioritized.get(1));
    }

    @Test
    void shouldThrowIfTasksOverlap() {
        Task t1 = new Task("задача1", "описание1", Status.NEW);
        t1.setStartTime(LocalDateTime.of(2023, 1, 1, 9, 0));
        t1.setDuration(Duration.ofMinutes(60));
        manager.createTask(t1);

        Task t2 = new Task("задача2", "описание2", Status.NEW);
        t2.setStartTime(LocalDateTime.of(2023, 1, 1, 9, 30));
        t2.setDuration(Duration.ofMinutes(60));

        assertThrows(RuntimeException.class, () -> manager.createTask(t2));
    }
}
