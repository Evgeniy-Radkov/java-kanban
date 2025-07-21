package manager;

import exception.HasIntersectionException;
import exception.NotFoundException;
import model.Epic;
import model.Task;
import model.Subtask;
import model.Status;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int nextId = 1;

    protected final Comparator<Task> taskComparator = Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo))
            .thenComparing(Task::getId);
    protected final Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    private int generateId() {
        return nextId++;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
        prioritizedTasks.removeIf(t -> !(t instanceof Subtask));
    }

    @Override
    public void clearAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeIf(t -> t instanceof Subtask);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        epics.values().forEach(epic -> {
            epic.clearSubtasks();
            updateEpicStatus(epic);
            updateEpicTimeFields(epic);
        });
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Task id " + id + " not found");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Epic id " + id + " not found");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Subtask id " + id + " not found");
        }
        historyManager.add(subtask);

        return subtask;
    }

    @Override
    public void createTask(Task task) {
        if (task.getStartTime() != null && hasIntersection(task)) {
            throw new HasIntersectionException("Task intersects with existing tasks");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new NotFoundException("Epic id " + subtask.getEpicId() + " not found");
        }
        if (subtask.getStartTime() != null && hasIntersection(subtask)) {
            throw new HasIntersectionException("Subtask intersects with existing tasks");
        }

        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        if (subtask.getStartTime() != null) prioritizedTasks.add(subtask);

        updateEpicStatus(epic);
        updateEpicTimeFields(epic);
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new NotFoundException("Task id " + task.getId() + " not found");
        }
        if (task.getStartTime() != null && hasIntersection(task)) {
            throw new HasIntersectionException("Task intersects with existing tasks");
        }

        Task old = tasks.get(task.getId());
        if (old.getStartTime() != null) prioritizedTasks.remove(old);

        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic stored = epics.get(epic.getId());
        if (stored == null) {
            throw new NotFoundException("Epic id " + epic.getId() + " not found");
        }
        stored.setTitle(epic.getTitle());
        stored.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new NotFoundException("Subtask id " + subtask.getId() + " not found");
        }
        if (subtask.getStartTime() != null && hasIntersection(subtask)) {
            throw new HasIntersectionException("Subtask intersects with existing tasks");
        }

        Subtask old = subtasks.get(subtask.getId());
        if (old.getStartTime() != null) prioritizedTasks.remove(old);

        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) prioritizedTasks.add(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {                                       // ← добавь проверку
            throw new NotFoundException("Epic id " + subtask.getEpicId() + " not found");
        }
        updateEpicStatus(epic);
        updateEpicTimeFields(epic);
    }

    @Override
    public void deleteTaskById(int id) {
        Task removed = tasks.remove(id);
        if (removed == null) {
            throw new NotFoundException("Task id " + id + " not found");
        }
        if (removed.getStartTime() != null) prioritizedTasks.remove(removed);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            throw new NotFoundException("Epic id " + id + " not found");
        }
        for (int subId : epic.getSubtaskIds()) {
            Subtask st = subtasks.remove(subId);
            if (st != null && st.getStartTime() != null) prioritizedTasks.remove(st);
            historyManager.remove(subId);
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask st = subtasks.remove(id);
        if (st == null) {
            throw new NotFoundException("Subtask id " + id + " not found");
        }
        if (st.getStartTime() != null) prioritizedTasks.remove(st);

        Epic epic = epics.get(st.getEpicId());
        epic.removeSubtaskId(id);
        updateEpicStatus(epic);
        updateEpicTimeFields(epic);

        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Epic id " + epicId + " not found");
        }
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int id : subtaskIds) {
            Status status = subtasks.get(id).getStatus();
            if (status != Status.NEW) {
                allNew = false;
            }
            if (status != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

     void updateEpicTimeFields(Epic epic) {
        List<Subtask> subtaskList = subtasks.values().stream()
                .filter(s -> s.getEpicId() == epic.getId())
                .collect(Collectors.toList());

        LocalDateTime startTime = subtaskList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime endTime = subtaskList.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        long totalMinutes = subtaskList.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .mapToLong(Duration::toMinutes)
                .sum();

        Duration duration;
        if (totalMinutes > 0) {
            duration = Duration.ofMinutes(totalMinutes);
        } else {
            duration = null;
        }

        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    private boolean isTimeOverLapping(Task a, Task b) {
        LocalDateTime startA = a.getStartTime();
        LocalDateTime endA = a.getEndTime();
        LocalDateTime startB = b.getStartTime();
        LocalDateTime endB = b.getEndTime();

        if (startA == null || startB == null || endA == null || endB == null) {
            return false;
        }

        return  startA.isBefore(endB) && startB.isBefore(endA);
    }

    private boolean hasIntersection(Task newTask) {
        for (Task existing : prioritizedTasks) {
            if (existing.getId() == newTask.getId()) {
                continue;
            }
            if (isTimeOverLapping(existing, newTask)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

}
