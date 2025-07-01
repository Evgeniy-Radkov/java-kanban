package http;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class TasksEndpointTest extends AbstractHttpTest {

    @Test
    void postCreatesTask() throws Exception {
        Task task = new Task("task1", "desc1", Status.NEW);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(task)))
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, resp.statusCode());
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    void getReturnsConcreteTask() throws Exception {
        Task task = new Task("task2", "desc2", Status.NEW);
        manager.createTask(task);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=" + task.getId()))
                .GET().build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());

        Task fromApi = GSON.fromJson(resp.body(), Task.class);
        assertEquals(task.getTitle(), fromApi.getTitle());
    }

    @Test
    void deleteRemovesTask() throws Exception {
        Task task = new Task("task3", "desc3", Status.NEW);
        manager.createTask(task);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=" + task.getId()))
                .DELETE().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, resp.statusCode());
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void getUnknownTaskReturns404() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=123"))
                .GET().build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, resp.statusCode());
    }

    @Test
    void deleteUnknownTaskReturns404() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=123"))
                .DELETE().build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, resp.statusCode());
    }

}
