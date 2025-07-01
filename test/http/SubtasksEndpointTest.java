package http;

import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksEndpointTest extends AbstractHttpTest {

    @Test
    void postCreatesSubtask() throws Exception {
        Epic epic = new Epic("Epic1", "DescEpic1");
        manager.createEpic(epic);

        Subtask st = new Subtask("Sub1", "DescSub1", Status.NEW, epic.getId());

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(st)))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, resp.statusCode());
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @Test
    void listSubtasksOfEpic() throws Exception {
        Epic epic = new Epic("Epic2", "DescEpic2");
        manager.createEpic(epic);
        manager.createSubtask(new Subtask("Sub2", "DescSub2", Status.NEW, epic.getId()));

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/epic?id=" + epic.getId()))
                .GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, resp.statusCode());
        Subtask[] arr = GSON.fromJson(resp.body(), Subtask[].class);
        assertEquals(1, arr.length);
    }

    @Test
    void deleteSubtask() throws Exception {
        Epic epic = new Epic("Epic3", "DescEpic3");
        manager.createEpic(epic);
        Subtask st = new Subtask("Sub3", "DescSub3", Status.NEW, epic.getId());
        manager.createSubtask(st);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks?id=" + st.getId()))
                .DELETE().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, resp.statusCode());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void getUnknownSubtaskReturns404() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks?id=123"))
                .GET().build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, resp.statusCode());
    }

    @Test
    void deleteUnknownSubtaskReturns404() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks?id=123"))
                .DELETE().build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, resp.statusCode());
    }

}
