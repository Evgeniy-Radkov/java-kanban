package http;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.http.HttpClient;

public abstract class AbstractHttpTest {
    protected TaskManager manager;
    protected HttpTaskServer server;
    protected HttpClient client;

    @BeforeEach
    void beforeEach() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void afterEach() {
        server.stop();
    }
}
