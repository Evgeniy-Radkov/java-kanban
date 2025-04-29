package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {

    @Test
    public void subtasksEqualsById() {
        Subtask subtask1 = new Subtask("Подзадача1", "Описание1", Status.NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание2", Status.NEW, 1);
        subtask1.setId(1);
        subtask2.setId(1);
        assertEquals(subtask1, subtask2);
    }
}
