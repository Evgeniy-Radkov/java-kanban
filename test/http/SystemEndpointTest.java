package http;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemEndpointTest extends AbstractHttpTest {

    @Test
    void historyShouldReturn200() throws Exception {
        var resp = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:8080/history"))
                        .GET().build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, resp.statusCode());
    }

    @Test
    void prioritizedShouldReturn200() throws Exception {
        var resp = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:8080/prioritized"))
                        .GET().build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, resp.statusCode());
    }
}
