package http;

import com.google.gson.Gson;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TasksEndpointTest extends AbstractHttpTest {
    private static final Gson GSON = HttpTaskServer.getGson();

    @Test
    void shouldReturn200OnEmptyGet() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        var resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
        assertEquals("[]", resp.body());
    }

    @Test
    void shouldCreateTaskAndAppearInManager() throws Exception {
        Task task = new Task("Test", "From HTTP", Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());

        String json = GSON.toJson(task);

        var post = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        var resp = client.send(post, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, resp.statusCode());

        assertEquals(1, manager.getAllTasks().size());
        assertEquals("Test", manager.getAllTasks().get(0).getTitle());
    }

    @Test
    void shouldReturn404ForUnknownId() throws Exception {
        var req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=999"))
                .GET()
                .build();
        var resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, resp.statusCode());
    }
}
