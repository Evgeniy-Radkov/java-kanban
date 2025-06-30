package server;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import server.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final TaskManager manager;
    private static final Gson GSON = BaseHttpHandler.buildGson();

    private static final HttpHandler STUB = exchange -> {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");

        exchange.sendResponseHeaders(200, -1);
        exchange.close();
    };

    public HttpTaskServer(TaskManager externalManager) throws IOException {
        this.manager = externalManager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        registerContexts();
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public void start() {
        httpServer.start();
        System.out.println("Сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
    }

    public void registerContexts() {
        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
        httpServer.createContext("/subtasks/epic", new SubtasksOfEpicHandler(manager));
    }

    public static Gson getGson() {
        return GSON;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }
}
