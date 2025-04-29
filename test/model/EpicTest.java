package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {

    @Test
    public void epicsEqualsById() {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2);
    }
}
