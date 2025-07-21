package http;

import model.Epic;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class EpicsEndpointTest extends AbstractHttpTest {

    @Test
    void postCreatesEpic() throws Exception {
        Epic epic = new Epic("Epic1", "Desc1");

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, resp.statusCode());
        assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    void getReturnsEpic() throws Exception {
        Epic epic = new Epic("Epic2", "Desc2");
        manager.createEpic(epic);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics?id=" + epic.getId()))
                .GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, resp.statusCode());
        Epic fromApi = GSON.fromJson(resp.body(), Epic.class);
        assertEquals(epic.getTitle(), fromApi.getTitle());
    }

    @Test
    void deleteEpic() throws Exception {
        Epic epic = new Epic("Epic3", "Desc3");
        manager.createEpic(epic);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics?id=" + epic.getId()))
                .DELETE().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, resp.statusCode());
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    void getUnknownEpicReturns404() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics?id=123"))
                .GET().build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, resp.statusCode());
    }

    @Test
    void deleteUnknownEpicReturns404() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics?id=123"))
                .DELETE().build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, resp.statusCode());
    }

}
