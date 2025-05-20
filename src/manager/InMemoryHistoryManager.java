package manager;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private final Map<Integer, Node> nodeMap = new HashMap<>();

    private void linkLast(Task task) {
        Node newNode = new Node(task, tail, null);

        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    private void removeNode(Node node) {
        Node prev = node.previous;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        }

        if (next != null) {
            next.previous = prev;
        }

        if (node == head) {
            head = next;
        }

        if (node == tail) {
            tail = prev;
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
        }

        linkLast(task);

        nodeMap.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            removeNode(nodeMap.get(id));
            nodeMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;

        while (current != null) {
            Task task = current.task;
            history.add(task);
            current = current.next;
        }
        return history;
    }

    private static class Node {
        Task task;
        Node previous;
        Node next;

        Node(Task task, Node previous, Node next) {
            this.task = task;
            this.previous = previous;
            this.next = next;
        }


    }
}
