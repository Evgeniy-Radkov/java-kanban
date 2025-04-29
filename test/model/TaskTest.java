package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    public void tasksEqualsById() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        Task task2 = new Task("Задача2", "Описание2", Status.NEW);
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2);
    }
}
