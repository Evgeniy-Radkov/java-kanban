package server.handler;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import manager.TaskManager;

import java.io.IOException;
import java.net.URI;

public class SubtasksOfEpicHandler extends BaseHttpHandler {

    public SubtasksOfEpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendEmpty(exchange, 405);
                return;
            }

            URI uri = exchange.getRequestURI();
            String query = uri.getQuery();

            if (query == null || !query.startsWith("id=")) {
                sendText(exchange, 400, "{\"error\":\"parameter id expected\"}");
                return;
            }

            String idValue = query.substring(3);
            if (idValue.isBlank()) {
                sendText(exchange, 400, "{\"error\":\"id is empty\"}");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idValue);
            } catch (NumberFormatException e) {
                sendText(exchange, 400, "{\"error\":\"id must be integer\"}");
                return;
            }

            sendText(exchange, 200, gson.toJson(manager.getSubtasksOfEpic(id)));

        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (Exception e) {
            sendText(exchange, 500, "{\"error\":\"internal server error\"}");
        }
    }
}
