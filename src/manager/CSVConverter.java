package manager;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CSVConverter {

    public static String taskToString(Task task) {
        int id = task.getId();
        TaskType type;
        String name = task.getTitle();
        Status status = task.getStatus();
        String description = task.getDescription();
        String epicId = "";
        if (task.getType() == TaskType.EPIC) {
            type = TaskType.EPIC;
        } else if (task.getType() == TaskType.SUBTASK) {
            type = TaskType.SUBTASK;
            epicId = String.valueOf(((Subtask) task).getEpicId());
        } else {
            type = TaskType.TASK;
        }

        String startTime;
        if (task.getStartTime() != null) {
            startTime = task.getStartTime().toString();
        } else {
            startTime = "null";
        }

        long duration;
        if (task.getDuration() != null) {
            duration = task.getDuration().toMinutes();
        } else {
            duration = 0;
        }

        return id + "," + type + "," + name + "," + status + "," + description + "," + epicId
                + "," + startTime + "," + duration;
    }

    public static String historyToString(HistoryManager historyManager) {
        List<String> history = new ArrayList<>();
        for (Task task : historyManager.getHistory()) {
            history.add(String.valueOf(task.getId()));
        }
        return String.join(",", history);
    }

    public static Task taskFromString(String line) {
        String[] split = line.split(",");
        int id = Integer.parseInt(split[0]);
        TaskType type = TaskType.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                if (!split[6].equals("null")) {
                    task.setStartTime(LocalDateTime.parse(split[6]));
                }
                task.setDuration(Duration.ofMinutes(Long.parseLong(split[7])));
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(split[5]);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                if (!split[6].equals("null")) {
                    subtask.setStartTime(LocalDateTime.parse(split[6]));
                }
                subtask.setDuration(Duration.ofMinutes(Long.parseLong(split[7])));
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public static List<Integer> historyFromString(String line) {
        if (line == null || line.isBlank()) {
            return new ArrayList<>();
        }
        List<Integer> history = new ArrayList<>();
        String[] split = line.split(",");
        for (String parts : split) {
            history.add(Integer.parseInt(parts));
        }
        return history;
    }
}
