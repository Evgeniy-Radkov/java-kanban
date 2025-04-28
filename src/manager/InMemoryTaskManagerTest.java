package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class InMemoryTaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    public void tasksEqualsById() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        Task task2 = new Task("Задача2", "Описание2", Status.NEW);
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2);
    }

    @Test
    public void epicsEqualsById() {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2);
    }

    @Test
    public void subtasksEqualsById() {
        Subtask subtask1 = new Subtask("Подзадача1", "Описание1", Status.NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание2", Status.NEW, 1);
        subtask1.setId(1);
        subtask2.setId(1);
        assertEquals(subtask1, subtask2);
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
    public void managersShouldReturnNonNullTaskManager() {
        assertNotNull(manager);
    }

    @Test
    public void managersShouldReturnNonNullHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

    @Test
    public void createAndFindTask() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        manager.createTask(task);

        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void createAndFindEpic() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);

        assertEquals(epic, manager.getEpicById(epic.getId()));
    }

    @Test
    public void  createAndFindSubtask() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void testUniqueIdsForTasks() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        manager.createTask(task1);

        Task task2 = new Task("Задача2", "Описание2", Status.NEW);
        manager.createTask(task2);

        assertNotEquals(task1.getId(), task2.getId());
        assertEquals(task1, manager.getTaskById(task1.getId()));
        assertEquals(task2, manager.getTaskById(task2.getId()));
    }

    @Test
    public void testSaveTaskFields() {
        Task task = new Task("Задача1", "Описание1", Status.NEW);

        String taskTitle = task.getTitle();
        String taskDescription = task.getDescription();
        Status taskStatus = task.getStatus();


        manager.createTask(task);

        int taskId = task.getId();

        Task actual = manager.getTaskById(task.getId());

        assertEquals(taskTitle,actual.getTitle());
        assertEquals(taskDescription, actual.getDescription());
        assertEquals(taskStatus, actual.getStatus());
        assertEquals(taskId, actual.getId());
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