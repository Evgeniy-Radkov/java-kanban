public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

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
    }
}

