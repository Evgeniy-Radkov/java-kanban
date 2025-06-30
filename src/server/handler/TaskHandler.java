package server.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exception.HasIntersectionException;
import exception.NotFoundException;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET": {
                    URI uri = exchange.getRequestURI();
                    String query = uri.getQuery();

                    if (query == null) {
                        List<Task> list = manager.getAllTasks();
                        sendText(exchange, 200, gson.toJson(list));
                        break;
                    }

                    if (!query.startsWith("id=")) {
                        sendText(exchange, 400, "{\"error\":\"parameter id expected\"}");
                        break;
                    }

                    String idValue = query.substring(3);
                    if (idValue.isBlank()) {
                        sendText(exchange, 400, "{\"error\":\"id is empty\"}");
                        break;
                    }

                    int id;
                    try {
                        id = Integer.parseInt(idValue);
                    } catch (NumberFormatException e) {
                        sendText(exchange, 400, "{\"error\":\"id must be integer\"}");
                        break;
                    }

                    Task task = manager.getTaskById(id);
                    if (task == null) {
                        sendNotFound(exchange, "Задача с id " + id + " не найдена");
                        break;
                    }

                    sendText(exchange, 200, gson.toJson(task));
                    break;
                }
                case "POST": {
                    String body = readBody(exchange);
                    if (body.isBlank()) {
                        sendText(exchange, 400, "{\"error\":\"request body is empty\"}");
                        break;
                    }

                    Task task;
                    try {
                        task = gson.fromJson(body, Task.class);
                    } catch (JsonSyntaxException e) {
                        sendText(exchange, 400, "{\"error\":\"invalid JSON format\"}");
                        break;
                    }

                    try {
                        if (task.getId() == 0 || manager.getTaskById(task.getId()) == null) {
                            manager.createTask(task);
                        } else {
                            manager.updateTask(task);
                        }

                        sendEmpty(exchange, 201);

                    } catch (HasIntersectionException e) {
                        sendHasInteractions(exchange, e.getMessage());

                    } catch (Exception e) {
                        sendText(exchange, 500, "{\"error\":\"internal server error\"}");
                    }
                    break;
                }
                case "DELETE": {
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        manager.clearAllTasks();
                        sendEmpty(exchange, 200);
                    } else {
                        if (!query.startsWith("id=")) {
                            sendText(exchange, 400, "{\"error\":\"parameter id expected\"}");
                            break;
                        }

                        String idValue = query.substring(3);
                        if (idValue.isBlank()) {
                            sendText(exchange, 400, "{\"error\":\"id is empty\"}");
                            break;
                        }

                        int id;
                        try {
                            id = Integer.parseInt(idValue);
                        } catch (NumberFormatException e) {
                            sendText(exchange, 400, "{\"error\":\"id must be integer\"}");
                            break;
                        }

                        if (manager.getTaskById(id) == null) {
                            sendNotFound(exchange, "Задача с id " + id + " не найдена");
                        } else {
                            manager.deleteTaskById(id);
                            sendEmpty(exchange, 200);
                        }
                    }
                    break;
                }
                default:
                    sendEmpty(exchange, 405);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (Exception e) {
            sendText(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
}
