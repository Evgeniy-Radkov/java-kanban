package server.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exception.HasIntersectionException;
import exception.NotFoundException;
import manager.TaskManager;
import model.Epic;
import model.Task;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class EpicHandler extends  BaseHttpHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET": {
                    URI uri = exchange.getRequestURI();
                    String q = uri.getQuery();

                    if (q == null) {
                        sendText(exchange, 200, gson.toJson(manager.getAllEpics()));
                        break;
                    }
                    if (!q.startsWith("id=")) {
                        sendText(exchange, 400, "{\"error\":\"parameter id expected\"}");
                        break;
                    }

                    String idPart = q.substring(3);
                    if (idPart.isBlank()) {
                        sendText(exchange, 400, "{\"error\":\"id is empty\"}");
                        break;
                    }

                    int id;
                    try {
                        id = Integer.parseInt(idPart);
                    } catch (NumberFormatException e) {
                        sendText(exchange, 400, "{\"error\":\"id must be integer\"}");
                        break;
                    }

                    try {
                        sendText(exchange, 200, gson.toJson(manager.getEpicById(id)));
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

                    Epic epic;
                    try {
                        epic = gson.fromJson(body, Epic.class);
                    } catch (JsonSyntaxException e) {
                        sendText(exchange, 400, "{\"error\":\"invalid JSON format\"}");
                        break;
                    }

                    try {
                        if (epic.getId() == 0) {
                            manager.createEpic(epic);
                        } else {
                            manager.updateEpic(epic);
                        }
                        sendEmpty(exchange, 201);
                        break;
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                        break;
                    } catch (Exception e) {
                        sendText(exchange, 500, "{\"error\":\"internal server error\"}");
                        break;
                    }
                }
                case "DELETE": {
                    String q = exchange.getRequestURI().getQuery();

                    if (q == null) {
                        manager.clearAllEpics();
                        sendEmpty(exchange, 200);
                        break;
                    }
                    if (!q.startsWith("id=")) {
                        sendText(exchange, 400, "{\"error\":\"parameter id expected\"}");
                        break;
                    }

                    String idPart = q.substring(3);
                    if (idPart.isBlank()) {
                        sendText(exchange, 400, "{\"error\":\"id is empty\"}");
                        break;
                    }

                    int id;
                    try {
                        id = Integer.parseInt(idPart);
                    } catch (NumberFormatException e) {
                        sendText(exchange, 400, "{\"error\":\"id must be integer\"}");
                        break;
                    }

                    try {
                        manager.deleteEpicById(id);
                        sendEmpty(exchange, 200);
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    } catch (Exception e) {
                        sendText(exchange, 500, "{\"error\":\"internal server error\"}");
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
