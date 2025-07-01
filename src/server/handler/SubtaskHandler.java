package server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exception.HasIntersectionException;
import exception.NotFoundException;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler {

    public  SubtaskHandler(TaskManager manager, Gson gson) {
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
        if (isEpicPath(exchange)) {
            Integer epicId = extractId(exchange);
            if (epicId == null) {
                sendText(exchange, 400, "{\"error\":\"parameter id expected\"}");
                return;
            }
            try {
                sendText(exchange, 200, gson.toJson(manager.getSubtasksOfEpic(epicId)));
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
            return;
        }

        Integer id = extractId(exchange);
        if (id == null) {
            sendText(exchange, 200, gson.toJson(manager.getAllSubtasks()));
            return;
        }
        try {
            sendText(exchange, 200, gson.toJson(manager.getSubtaskById(id)));
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

        Subtask subtask;
        try {
            subtask = gson.fromJson(body, Subtask.class);
        } catch (JsonSyntaxException e) {
            sendText(exchange, 400, "{\"error\":\"invalid JSON\"}");
            return;
        }

        try {
            if (subtask.getId() <= 0) {
                manager.createSubtask(subtask);
            } else {
                manager.updateSubtask(subtask);
            }
            sendEmpty(exchange, 201);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (HasIntersectionException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        Integer id = extractId(exchange);
        if (id == null) {
            manager.clearAllSubtasks();
            sendEmpty(exchange, 200);
            return;
        }
        try {
            manager.deleteSubtaskById(id);
            sendEmpty(exchange, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private boolean isEpicPath(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().endsWith("/subtasks/epic");
    }
}
