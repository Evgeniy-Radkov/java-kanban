package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendEmpty(exchange, 405);
                return;
            }
            sendText(exchange, 200, gson.toJson(manager.getPrioritizedTasks()));
        } catch (Exception e) {
            sendText(exchange, 500, "{\"error\":\"internal server error\"}");
        }
    }
}
