package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    private TaskManager manager;
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void managersShouldReturnNonNullTaskManager() {
        assertNotNull(manager);
    }

    @Test
    public void managersShouldReturnNonNullHistoryManager() {
        assertNotNull(historyManager);
    }
}
