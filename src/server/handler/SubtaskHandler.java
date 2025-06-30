package server.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exception.HasIntersectionException;
import exception.NotFoundException;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.net.URI;

public class SubtaskHandler extends BaseHttpHandler {

    public  SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET": {
                    URI uri   = exchange.getRequestURI();
                    String q  = uri.getQuery();

                    if (q == null) {
                        sendText(exchange, 200, gson.toJson(manager.getAllSubtasks()));
                        break;
                    }
                    if (!q.startsWith("id=")) {
                        sendText(exchange, 400, "{\"error\":\"parameter id expected\"}");
                        break;
                    }

                    String idVal = q.substring(3);
                    if (idVal.isBlank()) {
                        sendText(exchange, 400, "{\"error\":\"id is empty\"}");
                        break;
                    }

                    int id;
                    try {
                        id = Integer.parseInt(idVal);
                    } catch (NumberFormatException e) {
                        sendText(exchange, 400, "{\"error\":\"id must be integer\"}");
                        break;
                    }

                    try {
                        sendText(exchange, 200, gson.toJson(manager.getSubtaskById(id)));
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                    break;
                }
                case "POST": {
                    String body = readBody(exchange);
                    if (body.isBlank()) {
                        sendText(exchange, 400, "{\"error\":\"request body is empty\"}");
                        break;
                    }

                    Subtask subtask;
                    try {
                        subtask = gson.fromJson(body, Subtask.class);
                    } catch (JsonSyntaxException e) {
                        sendText(exchange, 400, "{\"error\":\"invalid JSON format\"}");
                        break;
                    }

                    try {
                        if (subtask.getId() == 0) {
                            manager.createSubtask(subtask);
                        } else {
                            manager.updateSubtask(subtask);
                        }
                        sendEmpty(exchange, 201);
                        break;                       // ← завершаем
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                        break;
                    } catch (HasIntersectionException e) {
                        sendHasInteractions(exchange, e.getMessage());
                        break;
                    } catch (Exception e) {
                        sendText(exchange, 500, "{\"error\":\"internal server error\"}");
                        break;
                    }
                }
                case "DELETE": {
                    String q = exchange.getRequestURI().getQuery();

                    if (q == null) {
                        manager.clearAllSubtasks();
                        sendEmpty(exchange, 200);
                        break;
                    }
                    if (!q.startsWith("id=")) {
                        sendText(exchange, 400, "{\"error\":\"parameter id expected\"}");
                        break;
                    }

                    String idVal = q.substring(3);
                    if (idVal.isBlank()) {
                        sendText(exchange, 400, "{\"error\":\"id is empty\"}");
                        break;
                    }

                    int id;
                    try {
                        id = Integer.parseInt(idVal);
                    } catch (NumberFormatException e) {
                        sendText(exchange, 400, "{\"error\":\"id must be integer\"}");
                        break;
                    }

                    try {
                        manager.deleteSubtaskById(id);
                        sendEmpty(exchange, 200);
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                    break;
                }
                default:
                    sendEmpty(exchange, 405);
            }
        } catch (Exception e) {
            sendText(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
}
