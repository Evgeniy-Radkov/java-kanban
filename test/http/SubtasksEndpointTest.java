package http;

import com.google.gson.Gson;
import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksEndpointTest extends AbstractHttpTest {
    private static final Gson GSON = HttpTaskServer.getGson();

    @Test
    void shouldReturnSubtasksOfEpic() throws Exception {
        Epic epic = new Epic("Epic", "For subtasks");
        manager.createEpic(epic);

        Subtask st = new Subtask("Sub", "HTTP", Status.NEW, epic.getId());
        st.setDuration(Duration.ofMinutes(10));
        st.setStartTime(LocalDateTime.now());
        manager.createSubtask(st);

        var req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/epic?id=" + epic.getId()))
                .GET().build();
        var resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, resp.statusCode());
        assertTrue(resp.body().contains("HTTP"));
    }

    @Test
    void shouldReturn404IfEpicNotFound() throws Exception {
        var req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/epic?id=999"))
                .GET().build();
        var resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, resp.statusCode());
    }
}
