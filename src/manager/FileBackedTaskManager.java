package manager;

import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public FileBackedTaskManager() {
        this(new File("data" + File.separator + "kanban-data.csv"));
    }

    public static void main(String[] args) {

        File file = new File("kanban-data.csv");


        FileBackedTaskManager manager = new FileBackedTaskManager();

        // Обычные задачи
        Task t1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        manager.createTask(t1);

        Task t2 = new Task("Задача2", "Описание задачи2", Status.NEW);
        manager.createTask(t2);

        // Эпик
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        manager.createEpic(epic1);

        // Подзадачи для этого эпика
        Subtask st1 = new Subtask("Подзадача1", "Описание подзадачи1", Status.NEW, epic1.getId());
        manager.createSubtask(st1);

        Subtask st2 = new Subtask("Подзадача2", "Описание подзадачи2", Status.NEW, epic1.getId());
        manager.createSubtask(st2);

        // посмотрим задачи в каком-то порядке, чтобы наполнилась история
        manager.getTaskById(t2.getId());
        manager.getTaskById(t1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(st2.getId());
        manager.getSubtaskById(st1.getId());

        // Выводим состояние до перезапуска
        System.out.println("=== Состояние менеджера ДО перезапуска ===");
        System.out.println("Задачи:");
        manager.getAllTasks().forEach(System.out::println);
        System.out.println("Эпики:");
        manager.getAllEpics().forEach(System.out::println);
        System.out.println("Подзадачи:");
        manager.getAllSubtasks().forEach(System.out::println);
        System.out.println("История просмотров (id):");
        manager.getHistory().forEach(task -> System.out.println("  " + task.getId()));

        // Создаём новый менеджер из того же файла
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(new FileBackedTaskManager().file);

        // Выводим состояние нового менеджера после загрузки
        System.out.println("\n=== Состояние менеджера ПОСЛЕ загрузки ===");
        System.out.println("Задачи:");
        manager2.getAllTasks().forEach(System.out::println);
        System.out.println("Эпики:");
        manager2.getAllEpics().forEach(System.out::println);
        System.out.println("Подзадачи:");
        manager2.getAllSubtasks().forEach(System.out::println);
        System.out.println("История просмотров (id):");
        manager2.getHistory().forEach(task -> System.out.println("  " + task.getId()));

    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxId = 0;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine();
            String line;
            List<String> lines = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }

            if (lines.isEmpty()) {
                return manager;
            }

            for (int i = 0; i < lines.size() - 1; i++) {
                Task task = CSVConverter.taskFromString(lines.get(i));

                if (task.getId() > maxId) {
                    maxId = task.getId();
                }

                if (task.getType() == TaskType.EPIC) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }
            }

            manager.nextId = maxId + 1;

            String historyLine = lines.get(lines.size() - 1);
            List<Integer> history = CSVConverter.historyFromString(historyLine);
            for (int id : history) {
                if (manager.tasks.containsKey(id)) {
                    manager.getTaskById(id);
                } else if (manager.epics.containsKey(id)) {
                    manager.getEpicById(id);
                } else {
                    manager.getSubtaskById(id);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }
        return manager;
    }


    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("id,type,name,status,description,epic");
            bufferedWriter.newLine();

            for (Task task : getAllTasks()) {
                bufferedWriter.write(CSVConverter.taskToString(task));
                bufferedWriter.newLine();
            }
            for (Epic epic : getAllEpics()) {
                bufferedWriter.write(CSVConverter.taskToString(epic));
                bufferedWriter.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                bufferedWriter.write(CSVConverter.taskToString(subtask));
                bufferedWriter.newLine();
            }

            bufferedWriter.newLine();
            String historyLine = CSVConverter.historyToString(historyManager);
            bufferedWriter.write(historyLine);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }


    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

}
