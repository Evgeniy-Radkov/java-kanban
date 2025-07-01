package server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;

public class EpicHandler extends  BaseHttpHandler {

    public EpicHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (isMethod(exchange, "GET")) {
                handleGet(exchange);
            } else if (isMethod(exchange, "POST")) {
                handlePost(exchange);
            } else if (isMethod(exchange, "DELETE")) {
                handleDelete(exchange);
            } else {
                sendEmpty(exchange, 405);
            }
        } catch (Exception e) {
            sendText(exchange, 500, "{\"error\":\"internal server error\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        Integer id = extractId(exchange);
        if (id == null) {
            sendText(exchange, 200, gson.toJson(manager.getAllEpics()));
            return;
        }
        try {
            sendText(exchange, 200, gson.toJson(manager.getEpicById(id)));
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        if (body.isBlank()) {
            sendText(exchange, 400, "{\"error\":\"request body is empty\"}");
            return;
        }

        Epic epic;
        try {
            epic = gson.fromJson(body, Epic.class);
        } catch (JsonSyntaxException e) {
            sendText(exchange, 400, "{\"error\":\"invalid JSON\"}");
            return;
        }

        if (epic.getId() <= 0) {
            manager.createEpic(epic);
        } else {
            manager.updateEpic(epic);
        }
        sendEmpty(exchange, 201);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        Integer id = extractId(exchange);
        if (id == null) {
            manager.clearAllEpics();
            sendEmpty(exchange, 200);
            return;
        }
        try {
            manager.deleteEpicById(id);
            sendEmpty(exchange, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}
