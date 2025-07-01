package server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import server.gson.DurationAdapter;
import server.gson.LocalDateTimeAdapter;
import server.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final TaskManager manager;
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

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
        SubtaskHandler subHandler = new SubtaskHandler(manager, GSON);

        httpServer.createContext("/tasks", new TaskHandler(manager, GSON));
        httpServer.createContext("/subtasks", subHandler);
        httpServer.createContext("/subtasks/epic", subHandler);
        httpServer.createContext("/epics", new EpicHandler(manager, GSON));
        httpServer.createContext("/history", new HistoryHandler(manager, GSON));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager, GSON));
    }

    public static Gson getGson() {
        return GSON;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }
}
