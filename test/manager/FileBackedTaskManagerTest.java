package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private File tempFile;

    @BeforeEach
    public void beforeEach() throws IOException {
        tempFile = File.createTempFile("java-kanban", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    public void shouldSaveAndLoadEmptyManager() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getAllTasks().isEmpty(), "Список задач должен быть пустым");
        assertTrue(loaded.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(loaded.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустым");
        assertTrue(loaded.getHistory().isEmpty(), "История должна быть пустой");
    }

    @Test
    public void shouldSaveAndLoadSingleTaskFromFile() {
        Task task = new Task("задача", "описание", Status.NEW);
        manager.createTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loaded.getAllTasks();
        assertEquals(1, tasks.size(), "Должна быть одна задача");

        Task loadedTask = tasks.get(0);
        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStatus(), loadedTask.getStatus());
        assertEquals(task.getId(), loadedTask.getId());
    }

    @Test
    public void shouldSaveAndLoadMultipleTasksFromFile() {
        Task task1 = new Task("задача1", "описание1", Status.NEW);
        Task task2 = new Task("задача2", "описание2", Status.NEW);
        Task task3 = new Task("задача3", "описание3", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loaded.getAllTasks();
        assertEquals(3, tasks.size(), "Должно быть три задачи");

        Task loadedTask1 = tasks.get(0);
        assertEquals(task1.getTitle(), loadedTask1.getTitle());
        assertEquals(task1.getDescription(), loadedTask1.getDescription());
        assertEquals(task1.getStatus(), loadedTask1.getStatus());
        assertEquals(task1.getId(), loadedTask1.getId());

        Task loadedTask2 = tasks.get(1);
        assertEquals(task2.getTitle(), loadedTask2.getTitle());
        assertEquals(task2.getDescription(), loadedTask2.getDescription());
        assertEquals(task2.getStatus(), loadedTask2.getStatus());
        assertEquals(task2.getId(), loadedTask2.getId());

        Task loadedTask3 = tasks.get(2);
        assertEquals(task3.getTitle(), loadedTask3.getTitle());
        assertEquals(task3.getDescription(), loadedTask3.getDescription());
        assertEquals(task3.getStatus(), loadedTask3.getStatus());
        assertEquals(task3.getId(), loadedTask3.getId());
    }

    @Test
    public void shouldSaveAndLoadEpicWithSubtasksFromFile() {
        Epic epic = new Epic("эпик", "описание эпика");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("подзадача1", "описание подзадачи1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("подзадача2", "описание подзадачи2", Status.DONE, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        List<Epic> epics = loaded.getAllEpics();
        assertEquals(1, epics.size(), "Должен быть один эпик");

        List<Subtask> subtasks = loaded.getAllSubtasks();
        assertEquals(2, subtasks.size(), "Должно быть две подзадачи");

        Epic loadedEpic = epics.get(0);
        assertEquals(epic.getTitle(), loadedEpic.getTitle());
        assertEquals(epic.getDescription(), loadedEpic.getDescription());
        assertEquals(epic.getId(), loadedEpic.getId());

        Subtask loadedSubtask1 = subtasks.get(0);
        assertEquals(subtask1.getTitle(), loadedSubtask1.getTitle());
        assertEquals(subtask1.getDescription(), loadedSubtask1.getDescription());
        assertEquals(subtask1.getStatus(), loadedSubtask1.getStatus());
        assertEquals(subtask1.getEpicId(), loadedSubtask1.getEpicId());

        Subtask loadedSubtask2 = subtasks.get(1);
        assertEquals(subtask2.getTitle(), loadedSubtask2.getTitle());
        assertEquals(subtask2.getDescription(), loadedSubtask2.getDescription());
        assertEquals(subtask2.getStatus(), loadedSubtask2.getStatus());
        assertEquals(subtask2.getEpicId(), loadedSubtask2.getEpicId());

    }
}
