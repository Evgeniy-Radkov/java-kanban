package http;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PrioritizedEndpointTest extends AbstractHttpTest {

    @Test
    void prioritizedReturnsSorted() throws Exception {
        Task early = new Task("Early", "T", Status.NEW);
        early.setDuration(Duration.ofMinutes(15));
        early.setStartTime(LocalDateTime.now().plusMinutes(5));

        Task late = new Task("Late", "T", Status.NEW);
        late.setDuration(Duration.ofMinutes(15));
        late.setStartTime(LocalDateTime.now().plusMinutes(30));

        manager.createTask(late);   // сначала более поздняя
        manager.createTask(early);  // потом ранняя

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, resp.statusCode());

        Task[] arr = GSON.fromJson(resp.body(), Task[].class);
        assertEquals(2, arr.length);
        assertEquals("Early", arr[0].getTitle());   // должна быть первой
    }
}
