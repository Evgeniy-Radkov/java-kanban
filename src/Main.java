import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпик 1");
        manager.createEpic(epic1);

        Subtask sub1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getId());
        Subtask sub2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epic1.getId());
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        Epic epic2 = new Epic("Эпик 2", "Описание эпик 2");
        manager.createEpic(epic2);
        Subtask sub3 = new Subtask("Подзадача 3", "Описание подзадачи 3", Status.DONE, epic2.getId());
        manager.createSubtask(sub3);

        System.out.println("Задачи:");
        System.out.println(manager.getAllTasks());

        System.out.println("\nЭпики:");
        System.out.println(manager.getAllEpics());

        System.out.println("\nПодзадачи:");
        System.out.println(manager.getAllSubtasks());

        // Обновление статуса подзадач
        sub1.setStatus(Status.DONE);
        manager.updateSubtask(sub1);

        sub2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(sub2);

        System.out.println("\nПосле обновления статусов:");
        System.out.println(manager.getEpicById(epic1.getId()));
        System.out.println(manager.getEpicById(epic2.getId()));

        // Удаление задачи и эпика
        manager.deleteTaskById(task1.getId());
        manager.deleteEpicById(epic2.getId());

        System.out.println("\nПосле удаления:");
        System.out.println("Оставшиеся задачи:");
        System.out.println(manager.getAllTasks());

        System.out.println("Оставшиеся эпики:");
        System.out.println(manager.getAllEpics());

        System.out.println("Оставшиеся подзадачи:");
        System.out.println(manager.getAllSubtasks());


        // ------------------ РАБОТА С ИСТОРИЕЙ ------------------

        // Просматриваем задачи и подзадачи в разном порядке
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(sub1.getId());
        manager.getSubtaskById(sub2.getId());
        manager.getSubtaskById(sub3.getId());
        manager.getEpicById(epic2.getId());

        // Выводим историю после просмотров
        System.out.println("\nИстория просмотров после обращений:");
        for (Task t : manager.getHistory()) {
            System.out.println(t);
        }

        // Удаляем задачу, которая есть в истории
        manager.deleteTaskById(task2.getId());

        // Проверяем, что удалённая задача исчезла из истории
        System.out.println("\nИстория после удаления task2:");
        for (Task t : manager.getHistory()) {
            System.out.println(t);
        }

        // Удаляем эпик с подзадачами
        manager.deleteEpicById(epic1.getId());

        // Проверяем, что эпик и его подзадачи исчезли из истории
        System.out.println("\nИстория после удаления epic1 (и его подзадач):");
        for (Task t : manager.getHistory()) {
            System.out.println(t);
        }

    }
}

