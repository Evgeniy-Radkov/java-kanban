
package http;

import com.google.gson.Gson;
import model.Epic;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class EpicsEndpointTest extends AbstractHttpTest {
    private static final Gson GSON = HttpTaskServer.getGson();

    @Test
    void shouldCreateAndDeleteEpic() throws Exception {
        Epic epic = new Epic("Epic-HTTP", "Description");
        var post = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();
        var resp = client.send(post, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, resp.statusCode());

        int id = manager.getAllEpics().get(0).getId();

        var del = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics?id=" + id))
                .DELETE()
                .build();
        resp = client.send(del, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
    }
}
