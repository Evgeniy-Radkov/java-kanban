package model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "model.Subtask{" + "id=" + id + ", title='" + title + '\'' +
                ", description='" + description + '\'' + ", status=" + status +
                ", epicId=" + epicId + '}';
    }
}
