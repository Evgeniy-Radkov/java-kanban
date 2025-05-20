package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
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
    public void createAndFindSubtask() {
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
    public void testEpicDoesNotContainDeletedSubtaskId() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", Status.NEW, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.deleteSubtaskById(subtask1.getId());

        Epic updateEpic = manager.getEpicById(epic.getId());

        List<Integer> subtaskIds = updateEpic.getSubtaskIds();
        assertEquals(1, subtaskIds.size());
        assertTrue(subtaskIds.contains(subtask2.getId()));
        assertFalse(subtaskIds.contains(subtask1.getId()));
    }

    @Test
    public void testTaskChangesSavedInManager() {
        Task task = new Task("Задача 1", "Описание 1", Status.NEW);
        manager.createTask(task);

        Task actual = manager.getTaskById(task.getId());
        actual.setTitle("Task 1");
        actual.setDescription("Description 1");
        actual.setStatus(Status.IN_PROGRESS);
        /*  В текущей реализации экземпляры задач можно изменить извне через сеттеры
        Возможное решение — возвращать копии задач, чтобы избежать изменения задачи напрямую,
        то есть вносить изменения через updateTask()
         */

        Task fromManager = manager.getTaskById(task.getId());

        assertEquals("Task 1", fromManager.getTitle());
        assertEquals("Description 1", fromManager.getDescription());
        assertEquals(Status.IN_PROGRESS, fromManager.getStatus());
    }
}
