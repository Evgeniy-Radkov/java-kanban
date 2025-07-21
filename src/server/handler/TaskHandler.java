package server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import manager.TaskManager;
import model.Task;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager manager, Gson gson) {
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
            sendText(exchange, 200, gson.toJson(manager.getAllTasks()));
            return;
        }
        try {
            sendText(exchange, 200, gson.toJson(manager.getTaskById(id)));
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

        Task task;
        try {
            task = gson.fromJson(body, Task.class);
        } catch (JsonSyntaxException e) {
            sendText(exchange, 400, "{\"error\":\"invalid JSON\"}");
            return;
        }

        if (task.getId() <= 0) {
            manager.createTask(task);
        } else {
            manager.updateTask(task);
        }
        sendEmpty(exchange, 201);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        Integer id = extractId(exchange);
        if (id == null) {
            manager.clearAllTasks();
            sendEmpty(exchange, 200);
            return;
        }
        try {
            manager.deleteTaskById(id);
            sendEmpty(exchange, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}
