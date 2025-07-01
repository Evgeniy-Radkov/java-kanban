package http;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryEndpointTest extends AbstractHttpTest {

    @Test
    void historyReturnsViewedTasks() throws Exception {
        Task task = new Task("View", "task", Status.NEW);
        manager.createTask(task);
        manager.getTaskById(task.getId());

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, resp.statusCode());
        assertTrue(resp.body().contains("\"id\":" + task.getId()));
    }
}
